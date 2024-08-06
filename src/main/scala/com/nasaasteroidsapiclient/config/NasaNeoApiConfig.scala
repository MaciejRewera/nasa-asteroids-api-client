package com.nasaasteroidsapiclient.config

import org.http4s.Uri

case class NasaNeoApiConfig(
    baseUri: Uri,
    apiKey: String
)
