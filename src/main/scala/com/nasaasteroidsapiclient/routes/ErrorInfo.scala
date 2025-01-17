package com.nasaasteroidsapiclient.routes

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class ErrorInfo(error: String, errorDetail: Option[String])

object ErrorInfo {
  implicit val codec: Codec[ErrorInfo] = deriveCodec[ErrorInfo]

  object Errors {
    val InternalServerError = "Internal Server Error"
    val BadRequest = "Bad Request"
    val NotFound = "Not Found"
  }

  def internalServerErrorInfo(detail: Option[String] = None): ErrorInfo = ErrorInfo(
    error = Errors.InternalServerError,
    errorDetail = detail
  )

  def badRequestErrorInfo(detail: Option[String] = None): ErrorInfo = ErrorInfo(
    error = Errors.BadRequest,
    errorDetail = detail
  )

  def notFoundErrorInfo(detail: Option[String] = None): ErrorInfo = ErrorInfo(
    error = Errors.NotFound,
    errorDetail = detail
  )
}
