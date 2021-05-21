package com.vku.myapplication.fragment

object c {
    fun convertKilocaloriesToMlKmin(kilocalories: Float, weightKgs: Float): Float {
        var kcalMin = kilocalories / 1440
        kcalMin /= 5f
        return kcalMin / weightKgs * 1000
    }
}