package com.nasaasteroidsapiclient.routes.definitions

import com.nasaasteroidsapiclient.routes.ErrorInfo
import com.nasaasteroidsapiclient.routes.ErrorInfo.Errors
import com.nasaasteroidsapiclient.routes.models.{GetNeosFeedResponse, GetSingleNeoResponse}
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

object NeoEndpoints {

  private val errorOutVariantNotFound: EndpointOutput.OneOfVariant[ErrorInfo] =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[ErrorInfo]
    ) { case errorInfo: ErrorInfo => errorInfo.error == Errors.NotFound }

  val neosFeedEndpoint: Endpoint[Unit, (Option[String], Option[String]), Unit, (StatusCode, GetNeosFeedResponse), Any] =
    endpoint.get
      .in("neo" / "feed")
      .in(query[Option[String]]("start_date"))
      .in(query[Option[String]]("end_date"))
      .out(statusCode)
      .out(jsonBody[GetNeosFeedResponse])

  val singleNeoFetchEndpoint: Endpoint[Unit, String, ErrorInfo, (StatusCode, GetSingleNeoResponse), Any] =
    endpoint.get
      .in("neo" / path[String]("neoReferenceId"))
      .out(statusCode)
      .out(jsonBody[GetSingleNeoResponse])
      .errorOut(oneOf[ErrorInfo](errorOutVariantNotFound))

}
