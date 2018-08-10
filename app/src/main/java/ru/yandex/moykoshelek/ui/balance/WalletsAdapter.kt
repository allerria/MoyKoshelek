package ru.yandex.moykoshelek.ui.balance

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_wallet.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.ui.common.WalletDiffUtil

class WalletsAdapter(
        private val clickListener: (Int, Int, Int) -> Unit
) : RecyclerView.Adapter<WalletsAdapter.ViewHolder>() {

    private val data: MutableList<Wallet> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_wallet, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var walletId: Int = 0
        private var itemPosition: Int = 0
        private var walletCurrency: Int = 0

        init {
            with(itemView) {
                setOnClickListener { clickListener(walletId, itemPosition, walletCurrency) }
            }
        }

        fun bind(item: Wallet) {
            itemPosition = data.indexOf(item)
            walletId = item.id
            walletCurrency = item.currency
            with(itemView) {
                if (item.id > 0) {
                    add_wallet_text_view.visibility = View.GONE
                    balance_text_view.visibility = View.VISIBLE
                    name_text_view.visibility = View.VISIBLE
                    setBackgroundResource(R.drawable.wallet_background)
                    name_text_view.text = item.name
                    balance_text_view.text = item.balance.formatMoney(item.currency)
                } else {
                    add_wallet_text_view.visibility = View.VISIBLE
                    balance_text_view.visibility = View.GONE
                    name_text_view.visibility = View.GONE
                    setBackgroundResource(R.drawable.wallet_add_background)
                    name_text_view.text = ""
                    balance_text_view.text = ""
                }
            }
        }
    }

    fun setData(wallets: List<Wallet>) {
        val diffResult = DiffUtil.calculateDiff(WalletDiffUtil(data, wallets))
        data.clear()
        data.addAll(wallets)
        diffResult.dispatchUpdatesTo(this)
    }
}
