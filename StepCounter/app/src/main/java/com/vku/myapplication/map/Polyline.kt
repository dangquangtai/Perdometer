package com.vku.myapplication.map

import com.google.gson.annotations.SerializedName

data class Polyline(
        @SerializedName("points")
        var points: String?
)