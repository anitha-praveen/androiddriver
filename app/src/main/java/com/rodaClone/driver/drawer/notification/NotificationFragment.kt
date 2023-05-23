package com.rodaClone.driver.drawer.notification

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.NotificationData
import com.rodaClone.driver.databinding.FragmentNotificationBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.notification.adapter.NotificationAdapter
import com.rodaClone.driver.ut.PaginationScrollListener
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class NotificationFragment : BaseFragment<FragmentNotificationBinding, NotificationVM>(),
    NotificationNavigator {

    lateinit var binding: FragmentNotificationBinding
    private var mlayoutManager:LinearLayoutManager? = null
    private var adapeter:NotificationAdapter? = null
    private val notificationList = ArrayList<NotificationData>()
    private var isPageLoading = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(NotificationVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        binding.backImg.setOnClickListener { closeFragment() }
        binding.pullToRefresh.setOnRefreshListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.notification)
            binding.pullToRefresh.isRefreshing = false
        }
        vm.getNotifications()
        startSimmer()
        mlayoutManager = LinearLayoutManager(context)
        adapeter = NotificationAdapter(notificationList)
        binding.recyclerNotificationList.layoutManager = mlayoutManager
        binding.recyclerNotificationList.adapter = adapeter
        binding.recyclerNotificationList.addOnScrollListener(object :PaginationScrollListener(mlayoutManager!!){
            override fun loadMoreItems() {
                isPageLoading = true
                vm.isLoading.value = true
                getMoreItems()
            }

            override val totalPageCount: Int
                get(){
                    return vm.totalPages
                }
            override val isLastPage: Boolean
                get() {
                    return vm.isLastPage
                }
            override val isLoading: Boolean
                get() {
                    return isPageLoading
                }

        })
    }

    private fun getMoreItems() {
        vm.previousPage = vm.currentPage
        vm.currentPage += 1
        vm.getNotifications()
    }


    override fun getLayoutId() = R.layout.fragment_notification

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    override fun addList(list: ArrayList<NotificationData>) {
        adapeter!! .addData(list)
    }


    override fun stopLoader() {
        isPageLoading = false
    }
    override fun mentionLastPage() {
        if (vm.currentPage != 1) {
            isPageLoading = false
            vm.isLastPage = true
        }
    }

    override fun startSimmer() {
        vm.showShimmer.set(true)
        binding.shimmerViewNotification.startShimmerAnimation()
    }

    override fun stopSimmer() {
        vm.showShimmer.set(false)
        binding.shimmerViewNotification.stopShimmerAnimation()
    }

}