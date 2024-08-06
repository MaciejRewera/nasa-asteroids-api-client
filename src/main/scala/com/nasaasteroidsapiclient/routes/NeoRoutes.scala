package com.nasaasteroidsapiclient.routes

import cats.effect.IO
import cats.implicits.catsSyntaxEitherId
import com.nasaasteroidsapiclient.routes.definitions.NeoEndpoints
import com.nasaasteroidsapiclient.routes.models.GetNeosFeedResponse
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

  val allRoutes: HttpRoutes[IO] = getNeosFeedRoutes
}
