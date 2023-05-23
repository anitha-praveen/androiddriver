package com.rodaClone.driver.drawer.subsription

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.SubscriptionModel
import com.rodaClone.driver.databinding.FragmentSubscriptionBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.subsription.adapter.SubscriptionAdapter
import com.rodaClone.driver.ut.SessionMaintainence
import java.util.ArrayList
import javax.inject.Inject

class SubscriptionFragment : BaseFragment<FragmentSubscriptionBinding, SubscriptionVm>(),
    SubscriptionNavigator {

    lateinit var binding: FragmentSubscriptionBinding

    companion object {
        const val TAG = "SubscriptionFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(SubscriptionVm::class.java)
    }

    var callReqProgInBackPress = false
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
        arguments?.let {
            callReqProgInBackPress =
                it.getBoolean("CALL_REQUEST_IN_PROGRESS_WHEN_BACK_PRESSED", false)
        }
        val lang = vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)
        if (lang == "en")
            vm.isenglish.set(true)
        else
            vm.isenglish.set(false)
        vm.getApi()
    }


    override fun getLayoutId() = R.layout.fragment_subscription

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (callReqProgInBackPress)
            (requireActivity() as DrawerActivity).navigateFirstTabWithClearStack()
        else
            findNavController().popBackStack()
    }

    override fun setAdapter(list: ArrayList<SubscriptionModel>) {
        val layoutManager = LinearLayoutManager(context)

        val adapter =
            SubscriptionAdapter(
                list,
                vm.translationModel,
                vm.allowSubscription.get(),
                vm.currencySymbol.get(),
                this
            )
        binding = getmBinding()
        vm.setNavigator(this)
        binding.toolbar.setNavigationOnClickListener { closeFragment() }
        binding.recylerSubscription.layoutManager = layoutManager
        binding.recylerSubscription.adapter = adapter


    }

    override fun setImage(url: String) {
        context?.let {
            Glide.with(it).load(url).apply(
                RequestOptions.circleCropTransform().error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
            ).into(binding.showImageSub)
        }
    }

    override fun clickedItem(id: String?) {
        vm.addSubscription(id)
    }
}
