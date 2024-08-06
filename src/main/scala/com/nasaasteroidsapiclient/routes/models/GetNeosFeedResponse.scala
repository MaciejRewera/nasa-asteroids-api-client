package com.nasaasteroidsapiclient.routes.models

import com.nasaasteroidsapiclient.model.NeosDataList
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class GetNeosFeedResponse(neos: List[GetNeosFeedSingleElement])

object GetNeosFeedResponse {
  implicit val codec: Codec[GetNeosFeedResponse] = deriveCodec[GetNeosFeedResponse]

  def from(neosDataList: NeosDataList): GetNeosFeedResponse = GetNeosFeedResponse(
    neos = neosDataList.neos.map(GetNeosFeedSingleElement.from)
  )
}
