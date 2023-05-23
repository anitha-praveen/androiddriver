package com.rodaClone.driver.dialogs.countrylist

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseDialog
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.Country
import com.rodaClone.driver.databinding.DialogCountryListBinding
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.content.Intent
import android.content.IntentFilter
import com.rodaClone.driver.ut.Config


class CountryListDialog(mode: Int) : BaseDialog<DialogCountryListBinding, CountryListVM>(),
    CountryListNavigator {
    companion object {
        const val TAG = "CountryListDialog"
    }

    private lateinit var binding: DialogCountryListBinding
    var countryListAdapter: CountryListAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    lateinit var data: AvailableCountryAndKLang

    private val noItemVisibilityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getBooleanExtra(Config.RECEIVE_NO_ITEM_FOUND, false).let {
                if (it)
                    view?.let { it1 -> hideKeyBoard(it1) }
                vm.showNoItem.set(it)
            }
        }
    }

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(CountryListVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            noItemVisibilityReceiver,
            IntentFilter(Config.RECEIVE_NO_ITEM_FOUND)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(noItemVisibilityReceiver)
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setDialogFullSCreen()
        setUpCountryList()
    }

    override fun getLayout(): Int = R.layout.dialog_country_list


    override fun getBindingVariable(): Int = BR.viewModel


    override fun getViewModel(): CountryListVM = vm
    override fun clickedItem(country: Country) {
        val intent = Intent(Config.RECEIVE_COUNTRY)
        intent.putExtra(Config.country, country)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        dismiss()
    }

    override fun close() {
        dismiss()
    }

    override fun itemSize(size: Int) {
        if (size > 0) vm.noItem.set(false) else vm.noItem.set(true)
    }


    override fun showSnackBar(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun isNetworkConnected(): Boolean = Utilz.checkForInternet(requireActivity())

    private fun setUpCountryList() {
        data = vm.session.getAvailableCountryAndLanguages()!!
        if (data.country!!.isNotEmpty()) {
            vm.noItem.set(false)
            linearLayoutManager = LinearLayoutManager(activity)
            countryListAdapter = CountryListAdapter(requireActivity(), data.country!!, this)
            binding.recylerview.layoutManager = linearLayoutManager
            binding.recylerview.adapter = countryListAdapter
            binding.searchEdit.addTextChangedListener(countryListAdapter)
        } else vm.noItem.set(true)

    }
}