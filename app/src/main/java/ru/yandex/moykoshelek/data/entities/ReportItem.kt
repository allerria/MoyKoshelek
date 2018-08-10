package ru.yandex.moykoshelek.data.entities

import java.util.*

data class ReportItem(
        var category: String,
        var income: Double,
        var expense: Double,
        var incomeRub: Double,
        var expenseRub: Double
)