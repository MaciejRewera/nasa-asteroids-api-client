package com.nasaasteroidsapiclient

import cats.effect.{IO, IOApp, Resource}
import cats.implicits.toShow
import org.http4s.blaze.client.BlazeClientBuilder
import pureconfig.ConfigSource
import com.nasaasteroidsapiclient.config.AppConfig
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector
import com.nasaasteroidsapiclient.routes.NeoRoutes
import com.nasaasteroidsapiclient.services.NeoService
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.DurationInt

object Application extends IOApp.Simple {

  private val logger: StructuredLogger[IO] = Slf4jLogger.getLoggerFromClass(getClass)

  override def run: IO[Unit] = {

    val resources = for {
      config <- Resource
        .eval(ConfigSource.default.load[AppConfig] match {
          case Left(failures) => IO.raiseError(new RuntimeException(failures.prettyPrint()))
          case Right(config) => IO.pure(config)
        })

      httpClient <- BlazeClientBuilder[IO].withConnectTimeout(1.minute).withIdleTimeout(5.minutes).resource

    } yield (config, httpClient)

    resources.use { case (config, httpClient) =>
      for {
        _ <- logger.info(s"Starting nasa-asteroids-api-client service with the following configuration: ${config.show}")

        nasaNeoApiConnector: NasaNeoApiConnector = new NasaNeoApiConnector(config.nasaNeoApi, httpClient)
        neoService: NeoService = new NeoService(nasaNeoApiConnector)

        neoRoutes = new NeoRoutes(neoService).allRoutes

        httpApp = CORS.policy
          .withAllowOriginAll(neoRoutes)
          .orNotFound

        _ <- buildServerResource(httpApp, config).useForever
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
