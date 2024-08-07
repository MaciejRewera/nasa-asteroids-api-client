package com.nasaasteroidsapiclient.connectors

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO, Resource}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.nasaasteroidsapiclient.base.TestData.JsonStrings._
import com.nasaasteroidsapiclient.base.TestData._
import com.nasaasteroidsapiclient.base.WireMockIntegrationSpec
import com.nasaasteroidsapiclient.config.NasaNeoApiConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector.{NeoFetchException, NeosFeedException}
import com.nasaasteroidsapiclient.model.{NeoData, NeoDataHeader}
import io.circe.parser
import org.http4s.blaze.client.BlazeClientBuilder
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class NasaNeoApiConnectorSpec
    extends AsyncWordSpec
    with AsyncIOSpec
    with Matchers
    with EitherValues
    with WireMockIntegrationSpec {

  private val ApiKey = "test-api-key-123"

  private val nasaNeoApiConfig = NasaNeoApiConfig(
    baseUri = wireMockUri,
    apiKey = ApiKey
  )

  private val nasaNeoApiConnectorRes: Resource[IO, NasaNeoApiConnector] =
    BlazeClientBuilder[IO].resource.map(new NasaNeoApiConnector(nasaNeoApiConfig, _))

  private val feedUrl = "/feed"
  private def lookupUrl(neoReferenceId: String) = s"/neo/$neoReferenceId"

  private def stubUri(uri: String)(responseStatus: Int, responseBody: String): StubMapping =
    stubFor(
      get(urlPathEqualTo(uri))
        .withQueryParam("api_key", equalTo(ApiKey))
        .willReturn(
          aResponse()
            .withStatus(responseStatus)
            .withBody(responseBody)
        )
    )

  private val feedResponseJsonEmpty =
    s"""{
       |    "links": {
       |		"next": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-13&end_date=2024-08-20&detailed=false&api_key=DEMO_KEY",
       |		"previous": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-07-30&end_date=2024-08-06&detailed=false&api_key=DEMO_KEY",
       |		"self": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-06&end_date=2024-08-13&detailed=false&api_key=DEMO_KEY"
       |	},
       |	"element_count": 0,
       |	"near_earth_objects": {}
       |}
       |""".stripMargin

  private val feedResponseJsonSingleNeo =
    s"""{
       |    "links": {
       |		"next": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-13&end_date=2024-08-20&detailed=false&api_key=DEMO_KEY",
       |		"previous": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-07-30&end_date=2024-08-06&detailed=false&api_key=DEMO_KEY",
       |		"self": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-06&end_date=2024-08-13&detailed=false&api_key=DEMO_KEY"
       |	},
       |	"element_count": 1,
       |	"near_earth_objects": {
       |      "2024-08-06": [
       |        $neoJsonStr_1
       |      ]
       |    }
       |}
       |""".stripMargin

  private val feedResponseJsonMultipleNeos =
    s"""{
       |    "links": {
       |		"next": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-13&end_date=2024-08-20&detailed=false&api_key=DEMO_KEY",
       |		"previous": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-07-30&end_date=2024-08-06&detailed=false&api_key=DEMO_KEY",
       |		"self": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-06&end_date=2024-08-13&detailed=false&api_key=DEMO_KEY"
       |	},
       |	"element_count": 3,
       |	"near_earth_objects": {
       |      "2024-08-06": [
       |        $neoJsonStr_1,
       |        $neoJsonStr_2
       |      ],
       |      "2024-08-10": [
       |        $neoJsonStr_3
       |      ]
       |    }
       |}
       |""".stripMargin

  "NasaNeoApiConnector on fetchAsteroidsList" should {

    "call Nasa NEO API, providing mandatory API key query parameter" in {
      stubUri(feedUrl)(200, feedResponseJsonEmpty)

      for {
        _ <- nasaNeoApiConnectorRes.use(_.fetchNeos(None, None))
        _ = verify(1, getRequestedFor(urlPathEqualTo(feedUrl)).withQueryParam("api_key", equalTo(ApiKey)))
      } yield ()
    }

    "call Nasa NEO API, providing optional query parameters" when {

      val startDateStr = "2024-08-06"
      val endDateStr = "2024-08-10"

      "provided with start date" in {
        stubUri(feedUrl)(200, feedResponseJsonEmpty)

        for {
          _ <- nasaNeoApiConnectorRes.use(_.fetchNeos(Some(startDateStr), None))
          _ = verify(
            1,
            getRequestedFor(urlPathEqualTo(feedUrl))
              .withQueryParam("api_key", equalTo(ApiKey))
              .withQueryParam("start_date", equalTo(startDateStr))
          )
        } yield ()
      }

      "provided with end date" in {
        stubUri(feedUrl)(200, feedResponseJsonEmpty)

        for {
          _ <- nasaNeoApiConnectorRes.use(_.fetchNeos(None, Some(endDateStr)))
          _ = verify(
            1,
            getRequestedFor(urlPathEqualTo(feedUrl))
              .withQueryParam("api_key", equalTo(ApiKey))
              .withQueryParam("end_date", equalTo(endDateStr))
          )
        } yield ()
      }

      "provided with both start and end date" in {
        stubUri(feedUrl)(200, feedResponseJsonEmpty)

        for {
          _ <- nasaNeoApiConnectorRes.use(_.fetchNeos(Some(startDateStr), Some(endDateStr)))
          _ = verify(
            1,
            getRequestedFor(urlPathEqualTo(feedUrl))
              .withQueryParam("api_key", equalTo(ApiKey))
              .withQueryParam("start_date", equalTo(startDateStr))
              .withQueryParam("end_date", equalTo(endDateStr))
          )
        } yield ()
      }
    }
  }

  "NasaNeoApiConnector on fetchAsteroidsList" when {

    "Nasa NOE API returns incorrect JSON" should {
      "return failed IO containing exception" in {
        val responseJsonIncorrect =
          s"""{
             |  "near_earth_object": {
             |      "2024-08-06": [
             |        $neoJsonStr_1,
             |        $neoJsonStr_2
             |      ],
             |      "2024-08-10": [
             |        $neoJsonStr_3
             |      ]
             |    }
             |}
             |""".stripMargin

        stubUri(feedUrl)(200, parser.parse(responseJsonIncorrect).value.spaces2)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).attempt.asserting { result =>
          result.isLeft shouldBe true
          result.left.value.getMessage should include(
            s"Invalid message body: Could not decode JSON: ${parser.parse(responseJsonIncorrect).value.spaces2}"
          )
        }
      }
    }

    "Nasa NEO API returns response other than 200 (OK)" should {
      "return failed IO containing NeosFeedException" in {
        stubFor(
          get(urlPathEqualTo(feedUrl))
            .withQueryParam("api_key", equalTo(ApiKey))
            .willReturn(
              aResponse()
                .withStatus(400)
            )
        )

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).attempt.asserting { result =>
          result.isLeft shouldBe true
          result.left.value shouldBe a[NeosFeedException]
          result.left.value.getMessage should include("Call to obtain NEOs feed failed.")
        }
      }
    }

    "Nasa NEO API returns NO NEOs" should {
      "return empty list" in {
        stubUri(feedUrl)(200, feedResponseJsonEmpty)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).asserting(_.neos shouldBe List.empty[NeoDataHeader])
      }
    }

    "Nasa NEO API returns single NEO" should {
      "return this NEO" in {
        stubUri(feedUrl)(200, feedResponseJsonSingleNeo)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).asserting(_.neos shouldBe List(neoDataHeader_1))
      }
    }

    "Nasa NEO API returns multiple NEOs" should {
      "return all these NEOs" in {
        stubUri(feedUrl)(200, feedResponseJsonMultipleNeos)

        nasaNeoApiConnectorRes
          .use(_.fetchNeos(None, None))
          .asserting(_.neos should contain theSameElementsAs List(neoDataHeader_1, neoDataHeader_2, neoDataHeader_3))
      }
    }
  }

  "NasaNeoApiConnector on fetchSingleNeo" should {
    "call Nasa NEO API, providing NEO reference ID and mandatory API key query parameter" in {
      val url = lookupUrl("2523661")
      stubUri(url)(200, neoJsonStr_1)

      for {
        _ <- nasaNeoApiConnectorRes.use(_.fetchSingleNeo("2523661"))
        _ = verify(1, getRequestedFor(urlPathEqualTo(url)).withQueryParam("api_key", equalTo(ApiKey)))
      } yield ()
    }
  }

  "NasaNeoApiConnector on fetchSingleNeo" when {

    val neoReferenceId = "2523661"
    val url = lookupUrl("2523661")

    "Nasa NOE API returns incorrect JSON" should {
      "return failed IO containing exception" in {
        val responseJsonIncorrect =
          s"""{
             |  "id": $neoReferenceId,
             |	"neo_id": "2523661",
             |	"name": "523661 (2012 LF11)"
             |}
             |""".stripMargin

        stubUri(url)(200, parser.parse(responseJsonIncorrect).value.spaces2)

        nasaNeoApiConnectorRes.use(_.fetchSingleNeo(neoReferenceId)).attempt.asserting { result =>
          result.isLeft shouldBe true
          result.left.value.getMessage should include(
            s"Invalid message body: Could not decode JSON: ${parser.parse(responseJsonIncorrect).value.spaces2}"
          )
        }
      }
    }

    "Nasa NEO API returns response other than 200 (OK)" should {
      "return failed IO containing NeoFetchException" in {
        stubFor(
          get(urlPathEqualTo(url))
            .withQueryParam("api_key", equalTo(ApiKey))
            .willReturn(
              aResponse()
                .withStatus(400)
            )
        )

        nasaNeoApiConnectorRes.use(_.fetchSingleNeo(neoReferenceId)).attempt.asserting { result =>
          result.isLeft shouldBe true
          result.left.value shouldBe a[NeoFetchException]
          result.left.value.getMessage should include(
            s"Call to obtain NEO data failed for NEO reference ID: $neoReferenceId."
          )
        }
      }
    }

    "Nasa NEO API returns 404 Not Found (no NEO found)" should {
      "return empty Option" in {
        stubFor(
          get(urlPathEqualTo(url))
            .withQueryParam("api_key", equalTo(ApiKey))
            .willReturn(
              aResponse()
                .withStatus(404)
            )
        )

        nasaNeoApiConnectorRes.use(_.fetchSingleNeo(neoReferenceId)).asserting(_ shouldBe None)
      }
    }

    "Nasa NEO API returns data" should {
      "return Option containing NeoData" in {
        stubUri(url)(200, neoJsonStr_1)

        val expectedNeoData = NeoData(
          header = neoDataHeader_1,
          data = parser.parse(neoJsonStr_1).value
        )

        nasaNeoApiConnectorRes.use(_.fetchSingleNeo(neoReferenceId)).asserting(_ shouldBe Some(expectedNeoData))
      }
    }
  }
}
