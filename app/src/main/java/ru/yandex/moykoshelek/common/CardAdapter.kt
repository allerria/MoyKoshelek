package ru.yandex.moykoshelek.common

import android.support.v7.widget.CardView


interface CardAdapter {

    var baseElevation: Float

    fun getCardViewAt(position: Int): CardView?
    fun getCount(): Int
    fun getMaxElevationFactor(): Int
}