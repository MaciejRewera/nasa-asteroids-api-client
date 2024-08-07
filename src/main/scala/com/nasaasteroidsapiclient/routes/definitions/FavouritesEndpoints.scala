package com.nasaasteroidsapiclient.routes.definitions

import com.nasaasteroidsapiclient.routes.models.{AddFavouriteRequestResponse, GetAllFavouritesResponse}
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{Endpoint, endpoint, statusCode}

object FavouritesEndpoints {

  val getAllFavouritesEndpoint: Endpoint[Unit, Unit, Unit, (StatusCode, GetAllFavouritesResponse), Any] =
    endpoint.get
      .in("favourites")
      .out(statusCode)
      .out(jsonBody[GetAllFavouritesResponse])

  val postAddFavouriteEndpoint
      : Endpoint[Unit, AddFavouriteRequestResponse, Unit, (StatusCode, AddFavouriteRequestResponse), Any] =
    endpoint.post
      .in("favourites")
      .in(jsonBody[AddFavouriteRequestResponse])
      .out(statusCode)
      .out(jsonBody[AddFavouriteRequestResponse])
}
