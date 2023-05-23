package com.rodaClone.driver.drawer.sos

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.Sos
import com.rodaClone.driver.databinding.FragmentSosBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.sos.adapter.SosAdapter
import com.rodaClone.driver.ut.SessionMaintainence
import java.util.*
import javax.inject.Inject

class SosFragment : BaseFragment<FragmentSosBinding, SosVM>(),
    SosNavigator {

    lateinit var _binding: FragmentSosBinding
    var adapter: SosAdapter? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(SosVM::class.java)
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
        setupAdapter()
    }


    override fun getLayoutId() = R.layout.fragment_sos

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    fun closeFragment() {
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

    private fun setupAdapter() {
        adapter = SosAdapter(ArrayList<Sos>(), this)
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding.recyclerSos.layoutManager = mLayoutManager
        _binding.recyclerSos.itemAnimator = DefaultItemAnimator()
        _binding.recyclerSos.adapter = adapter
        vm.getSosList()
    }

    override fun onPhoneClick(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + number.trim { it <= ' ' })
        startActivity(callIntent)
    }

    override fun addList(sos: List<Sos>?) {
        adapter!!.addList(sos!!)
    }

    override fun showSosAdd() {
        if (isAdded) {
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    mActivity as Context,
                    R.color.clr_transparent
                )
            )
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.add_sos_layout)
            val heading: TextView = dialog.findViewById(R.id.heading)
            val subHeading1: TextView = dialog.findViewById(R.id.sub_heading_1)
            val subHeading2: TextView = dialog.findViewById(R.id.sub_heading_2)
            val nameEdit: EditText = dialog.findViewById(R.id.name_edit)
            val phoneEdit: EditText = dialog.findViewById(R.id.phone_edit)
            val submit: AppCompatButton = dialog.findViewById(R.id.submit_button)
            heading.text = vm.translationModel?.txt_add_emergency
            subHeading1.text = vm.translationModel?.name
            subHeading2.text = vm.translationModel?.hint_phone_number
            submit.text = vm.translationModel?.text_submit
            submit.setOnClickListener {
                when {
                    nameEdit.text.isEmpty() -> {
                        showSnackBar("Please enter contact name")
                    }
                    phoneEdit.text.isEmpty() -> {
                        showSnackBar("Please enter contact number")
                    }
                    else -> {
                        vm.saveSos(nameEdit.text.toString(), phoneEdit.text.toString())
                        dialog.dismiss()
                    }
                }
            }
            dialog.show()
            dialog.setOnKeyListener { arg0, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss()
                    true
                } else
                    false
            }
        }
    }

    override fun deleteSosNav(slug: String) {
        vm.deleteSos(slug)
    }



}