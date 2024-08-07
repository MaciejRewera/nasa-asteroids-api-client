package com.nasaasteroidsapiclient.routes

import cats.effect.IO
import cats.implicits.{catsSyntaxEitherId, toSemigroupKOps}
import com.nasaasteroidsapiclient.model.Favourite
import com.nasaasteroidsapiclient.routes.definitions.FavouritesEndpoints
import com.nasaasteroidsapiclient.routes.models.{AddFavouriteRequestResponse, GetAllFavouritesResponse}
import com.nasaasteroidsapiclient.services.FavouritesService
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.server.http4s.Http4sServerInterpreter

class FavouritesRoutes(favouritesService: FavouritesService) {

  private val serverInterpreter =
    Http4sServerInterpreter(ServerConfiguration.options)

  private val getAllFavouritesRoutes: HttpRoutes[IO] =
    serverInterpreter
      .toRoutes(
        FavouritesEndpoints.getAllFavouritesEndpoint.serverLogic { _ =>
          favouritesService.getAllFavourites.map { allFavourites =>
            (StatusCode.Ok -> GetAllFavouritesResponse(allFavourites)).asRight[Unit]
          }
        }
      )

  private val postAddFavouriteRoutes: HttpRoutes[IO] =
    serverInterpreter
      .toRoutes(
        FavouritesEndpoints.postAddFavouriteEndpoint.serverLogic { request =>
          favouritesService.addToFavourites(Favourite.from(request)).map { addedFavourite =>
            (StatusCode.Ok -> AddFavouriteRequestResponse.from(addedFavourite)).asRight[Unit]
          }
        }
      )

  val allRoutes: HttpRoutes[IO] = getAllFavouritesRoutes <+> postAddFavouriteRoutes
}
