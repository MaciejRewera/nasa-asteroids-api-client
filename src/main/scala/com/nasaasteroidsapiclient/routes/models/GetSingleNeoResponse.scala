package com.nasaasteroidsapiclient.routes.models

import com.nasaasteroidsapiclient.model.NeoData
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GetSingleNeoResponse(
    header: GetNeosFeedSingleElement,
    data: String
)

object GetSingleNeoResponse {
  implicit val codec: Codec[GetSingleNeoResponse] = deriveCodec[GetSingleNeoResponse]

  def from(neoData: NeoData): GetSingleNeoResponse = GetSingleNeoResponse(
    header = GetNeosFeedSingleElement.from(neoData.header),
    data = neoData.data
  )
}
