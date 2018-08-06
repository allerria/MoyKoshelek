package ru.yandex.moykoshelek.ui.balance

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.textColorResource
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.toString
import ru.yandex.moykoshelek.ui.common.TransactionDiffUtil
import ru.yandex.moykoshelek.ui.common.expandablelayout.ExpandableLayout

class MainListAdapter(private val context: Context?) : RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    private val data: MutableList<Transaction> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.transactionTag.text = data[position].category
        var currency = (if (data[position].currency == CurrencyTypes.USD) "$ " else "\u20BD ") + data[position].cost
        if (data[position].type == TransactionTypes.IN) {
            currency = "+ $currency"
            viewHolder.transactionAmount.textColorResource = android.R.color.holo_green_light
        } else {
            currency = "- $currency"
            viewHolder.transactionAmount.textColorResource = R.color.red
        }
        viewHolder.transactionAmount.text = currency
        viewHolder.transactionPlaceholder.text = data[position].placeholder
        viewHolder.transactionTime.text = data[position].date.toString("yyyy/MM/dd HH:mm:ss")
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.transaction_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView.findViewById<ExpandableLayout>(R.id.expandable_layout)!!
        val expandIcon = itemView.findViewById<ImageView>(R.id.transaction_arrow)!!
        val transactionTag = itemView.findViewById<TextView>(R.id.transaction_tag)!!
        val transactionCardName = itemView.findViewById<TextView>(R.id.transaction_card_name)!!
        val transactionAmount = itemView.findViewById<TextView>(R.id.transaction_amount)!!
        val transactionTime = itemView.findViewById<TextView>(R.id.transaction_time)!!
        val transactionPlaceholder = itemView.findViewById<TextView>(R.id.transaction_placeholder)!!
        val transactionHasBalance = itemView.findViewById<TextView>(R.id.transaction_has_balance)!!
        init {
            layout.setOnExpandListener {
                if (it) {
                    this.expandIcon.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    this.expandIcon.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
    }

    fun setData(transactionList: List<Transaction>) {
        val diffResult = DiffUtil.calculateDiff(TransactionDiffUtil(data, transactionList))
        diffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(transactionList
        )
    }

}
