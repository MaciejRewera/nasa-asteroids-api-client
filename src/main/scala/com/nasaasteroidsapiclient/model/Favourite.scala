package com.nasaasteroidsapiclient.model

import com.nasaasteroidsapiclient.routes.models.AddFavouriteRequestResponse
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Favourite(
    neoReferenceId: String,
    name: String
)

object Favourite {
  implicit val codec: Codec[Favourite] = deriveCodec[Favourite]

  def from(addFavouriteRequestResponse: AddFavouriteRequestResponse): Favourite =
    Favourite(
      neoReferenceId = addFavouriteRequestResponse.neoReferenceId,
      name = addFavouriteRequestResponse.name
    )
}
