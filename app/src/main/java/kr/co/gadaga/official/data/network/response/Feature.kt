package kr.co.gadaga.official.data.network.response

data class Feature (
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)