package ru.yandex.moykoshelek.ui.wallet

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.fragment_wallets.*
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.forEachChild
import org.jetbrains.anko.forEachChildWithIndex
import ru.terrakok.cicerone.Router
import ru.yandex.moykoshelek.R
import ru.yandex.moykoshelek.data.datasource.local.entities.Wallet
import ru.yandex.moykoshelek.extensions.showSuccessToast
import ru.yandex.moykoshelek.ui.common.BaseFragment
import ru.yandex.moykoshelek.ui.Screens
import javax.inject.Inject

class WalletsFragment : BaseFragment() {

    override val layoutRes = R.layout.fragment_wallets
    override val TAG = Screens.WALLETS_SCREEN

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var viewModel: WalletViewModel

    lateinit var walletsAdapter: WalletsAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(WalletViewModel::class.java)
        initRecyclerView()
        initObserve()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_wallet_fab.setOnClickListener { router.navigateTo(Screens.WALLET_SCREEN) }
    }

    private fun initRecyclerView() {
        walletsAdapter = WalletsAdapter { navigateToEditWallet(it) }
        wallets_recycler_view.adapter = walletsAdapter
        wallets_recycler_view.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                val last = parent.childCount - 1
                outRect.top = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.right = resources.getDimension(R.dimen.dimen_16).toInt()
                outRect.left = resources.getDimension(R.dimen.dimen_16).toInt()
                if (position == last) {
                    outRect.bottom = resources.getDimension(R.dimen.dimen_16).toInt()
                }
            }
        })
        wallets_recycler_view.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    private fun initObserve() = launch {
        viewModel.wallets.await().observe(this@WalletsFragment, Observer { wallets ->
            if (wallets != null) {
                walletsAdapter.setData(wallets)
            }
        })
    }

    private fun navigateToEditWallet(walletId: Int) {
        router.navigateTo(Screens.WALLET_SCREEN, walletId)
    }
}