package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_transaction.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.ui.balance.CardsPagerAdapter
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import ru.yandex.moykoshelek.extensions.showSuccessToast
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import timber.log.Timber
import javax.inject.Inject


class TransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_transaction
    override val TAG = Screens.TRANSACTION_SCREEN

    companion object {
        fun getInstance(transactionId: Int?) = TransactionFragment().apply {
            if (transactionId != null) {
                arguments = Bundle().apply { putInt(TAG, transactionId) }
            }
        }
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: TransactionViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var cardAdapter: CardsPagerAdapter

    private var transactionId: Int? = null
    private var transaction = Transaction()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(TransactionViewModel::class.java)
        transactionId = arguments?.getInt(TAG)
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
        period_text_view.setOnClickListener { period_check_box.isChecked = !period_check_box.isChecked }
        template_text_view.setOnClickListener { template_check_box.isChecked = !template_check_box.isChecked }
        period_check_box.onCheckedChange { _, isChecked ->
            if (isChecked) {
                period_edit_text.visibility = View.VISIBLE
            } else {
                period_edit_text.visibility = View.GONE
            }
        }
        template_check_box.onCheckedChange { _, isChecked ->
            if (isChecked) {
                template_name_edit_text.visibility = View.VISIBLE
            } else {
                template_name_edit_text.visibility = View.GONE
            }
        }
        template_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setTemplate(p0?.getItemAtPosition(p2).toString())
            }

        }
        submit_button.setOnClickListener { createOrUpdateTransaction(transaction) }
    }

    private fun createOrUpdateTransaction(transaction: Transaction) {
        add_transaction_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = getString(R.string.fill_field)
                return
            }
        }
        if (period_edit_text.text.toString().contains(".")) {
            period_edit_text.error = getString(R.string.period_must_be_int)
            return
        }
        with(transaction) {
            val oldTransaction = this.copy()
            cost = transaction_amount.text.toString().toDouble()
            placeholder = getString(R.string.place_example)
            type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
            val wallet = cardAdapter.getItem(cards_viewpager.currentItem)
            currency = wallet.currency
            var period: Int? = null
            category = transaction_category.selectedItem.toString()
            walletId = wallet.id
            when {
                period_check_box.isChecked -> {
                    period = period_edit_text.text.toString().toInt()
                    viewModel.executePeriodTransaction(this, period)
                }
                id > 0 -> {
                    viewModel.executeAndUpdateTransaction(this, oldTransaction)
                }
                else -> {
                    viewModel.executeTransaction(this)
                }
            }
            val name = template_name_edit_text.text.toString()
            if (template_check_box.isChecked) {
                viewModel.createTemplateTransaction(transaction, name, period)
            }
        }
        showSuccessToast()
        router.backTo(Screens.BALANCE_SCREEN)
    }

    private fun setupViewPager() {
        tab_dots.setupWithViewPager(cards_viewpager, true)
        cardAdapter = CardsPagerAdapter()
        with(cards_viewpager) {
            adapter = cardAdapter
            clipToPadding = false
            setPadding(resources.getDimension(R.dimen.dimen_48).toInt(), 0, resources.getDimension(R.dimen.dimen_48).toInt(), 0)
            pageMargin = resources.getDimension(R.dimen.dimen_48).toInt()
        }
    }

    private fun initObservers() = launch(UI) {
        viewModel.wallets.await().observe(this@TransactionFragment, Observer { wallets ->
            if (wallets != null) {
                cardAdapter.setData(wallets)
            }
        })

        viewModel.templateTransactions.await().observe(this@TransactionFragment, Observer { templateTransactions ->
            if (templateTransactions != null && templateTransactions.isNotEmpty()) {
                template_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, listOf(getString(R.string.select_template)) + (templateTransactions.map { it.name }))
            } else {
                template_spinner.visibility = View.GONE
            }
        })

        if (transactionId != null) {
            viewModel.getTransactionById(transactionId!!).await().observe(this@TransactionFragment, Observer { tranasction ->
                if (tranasction != null) {
                    setTransaction(tranasction)
                }
            })
        }
    }

    private fun setTransaction(transaction: Transaction) {
        this.transaction = transaction
        if (transaction.id > 0) {
            template_spinner.visibility = View.GONE
            period_check_box.visibility = View.GONE
            period_text_view.visibility = View.GONE
        }
        cards_viewpager.currentItem = cardAdapter.itemPositionByWalletId(transaction.walletId)
        var categoryArrayId = 0
        if (transaction.type == TransactionTypes.IN) {
            in_radio.isChecked = true
            categoryArrayId = R.array.transaction_income_categories
        } else {
            out_radio.isChecked = true
            categoryArrayId = R.array.transaction_expense_categories
        }
        transaction_category.setSelection(resources.getStringArray(categoryArrayId).indexOf(transaction.category))
        transaction_amount.setText(transaction.cost.toString())
    }

    private fun setTemplate(templateName: String) = launch(UI) {
        val templates = viewModel.templateTransactions.await().value
        if (templates != null) {
            val template = templates.find { it.name == templateName }
            if (template != null) {
                template_check_box.isChecked = false
                template_name_edit_text.setText("")

                val transaction = Transaction(0, template.time, template.cost, template.currency, template.placeholder, template.type, template.walletId, template.category)
                setTransaction(transaction)

                if (template.period != null) {
                    period_check_box.isChecked = true
                    period_edit_text.visibility = View.VISIBLE
                    period_edit_text.setText(template.period.toString())
                } else {
                    period_check_box.isChecked = false
                    period_edit_text.setText("")
                    period_edit_text.visibility = View.GONE
                }
            }
        }
    }

}