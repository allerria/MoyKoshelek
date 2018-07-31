package ru.yandex.moykoshelek.balance

import android.view.ViewGroup
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.common.CardAdapter
import ru.yandex.moykoshelek.models.database.entities.WalletData
import ru.yandex.moykoshelek.common.CurrencyTypes


class CardsPagerAdapter : PagerAdapter(), CardAdapter {
    private val views: MutableList<CardView?>
    private val data: MutableList<WalletData>
    override var baseElevation: Float = 0.toFloat()

    init {
        data = ArrayList()
        views = ArrayList()
    }

    fun addCardItem(item: WalletData) {
        views.add(null)
        data.add(item)
    }

    override fun getMaxElevationFactor(): Int {
        return 8
    }

    override fun getCardViewAt(position: Int): CardView? {
        return views[position]
    }

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
        bind(data[position], view)
        val cardView = view.findViewById(R.id.card_layout) as CardView

        if (baseElevation == 0f) {
            baseElevation = cardView.cardElevation
        }

        cardView.maxCardElevation = baseElevation * getMaxElevationFactor()
        views[position] = cardView
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
        views[position] = null
    }

    private fun bind(item: WalletData, view: View) {
        val cardBackground = view.findViewById<ImageView>(R.id.card_background)
        val cardName = view.findViewById<TextView>(R.id.card_name)
        val cardNumber = view.findViewById<TextView>(R.id.card_number)
        val cardBalance = view.findViewById<TextView>(R.id.card_balance)
        val cardDate = view.findViewById<TextView>(R.id.card_date)
        cardName.text = item.name
        cardNumber.text = item.number
        var currency = if (CurrencyTypes.USD == item.currency) "$ " else "\u20BD "
        currency += String.format("%.2f", item.balance)
        cardBalance.text = currency
        cardDate.text = item.date

    }

    fun getItem(currentItem: Int): WalletData {
        return data[currentItem]
    }

}