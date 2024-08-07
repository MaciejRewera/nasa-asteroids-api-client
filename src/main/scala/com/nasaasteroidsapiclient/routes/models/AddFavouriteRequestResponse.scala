package com.nasaasteroidsapiclient.routes.models

import com.nasaasteroidsapiclient.model.Favourite
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class AddFavouriteRequestResponse(
    neoReferenceId: String,
    name: String
)

object AddFavouriteRequestResponse {
  implicit val codec: Codec[AddFavouriteRequestResponse] = deriveCodec[AddFavouriteRequestResponse]

  def from(favourite: Favourite): AddFavouriteRequestResponse =
    AddFavouriteRequestResponse(
      neoReferenceId = favourite.neoReferenceId,
      name = favourite.name
    )
}
