package ru.yandex.moykoshelek.ui.balance

import android.view.ViewGroup
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.card_view.view.*
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.currencySign


class CardsPagerAdapter : PagerAdapter() {

    private val data: MutableList<Wallet> = mutableListOf()

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                .inflate(R.layout.card_view, container, false)
        container.addView(view)
        view.tag = data[position].id
        bind(data[position], view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun bind(item: Wallet, view: View) {
        with(view) {
            card_name.text = item.name
            card_number.text = item.number
            var currency = currencySign(item.currency)
            currency += String.format("%.2f", item.balance)
            card_balance.text = currency
            card_date.text = item.date
        }
    }

    fun setData(wallets: List<Wallet>) {
        data.clear()
        data.addAll(wallets)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data[position]

    fun itemPositionByWalletId(walletId: Int) = data.indexOfFirst { it.id == walletId }
}