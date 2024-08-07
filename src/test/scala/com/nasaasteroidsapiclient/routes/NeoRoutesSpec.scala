package com.nasaasteroidsapiclient.routes

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.nasaasteroidsapiclient.base.TestData._
import com.nasaasteroidsapiclient.model.{NeoData, NeosDataList}
import com.nasaasteroidsapiclient.routes.models.{GetNeosFeedResponse, GetNeosFeedSingleElement, GetSingleNeoResponse}
import com.nasaasteroidsapiclient.services.NeoService
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.{Method, Request, Status, Uri}
import org.mockito.ArgumentMatchersSugar.{any, eqTo}
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.MockitoSugar.{mock, reset, verify}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class NeoRoutesSpec extends AsyncWordSpec with AsyncIOSpec with Matchers with BeforeAndAfterEach {

  private val neoService = mock[NeoService]

  private val neoRoutes = new NeoRoutes(neoService).allRoutes.orNotFound

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(neoService)
  }

  "NeoRoutes on GET /neo/feed" should {

    "call NeoService providing start and end dates" when {

      val startDateStr = "2024-08-06"
      val endDateStr = "2024-08-10"

      "both are NOT provided in the request" in {
        neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

        val uri = Uri.unsafeFromString("neo/feed")
        val request = Request[IO](method = Method.GET, uri = uri)

        for {
          _ <- neoRoutes.run(request)
          _ = verify(neoService).getNeosFeed(eqTo(None), eqTo(None))
        } yield ()
      }

      "only startDate is provided in the request" in {
        neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

        val uri = Uri
          .unsafeFromString("neo/feed")
          .withQueryParam("start_date", startDateStr)
        val request = Request[IO](method = Method.GET, uri = uri)

        for {
          _ <- neoRoutes.run(request)
          _ = verify(neoService).getNeosFeed(eqTo(Some(startDateStr)), eqTo(None))
        } yield ()
      }

      "only endDate is provided in the request" in {
        neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

        val uri = Uri
          .unsafeFromString("neo/feed")
          .withQueryParam("end_date", endDateStr)
        val request = Request[IO](method = Method.GET, uri = uri)

        for {
          _ <- neoRoutes.run(request)
          _ = verify(neoService).getNeosFeed(eqTo(None), eqTo(Some(endDateStr)))
        } yield ()
      }

      "both are provided in the request" in {
        neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

        val uri = Uri
          .unsafeFromString("neo/feed")
          .withQueryParam("start_date", startDateStr)
          .withQueryParam("end_date", endDateStr)
        val request = Request[IO](method = Method.GET, uri = uri)

        for {
          _ <- neoRoutes.run(request)
          _ = verify(neoService).getNeosFeed(eqTo(Some(startDateStr)), eqTo(Some(endDateStr)))
        } yield ()
      }
    }

    "return Ok with value returned by NeoService" in {
      val neosDataList = List(neoDataHeader_1, neoDataHeader_2, neoDataHeader_3)
      neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(neosDataList))

      val expectedResponse = GetNeosFeedResponse(neosDataList.map(GetNeosFeedSingleElement.from))

      val uri = Uri.unsafeFromString("neo/feed")
      val request = Request[IO](method = Method.GET, uri = uri)

      for {
        response <- neoRoutes.run(request)

        _ = response.status shouldBe Status.Ok
        _ <- response
          .as[GetNeosFeedResponse]
          .asserting(_ shouldBe expectedResponse)
      } yield ()
    }

    "return Internal Server Error when NeoService returns failed IO" in {
      val testException = new RuntimeException("Test Exception")
      neoService.getNeosFeed(any[Option[String]], any[Option[String]]) returns IO.raiseError(testException)

      val uri = Uri.unsafeFromString("neo/feed")
      val request = Request[IO](method = Method.GET, uri = uri)

      for {
        response <- neoRoutes.run(request)
        _ = response.status shouldBe Status.InternalServerError
      } yield ()
    }
  }

  "NeoRoutes on GET /neo/:neoReferenceId" should {

    val neoReferenceId = "2523661"
    val uri = Uri.unsafeFromString(s"neo/$neoReferenceId")
    val request = Request[IO](method = Method.GET, uri = uri)

    "call NeoService providing neoReferenceId" in {
      neoService.getSingleNeo(any[String]) returns IO.pure(None)

      for {
        _ <- neoRoutes.run(request)
        _ = verify(neoService).getSingleNeo(eqTo(neoReferenceId))
      } yield ()
    }

    "return Ok with value returned by NeoService when NeoService returns non-empty Option" in {
      val neoData = NeoData(header = neoDataHeader_1, data = neoJson_1)
      neoService.getSingleNeo(any[String]) returns IO.pure(Some(neoData))

      val expectedResponse = GetSingleNeoResponse(
        header = GetNeosFeedSingleElement(neoReferenceId = neoReferenceId, name = neoDataHeader_1.name),
        data = neoJson_1
      )

      for {
        response <- neoRoutes.run(request)

        _ = response.status shouldBe Status.Ok
        _ <- response
          .as[Option[GetSingleNeoResponse]]
          .asserting(_ shouldBe Some(expectedResponse))
      } yield ()
    }

    "return Not Found when NeoService returns empty Option" in {
      neoService.getSingleNeo(any[String]) returns IO.pure(None)

      for {
        response <- neoRoutes.run(request)
        _ = response.status shouldBe Status.NotFound
      } yield ()
    }

    "return Internal Server Error when NeoService returns failed IO" in {
      val testException = new RuntimeException("Test Exception")
      neoService.getSingleNeo(any[String]) returns IO.raiseError(testException)

      for {
        response <- neoRoutes.run(request)
        _ = response.status shouldBe Status.InternalServerError
      } yield ()
    }
  }

}
