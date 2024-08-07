package com.nasaasteroidsapiclient.routes.models

import com.nasaasteroidsapiclient.model.Favourite
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GetAllFavouritesResponse(favourites: List[Favourite])

object GetAllFavouritesResponse {
  implicit val codec: Codec[GetAllFavouritesResponse] = deriveCodec[GetAllFavouritesResponse]
}
