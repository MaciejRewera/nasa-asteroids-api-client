package com.nasaasteroidsapiclient.model

import cats.implicits.toTraverseOps
import io.circe.{Decoder, HCursor}

case class NeosDataList(neos: List[NeoDataHeader])

object NeosDataList {

  implicit val decoder: Decoder[NeosDataList] = (cursor: HCursor) =>
    cursor.get[Map[String, List[HCursor]]]("near_earth_objects").map(_.values.flatten).flatMap { neos =>
      neos.toList
        .traverse(singleNeo => NeoDataHeader.decoder.apply(singleNeo))
        .map(NeosDataList(_))
    }
}
