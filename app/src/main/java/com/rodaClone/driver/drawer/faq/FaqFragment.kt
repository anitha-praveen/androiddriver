package com.rodaClone.driver.drawer.faq

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.Faq
import com.rodaClone.driver.databinding.FragmentFaqBinding
import com.rodaClone.driver.drawer.faq.adapter.FAQAdapter
import com.rodaClone.driver.drawer.faq.adapter.FaqNewAdapter
import javax.inject.Inject

class FaqFragment : BaseFragment<FragmentFaqBinding, FaqVM>(),
    FaqNavigator {

    lateinit var _binding: FragmentFaqBinding
    lateinit var adapter : FAQAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(FaqVM::class.java)
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
        _binding = getmBinding()
        vm.setNavigator(this)
        _binding.backImg.setOnClickListener { closeFragment() }
        vm.getFAQListBase()
    }


    override fun getLayoutId() = R.layout.fragment_faq

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment(){
        findNavController().popBackStack()
    }

    override fun setFaqAdapter(list: ArrayList<Faq>) {
        val layoutManager = LinearLayoutManager(context)
        val adapter = FaqNewAdapter(list)
        _binding.faqRecyclerView.layoutManager = layoutManager
        _binding.faqRecyclerView.adapter = adapter
    }

//    override fun setFaqAdapter(
//        titleList: List<String>?,
//        faqMap: HashMap<String, MutableList<Faq>>
//    ) {
////        adapter = FAQAdapter(requireContext(), titleList!!, faqMap)
////        _binding.faqList.setAdapter(adapter)
//    }

}