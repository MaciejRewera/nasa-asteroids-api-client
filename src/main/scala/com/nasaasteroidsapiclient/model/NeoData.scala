package com.nasaasteroidsapiclient.model

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, HCursor}

case class NeoData(
    header: NeoDataHeader,
    data: String
)

object NeoData {

  implicit val decoder: Decoder[NeoData] = (cursor: HCursor) =>
    NeoDataHeader.decoder.apply(cursor).map { dataHeader =>
      NeoData(
        header = dataHeader,
        data = cursor.top.asJson.spaces2
      )
    }
}
