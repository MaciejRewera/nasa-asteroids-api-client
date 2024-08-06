package com.nasaasteroidsapiclient.model

import io.circe.{Decoder, HCursor}

case class NeoData(
    neoReferenceId: String,
    name: String
)

object NeoData {

  implicit val decoder: Decoder[NeoData] = (cursor: HCursor) =>
    for {
      ref <- cursor.get[String]("neo_reference_id")
      name <- cursor.get[String]("name")
    } yield NeoData(ref, name)

}
