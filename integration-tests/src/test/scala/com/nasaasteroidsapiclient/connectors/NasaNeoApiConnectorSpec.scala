package com.nasaasteroidsapiclient.connectors

import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.{IO, Resource}
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.nasaasteroidsapiclient.base.IntegrationTestData.JsonStrings.{neoJson_1, neoJson_2, neoJson_3}
import com.nasaasteroidsapiclient.base.IntegrationTestData.{neo_1, neo_2, neo_3}
import com.nasaasteroidsapiclient.base.WireMockIntegrationSpec
import com.nasaasteroidsapiclient.config.NasaNeoApiConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector.NeosFeedException
import com.nasaasteroidsapiclient.model.NeoData
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

  private val responseJsonEmpty =
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

  private val responseJsonSingleNeo =
    s"""{
       |    "links": {
       |		"next": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-13&end_date=2024-08-20&detailed=false&api_key=DEMO_KEY",
       |		"previous": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-07-30&end_date=2024-08-06&detailed=false&api_key=DEMO_KEY",
       |		"self": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-06&end_date=2024-08-13&detailed=false&api_key=DEMO_KEY"
       |	},
       |	"element_count": 1,
       |	"near_earth_objects": {
       |      "2024-08-06": [
       |        $neoJson_1
       |      ]
       |    }
       |}
       |""".stripMargin

  private val responseJsonMultipleNeos =
    s"""{
       |    "links": {
       |		"next": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-13&end_date=2024-08-20&detailed=false&api_key=DEMO_KEY",
       |		"previous": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-07-30&end_date=2024-08-06&detailed=false&api_key=DEMO_KEY",
       |		"self": "http://api.nasa.gov/neo/rest/v1/feed?start_date=2024-08-06&end_date=2024-08-13&detailed=false&api_key=DEMO_KEY"
       |	},
       |	"element_count": 3,
       |	"near_earth_objects": {
       |      "2024-08-06": [
       |        $neoJson_1,
       |        $neoJson_2
       |      ],
       |      "2024-08-10": [
       |        $neoJson_3
       |      ]
       |    }
       |}
       |""".stripMargin

  "NasaNeoApiConnector on fetchAsteroidsList" should {

    "call Nasa NEO API, providing mandatory API key query parameter" in {
      stubUri(feedUrl)(200, responseJsonEmpty)

      for {
        _ <- nasaNeoApiConnectorRes.use(_.fetchNeos(None, None))
        _ = verify(1, getRequestedFor(urlPathEqualTo(feedUrl)).withQueryParam("api_key", equalTo(ApiKey)))
      } yield ()
    }

    "call Nasa NEO API, providing optional query parameters" when {

      val startDateStr = "2024-08-06"
      val endDateStr = "2024-08-10"

      "provided with start date" in {
        stubUri(feedUrl)(200, responseJsonEmpty)

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
        stubUri(feedUrl)(200, responseJsonEmpty)

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
        stubUri(feedUrl)(200, responseJsonEmpty)

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
      "return failed IO containing NeosFeedException" in {
        val responseJsonIncorrect =
          s"""{
             |  "near_earth_object": {
             |      "2024-08-06": [
             |        $neoJson_1,
             |        $neoJson_2
             |      ],
             |      "2024-08-10": [
             |        $neoJson_3
             |      ]
             |    }
             |}
             |""".stripMargin

        stubUri(feedUrl)(400, responseJsonIncorrect)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).attempt.asserting { result =>
          result.isLeft shouldBe true
          result.left.value shouldBe a[NeosFeedException]
          result.left.value.getMessage should include("Call to obtain NEOs feed failed.")
        }
      }
    }

    "Nasa NEO API returns NO NEOs" should {
      "return empty list" in {
        stubUri(feedUrl)(200, responseJsonEmpty)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).asserting(_.neos shouldBe List.empty[NeoData])
      }
    }

    "Nasa NEO API returns single NEO" should {
      "return this NEO" in {
        stubUri(feedUrl)(200, responseJsonSingleNeo)

        nasaNeoApiConnectorRes.use(_.fetchNeos(None, None)).asserting(_.neos shouldBe List(neo_1))
      }
    }

    "Nasa NEO API returns multiple NEOs" should {
      "return all these NEOs" in {
        stubUri(feedUrl)(200, responseJsonMultipleNeos)

        nasaNeoApiConnectorRes
          .use(_.fetchNeos(None, None))
          .asserting(_.neos should contain theSameElementsAs List(neo_1, neo_2, neo_3))
      }
    }
  }

}
