package com.nasaasteroidsapiclient.connectors

import cats.effect.IO
import com.nasaasteroidsapiclient.config.NasaNeoApiConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector.{FeedEndpoint, NeosFeedException, QueryParamApiKey}
import com.nasaasteroidsapiclient.model.NeosDataList
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.{Response, Status}
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class NasaNeoApiConnector(config: NasaNeoApiConfig, httpClient: Client[IO]) {

  private val logger: StructuredLogger[IO] = Slf4jLogger.getLoggerFromClass(getClass)

  def fetchNeos(): IO[NeosDataList] = {
    val uri = config.baseUri
      .addPath(FeedEndpoint)
      .withQueryParam(QueryParamApiKey, config.apiKey)
    
    httpClient.get(uri) {
      case r @ Response(Status.Ok, _, _, _, _) =>
        r.as[NeosDataList]

      case r: Response[IO] =>
        extractErrorResponse(r).flatMap { responseText =>
          logger.warn(s"Call to obtain NEOs feed failed. Reason: $responseText")
        } >> IO.raiseError(NeosFeedException())
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
  val FeedEndpoint = "feed"

  val QueryParamApiKey = "api_key"

  case class NeosFeedException() extends RuntimeException("Call to obtain NEOs feed failed.")
}