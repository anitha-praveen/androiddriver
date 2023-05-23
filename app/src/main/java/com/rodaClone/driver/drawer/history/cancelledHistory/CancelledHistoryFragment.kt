package com.rodaClone.driver.drawer.history.cancelledHistory

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.databinding.FragmentCancelledHistoryBinding
import com.rodaClone.driver.drawer.history.HistoryFragmentDirections
import com.rodaClone.driver.drawer.history.adapter.CancelledHistoryAdapter
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.PaginationScrollListener
import javax.inject.Inject

class CancelledHistoryFragment : BaseFragment<FragmentCancelledHistoryBinding, CancelledHistoryVM>(),
    CancelledHistoryNavigator {
    companion object {
        const val TAG = "CancelledHistoryFragment"
    }

    private lateinit var binding: FragmentCancelledHistoryBinding
    var adapter: CancelledHistoryAdapter? = null
    var isPageLoading  = false

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(CancelledHistoryVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
//        binding.pullToRefresh.setOnRefreshListener {
//            setupAdapter()
//            binding.pullToRefresh.isRefreshing = false
//        }
        setupAdapter()
    }


    override fun getLayoutId() = R.layout.fragment_cancelled_history

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private fun setupAdapter() {
        adapter = vm.translationModel?.let {
            CancelledHistoryAdapter(
                ArrayList(),
                vm.session,
                vm.getNavigator(),
                it
            )
        }
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerHistoryList.layoutManager = mLayoutManager
        binding.recyclerHistoryList.itemAnimator = DefaultItemAnimator()
        binding.recyclerHistoryList.adapter = adapter
        vm.currentPage = 1
        binding.recyclerHistoryList.addOnScrollListener(object :
            PaginationScrollListener(mLayoutManager) {
            override fun loadMoreItems() {
                vm.isLoading.value = true
                isPageLoading = true
                vm.previousPage = vm.currentPage
                vm.currentPage += 1
                vm.getHistoryList()
            }

            override val totalPageCount: Int get() = vm.totalPages

            override val isLastPage: Boolean
                get() {
                    return vm.isLastPage
                }

            override val isLoading: Boolean get() {
                    return isPageLoading
                }
        })


        vm.getHistoryList()
    }

    override fun loadData(data: MutableList<HistoryModel.History>) {
        vm.isLoading.value = true
        adapter!!.addData(data)
        vm.isLoading.value = false

    }

    override fun invoice(data: RequestResponseData) {
        if (isAdded) {
            val intent = Intent(Config.RECEIVE_DIRECTION_INVOICE)
            intent.putExtra("REQUEST_DATA_ID",data.id)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

    }

    override fun startSimmer() {
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    override fun stopSimmer() {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE

    }

    override fun showText() {
        binding.showNoDataFoundChl.visibility = View.VISIBLE
    }

    override fun hideText() {
        binding.showNoDataFoundChl.visibility = View.GONE
    }


    override fun stopLoader() {
        isPageLoading = false
        vm.isLoading.value = false
    }

    override fun mentionLastPage() {
        if (vm.currentPage != 1) {
            isPageLoading = false
            vm.isLastPage = true
        }
    }

    override fun goToComplaints(reqId:String) {
      //  val action = HistoryFragmentDirections.goToComplaints(1, reqId)
        val bundle = bundleOf(Config.mode to 1,Config.request_id to reqId)
        findNavController().navigate(R.id.go_to_complaints,bundle)
    }
}