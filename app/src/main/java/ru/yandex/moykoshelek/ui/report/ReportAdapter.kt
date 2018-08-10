package ru.yandex.moykoshelek.ui.wallet

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_report.view.*
import kotlinx.android.synthetic.main.item_wallet_big.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.ReportItem
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.ui.common.WalletDiffUtil

class ReportAdapter() : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private val data: MutableList<ReportItem> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_report, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ReportItem) {
            with(itemView) {
                category_text_view.text = item.category
                income_rub.text = item.incomeRub.formatMoney(CurrencyTypes.RUB)
                income_usd.text = item.income.formatMoney(CurrencyTypes.USD)
                expense_rub.text = item.expenseRub.formatMoney(CurrencyTypes.RUB)
                expense_usd.text = item.expense.formatMoney(CurrencyTypes.USD)
            }
        }
    }

    fun setData(wallets: List<ReportItem>) {
        data.clear()
        data.addAll(wallets)
        notifyDataSetChanged()
    }
}
