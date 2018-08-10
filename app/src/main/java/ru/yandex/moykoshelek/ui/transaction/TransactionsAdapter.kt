package ru.yandex.moykoshelek.ui.transaction

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_transaction.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.transactionTypeSign
import ru.yandex.moykoshelek.ui.common.TransactionDiffUtil

class TransactionsAdapter(private val clickListener: (Int) -> Unit) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    private val data: MutableList<Transaction> = mutableListOf()
    private val transactions: MutableList<Transaction> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_transaction, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var transactionId: Int = 0

        init {
            with(itemView) {
                setOnClickListener { clickListener(transactionId) }
            }
        }

        fun bind(item: Transaction) {
            transactionId = item.id
            with(itemView) {
                category_text_view.text = item.category
                cost_text_view.text = transactionTypeSign(item.type) + item.cost.formatMoney(item.currency)
                date_text_view.text = item.date.format()
            }
        }
    }

    fun setData(walletId: Int) {
        val transactionList = transactions.filter { it.walletId == walletId }
        val diffResult = DiffUtil.calculateDiff(TransactionDiffUtil(data, transactionList))
        data.clear()
        data.addAll(transactionList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setTransactions(transactionList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(transactionList)
    }

}
