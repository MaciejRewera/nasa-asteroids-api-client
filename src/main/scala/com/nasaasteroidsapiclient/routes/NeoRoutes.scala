package com.nasaasteroidsapiclient.routes

import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toSemigroupKOps}
import com.nasaasteroidsapiclient.routes.definitions.NeoEndpoints
import com.nasaasteroidsapiclient.routes.models.{GetNeosFeedResponse, GetSingleNeoResponse}
import com.nasaasteroidsapiclient.services.NeoService
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.server.http4s.Http4sServerInterpreter

class NeoRoutes(neoService: NeoService) {

  private val serverInterpreter =
    Http4sServerInterpreter(ServerConfiguration.options)

  private val getNeosFeedRoutes: HttpRoutes[IO] =
    serverInterpreter
      .toRoutes(
        NeoEndpoints.neosFeedEndpoint.serverLogic { case (startDateOpt, endDateOpt) =>
          neoService.getNeosFeed(startDateOpt, endDateOpt).map { neosDataList =>
            (StatusCode.Ok -> GetNeosFeedResponse.from(neosDataList)).asRight[Unit]
          }
        }
      )

  private val getSingleNeoRoutes: HttpRoutes[IO] =
    serverInterpreter
      .toRoutes(
        NeoEndpoints.singleNeoFetchEndpoint.serverLogic { neoReferenceId =>
          neoService.getSingleNeo(neoReferenceId).map {
            case Some(neoData) =>
              (StatusCode.Ok -> GetSingleNeoResponse.from(neoData)).asRight

            case None =>
              ErrorInfo.notFoundErrorInfo(Some("No NEO with provided neoReferenceId found.")).asLeft
          }
        }
      )

  val allRoutes: HttpRoutes[IO] = getNeosFeedRoutes <+> getSingleNeoRoutes
}
