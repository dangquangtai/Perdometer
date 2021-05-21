package com.vku.myapplication.map

import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
        @SerializedName("points")
        var points: String?
)