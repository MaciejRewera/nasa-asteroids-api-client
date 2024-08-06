package com.nasaasteroidsapiclient.services

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import com.nasaasteroidsapiclient.base.TestData.JsonStrings.neoJson_1
import com.nasaasteroidsapiclient.base.TestData._
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector
import com.nasaasteroidsapiclient.model.{NeoData, NeosDataList}
import org.mockito.ArgumentMatchersSugar.{any, eqTo}
import org.mockito.IdiomaticMockito.StubbingOps
import org.mockito.MockitoSugar.{mock, reset, verify}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class NeoServiceSpec extends AsyncWordSpec with AsyncIOSpec with Matchers with BeforeAndAfterEach {

  private val nasaNeoApiConnector = mock[NasaNeoApiConnector]

  private val neoService = new NeoService(nasaNeoApiConnector)

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(nasaNeoApiConnector)
  }

  "NeoService on getNeosFeed" should {

    val startDate = Some("2024-08-06")
    val endDate = Some("2024-08-10")

    "call NasaNeoApiConnector providing start and end dates" in {
      nasaNeoApiConnector.fetchNeos(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

      for {
        _ <- neoService.getNeosFeed(startDate, endDate)
        _ = verify(nasaNeoApiConnector).fetchNeos(eqTo(startDate), eqTo(endDate))
      } yield ()
    }

    "return empty NeosDataList when NasaNeoApiConnector returns empty NeosDataList" in {
      nasaNeoApiConnector.fetchNeos(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(List.empty))

      neoService.getNeosFeed(startDate, endDate).asserting(_ shouldBe NeosDataList(List.empty))
    }
    "return NeosDataList with elements sorted by name when NasaNeoApiConnector returns non-empty NeosDataList" in {
      val neoHeaders = List(neoDataHeader_1, neoDataHeader_2, neoDataHeader_3)
      nasaNeoApiConnector.fetchNeos(any[Option[String]], any[Option[String]]) returns IO.pure(NeosDataList(neoHeaders))

      neoService.getNeosFeed(startDate, endDate).asserting(_ shouldBe NeosDataList(neoHeaders.sortBy(_.name)))
    }
  }

  "NeoService on getNeosFeed" should {

    val neoReferenceId = "2523661"

    "call NasaNeoApiConnector providing NEO reference ID" in {
      nasaNeoApiConnector.fetchSingleNeo(any[String]) returns IO.pure(Option.empty)

      for {
        _ <- neoService.getSingleNeo(neoReferenceId)
        _ = verify(nasaNeoApiConnector).fetchSingleNeo(eqTo(neoReferenceId))
      } yield ()
    }

    "return empty Option when NasaNeoApiConnector returns empty Option" in {
      nasaNeoApiConnector.fetchSingleNeo(any[String]) returns IO.pure(Option.empty)

      neoService.getSingleNeo(neoReferenceId).asserting(_ shouldBe None)
    }

    "return Option containing NeoData when NasaNeoApiConnector returns non-empty Option" in {
      val neoData = NeoData(header = neoDataHeader_1, data = neoJson_1)
      nasaNeoApiConnector.fetchSingleNeo(any[String]) returns IO.pure(Some(neoData))

      neoService.getSingleNeo(neoReferenceId).asserting(_ shouldBe Some(neoData))
    }
  }
}
