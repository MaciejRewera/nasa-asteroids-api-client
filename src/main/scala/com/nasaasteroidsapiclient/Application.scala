package com.nasaasteroidsapiclient

import cats.effect.{IO, IOApp, Resource}
import cats.implicits.{toSemigroupKOps, toShow}
import com.nasaasteroidsapiclient.config.AppConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector
import com.nasaasteroidsapiclient.routes.{FavouritesRoutes, NeoRoutes}
import com.nasaasteroidsapiclient.services.{FavouritesService, NeoService}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.middleware.{Logger => ClientLogger}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.{CORS, Logger => ServerLogger}
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

import scala.concurrent.duration.DurationInt

object Application extends IOApp.Simple {

  private val logger: StructuredLogger[IO] = Slf4jLogger.getLoggerFromClass(getClass)

  override def run: IO[Unit] = {

    val resources = for {
      config <- Resource
        .eval(ConfigSource.default.load[AppConfig] match {
          case Left(failures) => IO.raiseError(new RuntimeException(failures.prettyPrint()))
          case Right(config)  => IO.pure(config)
        })

      httpClient <- BlazeClientBuilder[IO].withConnectTimeout(1.minute).withIdleTimeout(5.minutes).resource
      loggingHttpClient = ClientLogger(logHeaders = false, logBody = true)(httpClient)

    } yield (config, loggingHttpClient)

    resources.use { case (config, httpClient) =>
      for {
        _ <- logger.info(s"Starting nasa-asteroids-api-client service with the following configuration: ${config.show}")

        nasaNeoApiConnector: NasaNeoApiConnector = new NasaNeoApiConnector(config.nasaNeoApi, httpClient)
        neoService: NeoService = new NeoService(nasaNeoApiConnector)
        neoRoutes: HttpRoutes[IO] = new NeoRoutes(neoService).allRoutes

        favouritesService: FavouritesService = new FavouritesService
        favouritesRoutes: HttpRoutes[IO] = new FavouritesRoutes(favouritesService).allRoutes

        httpApp = CORS.policy
          .withAllowOriginAll(neoRoutes <+> favouritesRoutes)
          .orNotFound

        loggingHttpApp = ServerLogger.httpApp(logHeaders = false, logBody = true)(httpApp)

        _ <- buildServerResource(loggingHttpApp, config).useForever
      } yield ()
    }
  }

  private def buildServerResource(httpApp: HttpApp[IO], config: AppConfig): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(config.http.host)
      .withPort(config.http.port)
      .withHttpApp(httpApp)
      .build
}
