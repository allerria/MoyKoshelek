package ru.yandex.moykoshelek.ui.balance

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.transaction_expand.view.*
import kotlinx.android.synthetic.main.transaction_item.view.*
import kotlinx.android.synthetic.main.transaction_main.view.*
import org.jetbrains.anko.textColorResource
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.currencySign
import ru.yandex.moykoshelek.extensions.format
import ru.yandex.moykoshelek.ui.common.TransactionDiffUtil

class MainListAdapter(private val context: Context?) : RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    private val data: MutableList<Transaction> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.transaction_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            with(itemView) {
                expandable_layout.setOnExpandListener {
                    if (it) {
                        transaction_arrow.setImageResource(R.drawable.ic_arrow_up)
                    } else {
                        transaction_arrow.setImageResource(R.drawable.ic_arrow_down)
                    }
                }
            }
        }

        fun bind(item: Transaction) {
            with(itemView) {
                transaction_tag.text = item.category
                var currency = currencySign(item.currency) + item.cost
                if (item.type == TransactionTypes.IN) {
                    currency = "+ $currency"
                    transaction_amount.textColorResource = android.R.color.holo_green_light
                } else {
                    currency = "- $currency"
                    transaction_amount.textColorResource = R.color.red
                }
                transaction_amount.text = currency
                transaction_placeholder.text = item.placeholder
                transaction_time.text = item.date.format()
            }
        }
    }

    fun setData(transactionList: List<Transaction>) {
        val oldItems = data
        data.clear()
        data.addAll(transactionList)
        val diffResult = DiffUtil.calculateDiff(TransactionDiffUtil(oldItems, transactionList))
        diffResult.dispatchUpdatesTo(this)
    }

}
