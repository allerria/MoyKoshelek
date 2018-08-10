package ru.yandex.moykoshelek.ui.transaction

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_period_transaction.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.transactionTypeSign
import ru.yandex.moykoshelek.ui.common.PeriodTransactionDiffUtil

class PeriodTransactionsAdapter(private val clickListener: (Int, Int, Int) -> Unit) : RecyclerView.Adapter<PeriodTransactionsAdapter.ViewHolder>() {

    private val data: MutableList<PeriodTransaction> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_period_transaction, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var transactionId: Int = 0
        private var walletId: Int = 0
        private var walletCurrency: Int = 0

        init {
            with(itemView) {
                setOnClickListener { clickListener(walletId, walletCurrency, transactionId) }
            }
        }

        fun bind(item: PeriodTransaction) {
            walletId = item.walletId
            walletCurrency = item.currency
            transactionId = item.id
            with(itemView) {
                p_category_text_view.text = item.category
                p_cost_text_view.text = transactionTypeSign(item.type) + item.cost.formatMoney(item.currency)
                p_date_text_view.text = item.time.format()
                p_period_text_view.text = item.period.toString()
            }
        }
    }

    fun setData(transactionList: List<PeriodTransaction>) {
        val diffResult = DiffUtil.calculateDiff(PeriodTransactionDiffUtil(data, transactionList))
        data.clear()
        data.addAll(transactionList)
        diffResult.dispatchUpdatesTo(this)
    }

}
