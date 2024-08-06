package com.nasaasteroidsapiclient.services

import cats.effect.IO
import com.nasaasteroidsapiclient.connectors.NasaNeoApiConnector
import com.nasaasteroidsapiclient.model.{NeoData, NeosDataList}

class NeoService(nasaNeoApiConnector: NasaNeoApiConnector) {

  def getNeosFeed(startDate: Option[String], endDate: Option[String]): IO[NeosDataList] =
    for {
      neoHeaders <- nasaNeoApiConnector.fetchNeos(startDate, endDate)
      neoHeadersSorted = neoHeaders.neos.sortBy(_.name)
    } yield NeosDataList(neoHeadersSorted)

  def getSingleNeo(neoReferenceId: String): IO[Option[NeoData]] =
    nasaNeoApiConnector.fetchSingleNeo(neoReferenceId)
}
