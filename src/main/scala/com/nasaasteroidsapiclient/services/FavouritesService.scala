package com.nasaasteroidsapiclient.services

import cats.effect.IO
import com.nasaasteroidsapiclient.model.Favourite
import scala.collection.mutable.ListBuffer

class FavouritesService {

  private val favourites = ListBuffer.empty[Favourite]

  def getAllFavourites: IO[List[Favourite]] = IO(favourites.toList)

  def addToFavourites(newFavourite: Favourite): IO[Favourite] =
    IO(favourites.addOne(newFavourite)) >> IO.pure(newFavourite)
}
