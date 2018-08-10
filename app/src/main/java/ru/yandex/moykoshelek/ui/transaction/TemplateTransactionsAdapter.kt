package ru.yandex.moykoshelek.ui.transaction

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_template_transaction.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.transactionTypeSign
import ru.yandex.moykoshelek.ui.common.TemplateTransactionDiffUtil

class TemplateTransactionsAdapter(private val clickListener: (Int, Int, Int) -> Unit) : RecyclerView.Adapter<TemplateTransactionsAdapter.ViewHolder>() {

    private val data: MutableList<TemplateTransaction> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_template_transaction, viewGroup, false)
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

        fun bind(item: TemplateTransaction) {
            walletId = item.walletId
            walletCurrency = item.currency
            transactionId = item.id
            with(itemView) {
                t_category_text_view.text = item.category
                t_name_text_view.text = item.name
                t_cost_text_view.text = transactionTypeSign(item.type) + item.cost.formatMoney(item.currency)
                t_date_text_view.text = item.time.format()
                t_period_text_view.text = item.period.toString()
            }
        }
    }

    fun setData(transactionList: List<TemplateTransaction>) {
        val diffResult = DiffUtil.calculateDiff(TemplateTransactionDiffUtil(data, transactionList))
        data.clear()
        data.addAll(transactionList)
        diffResult.dispatchUpdatesTo(this)
    }

}
