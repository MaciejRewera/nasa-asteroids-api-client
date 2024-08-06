# nasa-asteroids-api-client

This service integrates with NASA Asteroids NeoWs API (https://api.nasa.gov/).

## How to use it?

Run `sbt run` while in the project's main folder.

## How to interact with the service?

This service exposes 2 endpoints:

1.`/neo/feed?start_date=2024-08-01&end_date=2024-08-05`
- Query parameters are optional. The dates range is from the date of the request through to 7 days from that date.

2.`/neo/:neoReferenceId`
- If there is no NEO with given neoReferenceId found, the service returns 404 Not Found.
