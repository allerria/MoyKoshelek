package ru.yandex.moykoshelek.ui.common

import android.support.v7.util.DiffUtil
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet

class WalletDiffUtil(private val oldItems: List<Wallet>, private val newItems: List<Wallet>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition].id == newItems[newItemPosition].id

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldItems[oldItemPosition] == newItems[newItemPosition]

}
