package com.lugares.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Lugar(
    var id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val web: String?,
    val latitude: Double?,
    val length: Double?,
    val height: Double?,
    val routeAudio: String?,
    val routeImage: String?,
): Parcelable {
    constructor():
            this("","", "", "", "", 0.0, 0.0, 0.0, "", "")
}