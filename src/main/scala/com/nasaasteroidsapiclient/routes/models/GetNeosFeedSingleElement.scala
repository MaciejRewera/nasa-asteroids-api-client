package com.nasaasteroidsapiclient.routes.models

import com.nasaasteroidsapiclient.model.NeoDataHeader
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GetNeosFeedSingleElement(
    neoReferenceId: String,
    name: String
)

object GetNeosFeedSingleElement {
  implicit val codec: Codec[GetNeosFeedSingleElement] = deriveCodec[GetNeosFeedSingleElement]

  def from(neoDataHeader: NeoDataHeader): GetNeosFeedSingleElement = GetNeosFeedSingleElement(
    neoReferenceId = neoDataHeader.neoReferenceId,
    name = neoDataHeader.name
  )
}
