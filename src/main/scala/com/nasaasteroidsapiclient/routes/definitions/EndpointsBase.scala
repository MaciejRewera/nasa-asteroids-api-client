package com.nasaasteroidsapiclient.routes.definitions

import com.nasaasteroidsapiclient.routes.ErrorInfo
import com.nasaasteroidsapiclient.routes.ErrorInfo.Errors
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{EndpointOutput, oneOfVariantValueMatcher}

object EndpointsBase {

  val errorOutVariantNotFound: EndpointOutput.OneOfVariant[ErrorInfo] =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[ErrorInfo]
    ) { case errorInfo: ErrorInfo => errorInfo.error == Errors.NotFound }

}
