package com.nasaasteroidsapiclient.connectors

import cats.effect.IO
import com.nasaasteroidsapiclient.config.NasaNeoApiConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector._
import com.nasaasteroidsapiclient.model.{NeoData, NeosDataList}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.{Response, Status}
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class NasaNeoApiConnector(config: NasaNeoApiConfig, httpClient: Client[IO]) {

  private val logger: StructuredLogger[IO] = Slf4jLogger.getLoggerFromClass(getClass)

  def fetchNeos(startDate: Option[String], endDate: Option[String]): IO[NeosDataList] = {
    val uri = config.baseUri
      .addPath(FeedEndpoint)
      .withQueryParam(QueryParamApiKey, config.apiKey)
      .withOptionQueryParam(QueryParamStartDate, startDate)
      .withOptionQueryParam(QueryParamEndDate, endDate)

    httpClient.get(uri) {
      case r @ Response(Status.Ok, _, _, _, _) =>
        r.as[NeosDataList]

      case r: Response[IO] =>
        extractErrorResponse(r).flatMap { responseText =>
          logger.warn(s"Call to obtain NEOs feed failed. Reason: $responseText")
        } >> IO.raiseError(NeosFeedException())
    }
  }

  def fetchSingleNeo(neoReferenceId: String): IO[Option[NeoData]] = {
    val uri = config.baseUri
      .addPath(lookupEndpoint(neoReferenceId))
      .withQueryParam(QueryParamApiKey, config.apiKey)

    httpClient.get(uri) {
      case r @ Response(Status.Ok, _, _, _, _) =>
        r.as[NeoData].map(Option(_))

      case _ @Response(Status.NotFound, _, _, _, _) =>
        IO.pure(Option.empty)

      case r: Response[IO] =>
        extractErrorResponse(r).flatMap { responseText =>
          logger.warn(s"Call to obtain NEO data failed for NEO reference ID: [$neoReferenceId]. Reason: $responseText")
        } >> IO.raiseError(NeoFetchException(neoReferenceId))
    }
  }

  private def extractErrorResponse(response: Response[IO]): IO[String] =
    response.body
      .through(fs2.text.utf8.decode)
      .compile
      .toList
      .map(_.mkString)
}

object NasaNeoApiConnector {
  private val FeedEndpoint = "feed"
  private def lookupEndpoint(neoReferenceId: String) = s"neo/$neoReferenceId"

  private val QueryParamApiKey = "api_key"
  private val QueryParamStartDate = "start_date"
  private val QueryParamEndDate = "end_date"

  case class NeosFeedException() extends RuntimeException("Call to obtain NEOs feed failed.")
  case class NeoFetchException(neoReferenceId: String)
      extends RuntimeException(s"Call to obtain NEO data failed for NEO reference ID: $neoReferenceId.")
}
