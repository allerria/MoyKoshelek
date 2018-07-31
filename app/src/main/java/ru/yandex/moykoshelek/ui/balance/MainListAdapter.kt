package ru.yandex.moykoshelek.ui.balance

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.textColorResource
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.ui.common.expandablelayout.ExpandableLayout

class MainListAdapter(var transactionList: List<TransactionData>, private val context: Context?) : RecyclerView.Adapter<MainListAdapter.ViewHolder>() {

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.layout.setOnExpandListener {
            if (it) {
                viewHolder.expandIcon.setImageResource(R.drawable.ic_arrow_up)
            } else {
                viewHolder.expandIcon.setImageResource(R.drawable.ic_arrow_down)
            }
        }
        viewHolder.transactionTag.text = transactionList[position].category
        //viewHolder.transactionCardName = transactionList[position].
        var currency = (if (transactionList[position].currency == CurrencyTypes.USD) "$ " else "\u20BD ") + transactionList[position].cost
        if (transactionList[position].typeTransaction == TransactionTypes.IN) {
            currency = "+ $currency"
            viewHolder.transactionAmount.textColorResource = android.R.color.holo_green_light
        } else {
            currency = "- $currency"
            viewHolder.transactionAmount.textColorResource = R.color.red
        }
        viewHolder.transactionAmount.text = currency
        viewHolder.transactionPlaceholder.text = transactionList[position].placeholder
        viewHolder.transactionTime.text = transactionList[position].time
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.transaction_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return transactionList.size
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
    }

}
