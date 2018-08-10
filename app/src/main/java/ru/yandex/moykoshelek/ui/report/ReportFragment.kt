package ru.yandex.moykoshelek.ui.report

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.getCurrentDateTimeBeforeDays
import ru.yandex.moykoshelek.ui.Screens
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.wallet.ReportAdapter
import java.util.*
import javax.inject.Inject

class ReportFragment : BaseFragment() {
    override val TAG = Screens.REPORT_SCREEN
    override val layoutRes = R.layout.fragment_report

    private lateinit var reportAdapter: ReportAdapter

    @Inject
    lateinit var viewModel: ReportViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var calendar = Calendar.getInstance()

    private var isLastClickedFrom = false

    private var from: Date = getCurrentDateTime()
    private var to: Date = getCurrentDateTimeBeforeDays(7)

    private var date: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        if (isLastClickedFrom) {
            from = calendar.time
            date_from_text_view.setText(calendar.time.format())
        } else {
            to = calendar.time
            date_to_text_view.setText(calendar.time.format())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(ReportViewModel::class.java)
        setupRecyclerView()
        date_from_text_view.setOnClickListener {
            isLastClickedFrom = true
            DatePickerDialog(context, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        date_to_text_view.setOnClickListener {
            isLastClickedFrom = false
            DatePickerDialog(context, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        show_button.setOnClickListener {
            launch(UI) {
                viewModel.getReport(from, to).await().observe(this@ReportFragment, android.arch.lifecycle.Observer { report ->
                    if (report != null) {
                        reportAdapter.setData(report.reports)
                        income_rub.text = report.incomeRub.formatMoney(CurrencyTypes.RUB)
                        income_usd.text = report.income.formatMoney(CurrencyTypes.USD)
                        expense_rub.text = report.expenseRub.formatMoney(CurrencyTypes.RUB)
                        expense_usd.text = report.expense.formatMoney(CurrencyTypes.USD)
                    }
                })
            }
        }
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter()
        report_recycler_view.adapter = reportAdapter
        report_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                val last = parent.childCount - 1
                outRect.left = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.right = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.top = resources.getDimension(R.dimen.dimen_16).toInt()
                if (position == last) {
                    outRect.bottom = resources.getDimension(R.dimen.dimen_16).toInt()
                }
            }
        })
        report_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

}