package com.example.theproj.retrofit

data class AirQualityResponse(
    val `data`: Data,
    val status: String
) {
    data class Data(
        val city: String,
        val country: String,
        val current: Current,
        val location: Location,
        val state: String
    ) {
        data class Current(
            val pollution: Pollution,
            val weather: Weather
        ) {
            data class Pollution(
                val aqicn: Int,
                val aqius: Int,
                val maincn: String,
                val mainus: String,
                val ts: String
            )

            data class Weather(
                val hu: Int,    //습기
                val ic: String, //날씨 아이콘
                val pr: Int,    //대기압
                val tp: Int,    //온도
                val ts: String,
                val wd: Int,    //풍향
                val ws: Double  //바람속도
            )
        }

        data class Location(
            val coordinates: List<Double>,
            val type: String
        )
    }
}