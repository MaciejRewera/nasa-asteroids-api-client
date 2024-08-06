package com.nasaasteroidsapiclient.base

import com.nasaasteroidsapiclient.model.NeoDataHeader

object TestData {

  val neoDataHeader_1: NeoDataHeader = NeoDataHeader(neoReferenceId = "2523661", name = "523661 (2012 LF11)")
  val neoDataHeader_2: NeoDataHeader = NeoDataHeader(neoReferenceId = "3275974", name = "(2005 GN22)")
  val neoDataHeader_3: NeoDataHeader = NeoDataHeader(neoReferenceId = "2414772", name = "414772 (2010 OC103)")

  object JsonStrings {

    val neoJson_1: String =
      s"""{
         |	"links": {
         |		"self": "http://api.nasa.gov/neo/rest/v1/neo/2523661?api_key=DEMO_KEY"
         |	},
         |	"id": "2523661",
         |	"neo_reference_id": "2523661",
         |	"name": "523661 (2012 LF11)",
         |	"nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2523661",
         |	"absolute_magnitude_h": 20.81,
         |	"estimated_diameter": {
         |	  "kilometers": {
         |        "estimated_diameter_min": 0.1830437804,
         |        "estimated_diameter_max": 0.4092983358
         |	  },
         |  	  "meters": {
         |        "estimated_diameter_min": 183.0437803683,
         |        "estimated_diameter_max": 409.2983357621
         |	  },
         |	  "miles": {
         |        "estimated_diameter_min": 0.1137380969,
         |        "estimated_diameter_max": 0.2543261162
         |	  },
         |	  "feet": {
         |        "estimated_diameter_min": 600.5373563836,
         |        "estimated_diameter_max": 1342.8423519017
         |	  }
         |	},
         |	"is_potentially_hazardous_asteroid": false,
         |	"close_approach_data": [
         |	  {
         |        "close_approach_date": "2024-08-06",
         |        "close_approach_date_full": "2024-Aug-06 07:48",
         |        "epoch_date_close_approach": 1722930480000,
         |        "relative_velocity": {
         |	      "kilometers_per_second": "10.7085457267",
         |	      "kilometers_per_hour": "38550.7646161211",
         |	      "miles_per_hour": "23953.9459360559"
         |        },
         |        "miss_distance": {
         |	      "astronomical": "0.3889196249",
         |	      "lunar": "151.2897340861",
         |	      "kilometers": "58181547.486238963",
         |	      "miles": "36152337.2017971694"
         |        },
         |        "orbiting_body": "Earth"
         |	  }
         |	],
         |	"is_sentry_object": false
         |}
         |""".stripMargin

    val neoJson_2: String =
      s"""{
         |	"links": {
         |		"self": "http://api.nasa.gov/neo/rest/v1/neo/3275974?api_key=DEMO_KEY"
         |	},
         |	"id": "3275974",
         |	"neo_reference_id": "3275974",
         |	"name": "(2005 GN22)",
         |	"nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=3275974",
         |	"absolute_magnitude_h": 26.5,
         |	"estimated_diameter": {
         |	  "kilometers": {
         |      "estimated_diameter_min": 0.0133215567,
         |      "estimated_diameter_max": 0.0297879063
         |	  },
         |	  "meters": {
         |      "estimated_diameter_min": 13.3215566698,
         |      "estimated_diameter_max": 29.7879062798
         |	  },
         |	  "miles": {
         |      "estimated_diameter_min": 0.008277629,
         |      "estimated_diameter_max": 0.0185093411
         |	  },
         |	  "feet": {
         |      "estimated_diameter_min": 43.7058959846,
         |      "estimated_diameter_max": 97.7293544391
         |	  }
         |	},
         |	"is_potentially_hazardous_asteroid": false,
         |	"close_approach_data": [
         |		{
         |        "close_approach_date": "2024-08-06",
         |        "close_approach_date_full": "2024-Aug-06 19:03",
         |        "epoch_date_close_approach": 1722970980000,
         |        "relative_velocity": {
         |	        "kilometers_per_second": "5.064988896",
         |	        "kilometers_per_hour": "18233.9600255002",
         |	        "miles_per_hour": "11329.8736614004"
         |        },
         |      "miss_distance": {
         |	      "astronomical": "0.3664311166",
         |	      "lunar": "142.5417043574",
         |	      "kilometers": "54817314.545081642",
         |	      "miles": "34061899.7870329796"
         |      },
         |      "orbiting_body": "Earth"
         |	  }
         |	],
         |	"is_sentry_object": false
         |}
         |""".stripMargin

    val neoJson_3: String =
      s"""{
         |   "links": {
         |		"self": "http://api.nasa.gov/neo/rest/v1/neo/2414772?api_key=DEMO_KEY"
         |	},
         |	"id": "2414772",
         |	"neo_reference_id": "2414772",
         |	"name": "414772 (2010 OC103)",
         |	"nasa_jpl_url": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=2414772",
         |	"absolute_magnitude_h": 19.14,
         |	"estimated_diameter": {
         |	  "kilometers": {
         |       "estimated_diameter_min": 0.3949616937,
         |       "estimated_diameter_max": 0.8831611957
         |     },
         |	  "meters": {
         |       "estimated_diameter_min": 394.9616937199,
         |       "estimated_diameter_max": 883.1611956661
         |	  },
         |	  "miles": {
         |       "estimated_diameter_min": 0.2454177426,
         |       "estimated_diameter_max": 0.5487707553
         |	  },
         |	  "feet": {
         |       "estimated_diameter_min": 1295.806123224,
         |       "estimated_diameter_max": 2897.5105771892
         |	  }
         |	},
         |	"is_potentially_hazardous_asteroid": false,
         |	"close_approach_data": [
         |	  {
         |       "close_approach_date": "2024-08-10",
         |       "close_approach_date_full": "2024-Aug-10 21:50",
         |       "epoch_date_close_approach": 1723326600000,
         |       "relative_velocity": {
         |	      "kilometers_per_second": "19.502228227",
                  "kilometers_per_hour": "70208.0216172522",
         |	      "miles_per_hour": "43624.5343210087"
         |       },
         |       "miss_distance": {
         |	      "astronomical": "0.3662953606",
         |	      "lunar": "142.4888952734",
         |	      "kilometers": "54797005.736641922",
         |	      "miles": "34049280.4786336436"
         |       },
         |       "orbiting_body": "Earth"
         |	  }
         |	],
         |	"is_sentry_object": false
         |}
         |""".stripMargin
  }

}
