package co.uk.android.spotify.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AccessTokenRequest(
    @Json(name = "client_id")
    val clientId: String,
    @Json(name = "client_secret")
    val clientSecret: String,
    @Json(name = "grant_type")
    val grantType: String,
)