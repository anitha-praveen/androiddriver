package com.rodaClone.driver.drawer.approvalDecline

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.databinding.ApprovalDeclineBinding
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.ut.Config
import javax.inject.Inject

class ApprovalFragment : BaseFragment<ApprovalDeclineBinding, ApprovalVM>(),
    ApprovalNavigator {
    var adminNumber = ""
    var headOfficeNum = ""
    lateinit var _binding: ApprovalDeclineBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFactory).get(ApprovalVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                close()
            }
        })
        vm.printE("data--", "__" + requireArguments().getString(Config.APPROVE_STATUS))
        vm.printE("data--", "__" + requireArguments().getString(Config.DOCS_STATUS))
        status = requireArguments().getString(Config.DOCS_STATUS)!!
        blockReason = requireArguments().getString(Config.BLOCK_REASON)!!
        headOfficeNum = requireArguments().getString(Config.CUSTOMER_CARE_NUM )?:""
        adminNumber = requireArguments().getString(Config.HELPLINE_NUM)?:""

    }

    var status = ""
    var blockReason = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)
        vm.showSubscribe.set(requireArguments().getBoolean(Config.SHOW_SUBSCRIBE, false))
/*       case notUploaded = 2
        case uploadedWaitingForApproval = 4
        case modifiedWaitingForApproval = 3
        case approved = 1
        case expired = 5 */
        if (status == "2" || status == "5") {
            vm.title_reson.set(vm.translationModel?.txt_thank_u_time)
            vm.buttonText.set(vm.translationModel?.text_upload_doc)
        }
        else {
            vm.title_reson.set(vm.translationModel?.txt_contact_admin_blocked)
            vm.buttonText.set(vm.translationModel?.text_call_admin)
        }

        when (status) {
            "2" -> vm.reason.set(vm.translationModel?.text_driver_notUploaded)
            "3" -> vm.reason.set(vm.translationModel?.text_driver_modifiedWaitingForApproval)
            "4" -> vm.reason.set(vm.translationModel?.text_driver_uploadedWaitingForApproval)
            "5" -> vm.reason.set(vm.translationModel?.doc_expired)
            else -> vm.reason.set(blockReason)
        }
    }


    override fun getLayoutId() = R.layout.approval_decline

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private var backPressed = false
    fun close() {
        if (backPressed)
            requireActivity().finishAffinity()
        else
            showMessage(vm.translationModel?.txt_press_back_again ?:"")
        backPressed = true
        Handler(Looper.getMainLooper()).postDelayed(
            { backPressed = false },
            2000
        )

    }

    override fun handleClick() {
        if (status == "2" || status == "5")
            findNavController().navigate(R.id.approvalToDocuments)
        else
            callAdmin()
    }

    override fun openSubscription() {
        val bundle = bundleOf("CALL_REQUEST_IN_PROGRESS_WHEN_BACK_PRESSED" to true)
        findNavController().navigate(R.id.approvalToSubscription, bundle)
    }

    override fun openSideMenu() {
        (requireActivity() as DrawerActivity).openSideMenu()

    }

    var phoneNumbers: Dialog? = null
    private fun callAdmin() {
        if (phoneNumbers == null || !phoneNumbers?.isShowing!!) {
            phoneNumbers = Dialog(requireContext())
            phoneNumbers?.setContentView(R.layout.dialog_admin_contact_list)
            phoneNumbers?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            phoneNumbers?.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            val phoneOne = phoneNumbers?.findViewById<TextView>(R.id.phone1)
            val phoneTwo = phoneNumbers?.findViewById<TextView>(R.id.phone2)

            if (headOfficeNum.isNotEmpty() && adminNumber.isNotEmpty()){
                phoneOne?.text = headOfficeNum
                phoneTwo?.text = adminNumber
            }


            phoneOne?.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                    phoneOne.text.toString(), null))
                startActivity(intent)
                phoneNumbers?.dismiss()
            }

            phoneTwo?.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                    phoneTwo.text.toString(), null))
                startActivity(intent)
                phoneNumbers?.dismiss()
            }
            phoneNumbers?.show()
        }

    }

}