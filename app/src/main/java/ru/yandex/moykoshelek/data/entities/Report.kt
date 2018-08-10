package ru.yandex.moykoshelek.data.entities

import java.util.*

data class Report(
        var reports: List<ReportItem>,
        var income: Double,
        var expense: Double,
        var incomeRub: Double,
        var expenseRub: Double,
        var from: Date,
        var to: Date
)