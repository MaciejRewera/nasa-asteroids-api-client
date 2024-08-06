package com.nasaasteroidsapiclient.routes.models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GetSingleNeoResponse(
    header: GetNeosFeedSingleElement,
    data: String
)

object GetSingleNeoResponse {
  implicit val codec: Codec[GetSingleNeoResponse] = deriveCodec[GetSingleNeoResponse]
}
