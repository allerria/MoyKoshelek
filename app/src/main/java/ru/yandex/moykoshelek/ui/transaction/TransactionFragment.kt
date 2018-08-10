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
import ru.yandex.moykoshelek.data.datasource.local.entities.TemplateTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Transaction
import ru.yandex.moykoshelek.data.entities.TransactionTypes
import ru.yandex.moykoshelek.extensions.showSuccessToast
import ru.yandex.moykoshelek.extensions.showToast
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import ru.yandex.moykoshelek.data.datasource.local.entities.PeriodTransaction
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.formatMoney
import ru.yandex.moykoshelek.extensions.getCurrentDateTime
import timber.log.Timber


class TransactionFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_transaction
    override val TAG = Screens.TRANSACTION_SCREEN

    companion object {
        fun getInstance(args: ArrayList<Int>) = TransactionFragment().apply {
            arguments = Bundle().apply {
                putIntegerArrayList(TAG, args)
            }
        }
    }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: TransactionViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var anyTransactionId: Int? = null
    private var walletId: Int = 0
    private var walletCurrency: Int = 0
    private var transaction: Transaction? = null
    private var periodTransaction: PeriodTransaction? = null
    private var templateTransaction: TemplateTransaction? = null
    private var actionType = 0
    private var walletPosition: Int = -1

    private var dialogClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                when (actionType) {
                    ActionTypes.EDIT_TRANSACTION -> viewModel.deleteTransaction(transaction!!)
                    ActionTypes.EDIT_PERIOD_TRANSACTION -> viewModel.deletePeriodTransaction(periodTransaction!!)
                    ActionTypes.EDIT_TEMPLATE_TRANSACTION -> viewModel.deleteTemplateTransaction(templateTransaction!!)
                }
                router.exit()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(TransactionViewModel::class.java)
        val args = arguments!!.getIntegerArrayList(TAG)
        actionType = args[0]
        walletId = args[1]
        walletCurrency = args[2]
        if (args!!.size == 4) {
            anyTransactionId = args[3]
        }
        initObservers()
        initView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initObservers() = launch(UI) {

        viewModel.wallets.await().observe(this@TransactionFragment, Observer { wallets ->
            if (wallets != null) {
                initWalletSpinner(wallets)
            }
        })

        viewModel.templateTransactions.await().observe(this@TransactionFragment, Observer { templateTransactions ->
            if (templateTransactions != null && templateTransactions.isNotEmpty()) {
                if (actionType == ActionTypes.EDIT_TEMPLATE_TRANSACTION) {
                    templateTransaction = templateTransactions.find { it.id == anyTransactionId }
                    setTemplateTransaction()
                    setTransactionTypeInRadio(templateTransaction!!.type)
                }
                template_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, listOf(getString(R.string.choose_template)) + (templateTransactions.map { it.name }))
            } else {
                template_spinner.visibility = View.GONE
            }
        })

        when (actionType) {
            ActionTypes.EDIT_TRANSACTION -> viewModel.getTransactionById(anyTransactionId!!).await().observe(this@TransactionFragment, Observer { transaction ->
                if (transaction != null) {
                    this@TransactionFragment.transaction = transaction
                    setTransaction()
                    setTransactionTypeInRadio(transaction.type)
                }
            })
            ActionTypes.EDIT_PERIOD_TRANSACTION -> {
                periodTransaction = viewModel.getPeriodTransactionById(anyTransactionId!!).await()
                setPeriodTransaction()
                setTransactionTypeInRadio(periodTransaction!!.type)
            }
        }
    }

    fun setTemplate(templateName: String) = launch(UI) {
        val templates = viewModel.templateTransactions.await().value
        if (templates != null) {
            val template = templates.find { it.name == templateName }
            if (template != null) {
                template_check_box.isChecked = false
                template_name_edit_text.setText("")

                this@TransactionFragment.transaction = Transaction(0, template.time, template.cost, template.currency, template.type, template.walletId, template.category)
                setTransaction()

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

    private fun createTransaction() {
        if (!checkForm()) {
            return
        }
        with(transaction!!) {
            launch {
                transaction!!.walletId = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().id
                transaction!!.currency = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().currency
                cost = transaction_amount.text.toString().toDouble()
                type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
                var period: Int? = null
                category = transaction_category.selectedItem.toString()
                date = getCurrentDateTime()
                if (period_check_box.isChecked) {
                    period = period_edit_text.text.toString().toInt()
                    viewModel.executePeriodTransaction(transaction!!, period)
                } else {
                    viewModel.executeTransaction(transaction!!)
                }
                if (template_check_box.isChecked) {
                    val name = template_name_edit_text.text.toString()
                    viewModel.createTemplateTransaction(transaction!!, name, period)
                }
            }
        }
        showSuccessToast()
        router.exit()
    }

    private fun editTransaction() {
        if (!checkForm()) {
            return
        }
        with(transaction!!) {
            launch {
                transaction!!.walletId = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().id
                transaction!!.currency = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().currency
                val oldTransaction = transaction!!.copy()
                cost = transaction_amount.text.toString().toDouble()
                type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
                category = transaction_category.selectedItem.toString()
                viewModel.updateTransaction(transaction!!, oldTransaction)
                if (template_check_box.isChecked) {
                    val name = template_name_edit_text.text.toString()
                    date = getCurrentDateTime()
                    viewModel.createTemplateTransaction(transaction!!, name, null)
                }
            }
        }
        showSuccessToast()
        router.exit()
    }

    private fun editPeriodTransaction() {
        if (!checkForm()) {
            return
        }
        with(periodTransaction!!) {
            launch {
                periodTransaction!!.walletId = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().id
                periodTransaction!!.currency = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().currency
                val oldPeriodTransaction = periodTransaction!!.copy()
                cost = transaction_amount.text.toString().toDouble()
                type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
                period = period_edit_text.text.toString().toInt()
                category = transaction_category.selectedItem.toString()
                viewModel.updatePeriodTransaction(periodTransaction!!, oldPeriodTransaction)
                if (template_check_box.isChecked) {
                    val name = template_name_edit_text.text.toString()
                    viewModel.createTemplateTransaction(Transaction(0, getCurrentDateTime(), periodTransaction!!.cost, periodTransaction!!.currency, periodTransaction!!.type, periodTransaction!!.walletId, periodTransaction!!.category), name, period)
                }
            }
        }
        showSuccessToast()
        router.exit()
    }

    private fun editTemplateTransaction() {
        if (!checkForm()) {
            return
        }
        with(templateTransaction!!) {
            launch {
                templateTransaction!!.walletId = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().id
                templateTransaction!!.currency = viewModel.getWalletByTag(wallet_spinner.selectedItem.toString()).await().currency
                val oldTemplateTransaction = templateTransaction!!.copy()
                cost = transaction_amount.text.toString().toDouble()
                type = if (in_radio.isChecked) TransactionTypes.IN else TransactionTypes.OUT
                var period: Int? = null
                if (period_check_box.isChecked) {
                    period = period_edit_text.text.toString().toInt()
                } else {
                    period = null
                }
                category = transaction_category.selectedItem.toString()
                name = template_name_edit_text.text.toString()
                viewModel.updateTemplateTransaction(templateTransaction!!, oldTemplateTransaction)
            }
        }
        showSuccessToast()
        router.exit()
    }

    private fun initView() {
        initTypeRadioGroup()
        initPeriodView()
        initTemplateView()
        if (actionType != ActionTypes.ADD_TRANSACTION) {
            submit_button.text = getString(R.string.edit)
            delete_button.setOnClickListener { createDeleteDialog() }
        } else {
            transaction = Transaction()
            delete_button.visibility = View.GONE
        }
        when (actionType) {
            ActionTypes.ADD_TRANSACTION -> submit_button.setOnClickListener { createTransaction() }
            ActionTypes.EDIT_TRANSACTION -> submit_button.setOnClickListener { editTransaction() }
            ActionTypes.EDIT_PERIOD_TRANSACTION -> submit_button.setOnClickListener { editPeriodTransaction() }
            ActionTypes.EDIT_TEMPLATE_TRANSACTION -> submit_button.setOnClickListener { editTemplateTransaction() }
        }
    }

    private fun initTypeRadioGroup() {
        in_radio.onCheckedChange { _, isChecked ->
            if (isChecked) {
                transaction_category.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.transaction_income_categories))
            } else {
                transaction_category.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.transaction_expense_categories))
            }
        }
    }

    private fun initPeriodView() {
        period_text_view.setOnClickListener { period_check_box.isChecked = !period_check_box.isChecked }

        period_check_box.onCheckedChange { _, isChecked ->
            if (isChecked) {
                period_edit_text.visibility = View.VISIBLE
            } else {
                period_edit_text.visibility = View.GONE
            }
        }
    }

    private fun initTemplateView() {
        template_text_view.setOnClickListener { template_check_box.isChecked = !template_check_box.isChecked }

        template_check_box.onCheckedChange { _, isChecked ->
            if (isChecked) {
                template_name_edit_text.visibility = View.VISIBLE
            } else {
                template_name_edit_text.visibility = View.GONE
            }
        }

        template_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>?, itemView: View?, position: Int, p3: Long) {
                setTemplate(adapterView?.getItemAtPosition(position).toString())
            }

        }
    }

    private fun checkForm(): Boolean {
        add_transaction_layout.forEachChild {
            if (it.visibility == View.VISIBLE && it is EditText && it.text.isEmpty()) {
                it.error = getString(R.string.fill_field)
                return false
            }
        }
        if (period_edit_text.text.toString().contains(".")) {
            period_edit_text.error = getString(R.string.period_must_be_int)
            return false
        }
        if (transaction_category.selectedItem.toString() == getString(R.string.choose_category)) {
            showToast(getString(R.string.choose_category))
            return false
        }
        if (wallet_spinner.selectedItem.toString() == getString(R.string.choose_wallet)) {
            showToast(getString(R.string.choose_wallet))
        }
        return true
    }

    private fun createDeleteDialog() {
        AlertDialog.Builder(this@TransactionFragment.context!!)
                .setMessage(getString(R.string.are_you_sure_to_delete))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show()
    }

    private fun setTransaction() {
        if (transaction!!.id > 0) {
            template_spinner.visibility = View.GONE
            period_check_box.visibility = View.GONE
            period_text_view.visibility = View.GONE
        }
        var categoryArrayId = 0
        if (transaction!!.type == TransactionTypes.IN) {
            //in_radio.isChecked = true
            categoryArrayId = R.array.transaction_income_categories
        } else {
            //out_radio.isChecked = true
            categoryArrayId = R.array.transaction_expense_categories
        }
        transaction_amount.setText(transaction!!.cost.toString())
        launch(UI) {
            delay(166)
            transaction_category.setSelection(resources.getStringArray(categoryArrayId).indexOf(transaction!!.category), true)
        }
    }

    private fun setPeriodTransaction() {
        template_spinner.visibility = View.GONE
        period_check_box.visibility = View.VISIBLE
        period_text_view.visibility = View.VISIBLE
        period_check_box.visibility = View.GONE
        period_text_view.visibility = View.GONE
        period_edit_text.visibility = View.VISIBLE
        period_edit_text.setText(periodTransaction!!.period.toString())
        var categoryArrayId = 0
        if (periodTransaction!!.type == TransactionTypes.IN) {
            //in_radio.isChecked = true
            categoryArrayId = R.array.transaction_income_categories
        } else {
            //out_radio.isChecked = true
            categoryArrayId = R.array.transaction_expense_categories
        }
        launch(UI) {
            delay(166)
            transaction_category.setSelection(resources.getStringArray(categoryArrayId).indexOf(periodTransaction!!.category), true)
        }
        transaction_amount.setText(periodTransaction!!.cost.toString())
    }

    private fun setTemplateTransaction() {
        template_spinner.visibility = View.GONE
        period_check_box.visibility = View.VISIBLE
        period_text_view.visibility = View.VISIBLE
        template_check_box.visibility = View.GONE
        template_text_view.visibility = View.GONE
        template_name_edit_text.visibility = View.VISIBLE
        template_name_edit_text.setText(templateTransaction!!.name)
        if (templateTransaction!!.period != null) {
            period_check_box.isChecked = templateTransaction!!.period != null
            period_edit_text.setText(templateTransaction!!.period.toString())
        }
        var categoryArrayId = 0
        if (templateTransaction!!.type == TransactionTypes.IN) {
            //in_radio.isChecked = true
            categoryArrayId = R.array.transaction_income_categories
        } else {
            //out_radio.isChecked = true
            categoryArrayId = R.array.transaction_expense_categories
        }
        launch(UI) {
            delay(166)
            transaction_category.setSelection(resources.getStringArray(categoryArrayId).indexOf(templateTransaction!!.category), true)
        }
        transaction_amount.setText(templateTransaction!!.cost.toString())
    }

    private fun setTransactionTypeInRadio(transactionType: Int) {
        if (transactionType == TransactionTypes.IN) {
            in_radio.isChecked = true
        } else {
            out_radio.isChecked = true
        }
    }

    private fun initWalletSpinner(wallets: List<Wallet>) {
        val walletStringArray = mutableListOf<String>()
        if (anyTransactionId != null) {
            walletStringArray.addAll(listOf(getString(R.string.choose_wallet)).plus(wallets.filter { walletCurrency == it.currency }.map { "${it.name}-${it.balance.formatMoney(it.currency)}" }))
        } else {
            walletStringArray.addAll(listOf(getString(R.string.choose_wallet)).plus(wallets.map { "${it.name}-${it.balance.formatMoney(it.currency)}" }))
        }
        wallet_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, walletStringArray)
        if (walletId > 0) {
            val it = wallets.find { it.id == walletId }!!
            walletPosition = walletStringArray.indexOf("${it.name}-${it.balance.formatMoney(it.currency)}")
            launch(UI) {
                delay(166)
                wallet_spinner.setSelection(walletPosition)
            }
        }
    }

}