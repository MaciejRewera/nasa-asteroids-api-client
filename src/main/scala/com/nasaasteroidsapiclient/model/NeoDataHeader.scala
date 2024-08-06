package com.nasaasteroidsapiclient.model

import io.circe.{Decoder, HCursor}

case class NeoDataHeader(
    neoReferenceId: String,
    name: String
)

object NeoDataHeader {

  implicit val decoder: Decoder[NeoDataHeader] = (cursor: HCursor) =>
    for {
      ref <- cursor.get[String]("neo_reference_id")
      name <- cursor.get[String]("name")
    } yield NeoDataHeader(ref, name)

}
