
package com.vku.myapplication.map

import com.google.gson.annotations.SerializedName

data class Bounds(
        @SerializedName("northeast")
        var northeast: Northeast?,
        @SerializedName("southwest")
        var southwest: Southwest?
)