package com.rodaClone.driver.drawer.searchPlace

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaa.client.drawer.searchPlace.adapter.SearchPlaceAdapter
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.FavPlace
import com.rodaClone.driver.databinding.FragSearchPlaceBinding
import com.rodaClone.driver.ut.Config
import javax.inject.Inject

class FragSearchPlace : BaseFragment<FragSearchPlaceBinding, SearchPlaceVm>(),
    SearchPlaceNavigator {

    private var adapter: SearchPlaceAdapter? = null
    lateinit var binding: FragSearchPlaceBinding

    //private val args: FragSearchPlaceArgs by navArgs()
    private var pickupAddress = ""
    private var pickupLat: Double? = null
    private var pickupLong: Double? = null


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(SearchPlaceVm::class.java)
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
        setUpAdapter()
        arguments?.let {
            pickupAddress = it.getString(Config.pickupAddress).toString()
            pickupLat = it.getDouble(Config.pick_lat)
            pickupLong = it.getDouble(Config.pick_lng)
        }

    }


    override fun getLayoutId() = R.layout.frag_search_place

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        findNavController().popBackStack()
    }

    override fun itemSelected(favPlace: FavPlace.Favourite?) {
        hideKeyboard(requireActivity())
        binding.ACPlaceEdit.clearFocus()
        vm.dropAddress.set(favPlace!!.address)
        vm.getLatLngFromAddress(vm.dropAddress.get()!!)
    }


    override fun addList(favPlace: List<FavPlace.Favourite>?) {
        if (favPlace != null) {
            vm.showRecylers.set(true)
            adapter!!.addList(favPlace)
        }
    }

    override fun getCtx(): Context {
        return requireContext()
    }

    override fun backPressed() {
        if (isAdded)
            hideKeyboard(requireActivity())
        closeFragment()
    }

    override fun goToInstantTrip() {
        val bundle = bundleOf(
            Config.drop_address to vm.dropAddress.get()!!,
            Config.updatedLat to vm.updatedLatLng.get()!!.latitude,
            Config.updatedLng to vm.updatedLatLng.get()!!.longitude,
            Config.pick_lat to pickupLat,
            Config.pick_lng to pickupLong,
            Config.pickupAddress to pickupAddress,
            Config.dropaddresspressed to true
        )
        if (findNavController().currentDestination?.label == "SearchPlace")
            findNavController().navigate(R.id.search_place_to_instant_trip, bundle)
    }

    override fun showOutStationDialog() {

    }

    private fun setUpAdapter() {
        adapter = SearchPlaceAdapter(ArrayList<FavPlace.Favourite>(), vm.session, vm.mConnect, this)
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.placesRecycler.layoutManager = mLayoutManager
        binding.placesRecycler.itemAnimator = DefaultItemAnimator()
        binding.placesRecycler.adapter = adapter
    }
}