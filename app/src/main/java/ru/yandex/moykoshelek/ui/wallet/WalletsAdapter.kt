package ru.yandex.moykoshelek.ui.wallet

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_wallet_big.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.ui.common.WalletDiffUtil

class WalletsAdapter(
        private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<WalletsAdapter.ViewHolder>() {

    private val data: MutableList<Wallet> = mutableListOf()

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_wallet_big, viewGroup, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var walletId: Int = 0

        init {
            with(itemView) {
                setOnClickListener { clickListener(walletId) }
            }
        }

        fun bind(item: Wallet) {
            walletId = item.id
            with(itemView) {
                name_text_view.text = item.name
                balance_text_view.text = item.balance.formatMoney(item.currency)
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
