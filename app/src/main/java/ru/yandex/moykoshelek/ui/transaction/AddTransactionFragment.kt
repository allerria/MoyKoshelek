package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import java.util.*
import javax.inject.Inject


class AddTransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_transaction
    override val TAG = Screens.ADD_TRANSACTION_SCREEN

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: AddTransactionViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var cardAdapter: CardsPagerAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(AddTransactionViewModel::class.java)
        initObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        in_radio.onCheckedChange { _, isChecked ->
            if (isChecked) {
                transaction_category.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, resources.getStringArray(R.array.transaction_income_categories))
            } else {
                transaction_category.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, resources.getStringArray(R.array.transaction_expense_categories))
            }
        }
        period_check_box.onCheckedChange { _, isChecked ->
            if (isChecked) {
                period_edit_text.visibility = View.VISIBLE
            } else {
                period_edit_text.visibility = View.GONE
            }
        }
        submit_button.setOnClickListener { createTransaction() }
    }

    private fun createTransaction() {
        add_transaction_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = "Пожалуйста заполните поле"
                return
            }
        }
        val transaction = Transaction()
        transaction.cost = transaction_amount.text.toString().toDouble()
        transaction.placeholder = "Moscow, Russia"
        transaction.type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
        val wallet = viewModel.getWallet(cards_viewpager.currentItem)
        transaction.currency = wallet.currency
        transaction.walletId = wallet.id
        transaction.date = getCurrentDateTime()
        transaction.category = transaction_category.selectedItem.toString()
        if (period_check_box.isChecked) {
            viewModel.executePeriodTransaction(transaction, period_edit_text.text.toString().toInt())
        } else {
            viewModel.executeTransaction(transaction)
        }
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun setupViewPager() {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        cards_viewpager.adapter = cardAdapter
        cards_viewpager.offscreenPageLimit = 3
        cards_viewpager.clipToPadding = false
        cards_viewpager.setPadding(96, 0, 96, 0)
        cards_viewpager.pageMargin = 48
    }


    private fun initObservers() {

        viewModel.wallets.observe(this, Observer { wallets ->
            cardAdapter.setData(wallets!!)
        })

    }

}