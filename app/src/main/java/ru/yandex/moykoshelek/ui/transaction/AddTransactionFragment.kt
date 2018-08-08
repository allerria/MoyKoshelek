package ru.yandex.moykoshelek.ui.transaction

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
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
        template_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setTemplate(p0?.getItemAtPosition(p2).toString())
            }

        }
        submit_button.setOnClickListener { createTransaction() }
    }

    private fun createTransaction() {
        add_transaction_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = getString(R.string.fill_field)
                return
            }
        }
        val transaction = Transaction()
        with(transaction) {
            cost = transaction_amount.text.toString().toDouble()
            placeholder = getString(R.string.place_example)
            type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
            val wallet = cardAdapter.getItem(cards_viewpager.currentItem)
            currency = wallet.currency
            walletId = wallet.id
            date = getCurrentDateTime()
            category = transaction_category.selectedItem.toString()
            var period: Int? = null
            if (period_check_box.isChecked) {
                period = period_edit_text.text.toString().toInt()
                viewModel.executePeriodTransaction(this, period)
            } else {

                viewModel.executeTransaction(this)
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
        viewModel.wallets.await().observe(this@AddTransactionFragment, Observer { wallets ->
            if (wallets != null) {
                cardAdapter.setData(wallets)
            }
        })

        viewModel.templateTransactions.await().observe(this@AddTransactionFragment, Observer { templateTransactions ->
            if (templateTransactions != null && templateTransactions.isNotEmpty()) {
                template_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, listOf(getString(R.string.select_template)) + (templateTransactions.map { it.name }))
            } else {
                template_spinner.visibility = View.GONE
            }
        })
    }

    private fun setTemplate(templateName: String) = launch(UI) {
        val templates = viewModel.templateTransactions.await().value
        if (templates != null) {
            val template = templates.find { it.name == templateName }
            if (template != null) {
                cards_viewpager.currentItem = cardAdapter.itemPositionByWalletId(template.walletId)
                var categoryArrayId = 0
                if (template.type == TransactionTypes.IN) {
                    in_radio.isChecked = true
                    categoryArrayId = R.array.transaction_income_categories
                } else {
                    out_radio.isChecked = true
                    categoryArrayId = R.array.transaction_expense_categories
                }
                transaction_category.setSelection(resources.getStringArray(categoryArrayId).indexOf(template.category))
                transaction_amount.setText(template.cost.toString())
                if (template.period != null) {
                    period_check_box.isChecked = true
                    period_edit_text.visibility = View.VISIBLE
                    period_edit_text.setText(template.period.toString())
                } else {
                    period_check_box.isChecked = false
                    period_edit_text.visibility = View.GONE
                }
            }
        }
    }

}