package ru.yandex.moykoshelek.ui.transaction

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.ui.main.MainActivity
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.data.datasource.database.entities.TransactionData
import ru.yandex.moykoshelek.data.datasource.CurrencyPref
import ru.yandex.moykoshelek.data.datasource.database.AppDatabase
import ru.yandex.moykoshelek.data.entities.CurrencyTypes
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.interactors.WalletInteractor
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject


class AddTransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_add_transaction
    override val TAG = Screens.ADD_TRANSACTION_SCREEN

    private lateinit var layout: ConstraintLayout
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var cardAdapter: CardsPagerAdapter

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var walletInteractor: WalletInteractor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    private val COUNTRIES = arrayOf("Belgium", "France", "Italy", "Germany", "Spain")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager(view)
        fetchUniqueCategories(view)
        layout = view.findViewById(R.id.add_transaction_layout)
        view.findViewById<Button>(R.id.submit_button).setOnClickListener { createTransaction(view) }
    }

    private fun fetchUniqueCategories(view: View) {
        val task = Runnable {
            val test = walletInteractor.getCategories()
            test.observeForever {
                (activity as MainActivity).uiHandler.post {
                    val adapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, it!!)
                    val textView = view.findViewById(R.id.transaction_category) as AutoCompleteTextView
                    textView.setAdapter<ArrayAdapter<String>>(adapter)
                }
            }
        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun createTransaction(view: View) {
        for (i in 0 until layout.childCount)
            if (layout.getChildAt(i).visibility == View.VISIBLE && layout.getChildAt(i) is EditText && (layout.getChildAt(i) as EditText).text.isEmpty()) {
                (layout.getChildAt(i) as EditText).error = "Пожалуйста, запольните полье"
                return
            }
        val transaction = TransactionData()
        transaction.cost = view.findViewById<EditText>(R.id.transaction_amount).text.toString().toDouble()
        transaction.currency = view.findViewById<Spinner>(R.id.transaction_currency_spinner).selectedItemPosition
        transaction.placeholder = "Moscow, Russia"
        transaction.typeTransaction = if(view.findViewById<RadioButton>(R.id.in_radio).isChecked) TransactionTypes.IN else TransactionTypes.OUT
        val wallet = cardAdapter.getItem(view.findViewById<ViewPager>(R.id.cards_viewpager).currentItem)
        transaction.walletId = wallet.id?.toInt()
        transaction.category = view.findViewById<AutoCompleteTextView>(R.id.transaction_category).text.toString()
        insertTransactionDataInDb(transaction)
        var balanceChange = transaction.cost
        val curr = walletInteractor.getCurrencyRate().value!!
        if (wallet.currency != transaction.currency)
            balanceChange = if(transaction.currency == CurrencyTypes.USD) transaction.cost * curr else transaction.cost / curr
        if (transaction.typeTransaction == TransactionTypes.IN)
            wallet.balance += balanceChange
        else
            wallet.balance -= balanceChange
        val task = Runnable { walletInteractor.updateWallet(wallet) }
        (activity as MainActivity).dbWorkerThread.postTask(task)
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun insertTransactionDataInDb(data: TransactionData) {
        val task = Runnable { walletInteractor.addTransaction(data) }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }

    private fun setupViewPager(view: View) {
        viewPager = view.findViewById(R.id.cards_viewpager)
        tabLayout = view.findViewById(R.id.tab_dots) as TabLayout
        tabLayout.setupWithViewPager(viewPager, true)
        cardAdapter = CardsPagerAdapter()
        fetchwalletsDataFromDb()
        viewPager.adapter = cardAdapter
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.setPadding(96, 0, 96, 0)
        viewPager.pageMargin = 48
    }

    private fun fetchwalletsDataFromDb() {
        val task = Runnable {
            val test = walletInteractor.getWallets()
            test.observeForever {
                (activity as MainActivity).uiHandler.post {
                    if (it != null) {
                        for (i in 0 until it.size)
                            cardAdapter.addCardItem(it[i])
                        cardAdapter.notifyDataSetChanged()
                    }
                }
            }

        }
        (activity as MainActivity).dbWorkerThread.postTask(task)
    }
}