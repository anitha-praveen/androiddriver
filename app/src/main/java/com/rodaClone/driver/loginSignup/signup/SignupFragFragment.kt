package com.rodaClone.driver.loginSignup.signup

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.CompanyModel
import com.rodaClone.driver.databinding.SignupFragBinding
import com.rodaClone.driver.loginSignup.CustomSpinnerAdapter
import com.rodaClone.driver.loginSignup.SignupData
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import javax.inject.Inject
import android.text.Spannable

import android.text.style.RelativeSizeSpan

import android.text.SpannableString
import com.rodaClone.driver.ut.Config


class SignupFragFragment : BaseFragment<SignupFragBinding, SignupFragVM>(),
    SignupFragNavigator {
    companion object {
        const val TAG = "SignupFragment"
    }

    private lateinit var binding: SignupFragBinding
    lateinit var data: AvailableCountryAndKLang



    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(SignupFragVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isAdded)
                    Utilz.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.SignuptoLogin)
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(true)
        } else {
            requireActivity().window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

//        requireActivity().getWindow()
//            .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        arguments?.let {
            vm.phonenumber.set(it.getString(Config.phone_number))
            vm.countrycode.set(it.getString(Config.country_code))
            vm.countrycodeID.set(it.getString(Config.country_code_id))
        }

        binding.backImg.setOnClickListener {
            if (isAdded)
                Utilz.hideKeyboard(requireActivity())
            findNavController().navigate(R.id.SignuptoLogin)
        }
        vm.serviceLocApicall()
        setCustomHints()

    }


    override fun getLayoutId() = R.layout.signup_frag

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm
    override fun hideKeyBoard() {

    }

    override fun getContextAttached(): Context {
        return requireContext()
    }

    override fun openVehicleFrag() {
        SignupData.phoneNumber = vm.phonenumber.get()!!
        SignupData.countryCodeForDisplay = vm.countrycode.get()!!
        SignupData.email = vm.email.get()!!
        SignupData.fname = vm.fname.get()!!
        SignupData.lastname = vm.lname.get()!!
        SignupData.service_location = vm.serviceLocationSlug.get()!!
        SignupData.countrycodeID = vm.countrycodeID.get()!!
        SignupData.referral_code = vm.referralCode.get()!!
        SignupData.login_method = vm.loginMethod
        SignupData.device_info_hash = vm.session.getString(SessionMaintainence.FCM_TOKEN)!!
        if (vm.companyRadio.get()) {
            SignupData.isCompanySelected = true
            if (vm.selectedCompanySlug == "OTHER") {
                SignupData.isOtherCompany = true
                SignupData.company_name = vm.companyName.get()!!
                SignupData.company_phone = vm.companyPhone.get()!!
                SignupData.company_no_of_vehicles = vm.companyVehicleNumbers.get()!!
            } else {
                SignupData.isOtherCompany = false
                SignupData.company_slug = vm.selectedCompanySlug
            }
        } else {
            SignupData.isCompanySelected = false
        }
        findNavController().navigate(R.id.toVehicle)
    }


    var dialog: Dialog? = null
    override fun serviceLocationSelection(signupServiceLocList: ArrayList<SignupServiceLocData>) {
        dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.spinner_like_layout)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val recyclerView: RecyclerView = dialog!!.findViewById(R.id.spinner_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val serviceLocAdapter = CustomSpinnerAdapter(signupServiceLocList, this, previouslySelected)
        recyclerView.adapter = serviceLocAdapter
        dialog?.show()

    }

    var previouslySelected = ""
    override fun setServiceLocation(sLocation: SignupServiceLocData) {
        vm.serviceLocationSlug.set(sLocation.slug)
        vm.selectedServiceLocation.set(sLocation.zoneName)
        previouslySelected = sLocation.slug!!
        dialog?.dismiss()
    }

    var companyDialog: Dialog? = null
    var previousSelCompany = ""
    var customList: MutableList<CompanyModel.Company> = ArrayList()
    override fun companySelection() {
        companyDialog = Dialog(requireContext())
        companyDialog?.setContentView(R.layout.spinner_like_layout)
        companyDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        var customClass = CompanyModel.Company()
        customClass.firstname = "OTHER"
        customClass.slug = "OTHER"
        var lastIndex = 0
        customList.clear()
        vm.companyList.forEachIndexed { index, company ->
            customList.add(company)
            lastIndex = index
        }
        customList.add(lastIndex + 1, customClass)
        val recyclerView: RecyclerView = companyDialog!!.findViewById(R.id.spinner_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val serviceLocAdapter = CustomSpinnerAdapter(customList, this, previousSelCompany)
        recyclerView.adapter = serviceLocAdapter
        companyDialog?.show()

    }

    override fun setSelectedCompany(company: CompanyModel.Company) {
        if (company.slug.equals("OTHER", true)) {
            vm.showOtherCompanyOptions.set(true)
        } else {
            vm.showOtherCompanyOptions.set(false)
        }
        company.slug?.let { vm.selectedCompanySlug = it }
        company.slug?.let { previousSelCompany = it }
        company.firstname?.let { vm.selectedCompany.set(it) }
        companyDialog?.dismiss()

    }

    override fun getBinds(): SignupFragBinding {
        return binding
    }

    private fun setCustomHints() {
        val firstNameHint = SpannableString(vm.translationModel?.txt_first_name_hint)
        firstNameHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_first_name_hint?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.etFname.hint = firstNameHint

        val lastNameHint = SpannableString(vm.translationModel?.txt_last_name_hint)
        lastNameHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_last_name_hint?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.lname.hint = lastNameHint

        val emailHint = SpannableString(vm.translationModel?.txt_mail_hint)
        emailHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_mail_hint?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.editEmailSignup.hint = emailHint

        val serviceLocationHint = SpannableString(vm.translationModel?.txt_select_service_location)
        serviceLocationHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_select_service_location?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textView.hint = serviceLocationHint

        val refferalHint = SpannableString(vm.translationModel?.refferal_code)
        refferalHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.refferal_code?.length ?: 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.editRefferalCodeSignup.hint = refferalHint

        val companyListHint = SpannableString(vm.translationModel?.txt_select_company)
        companyListHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_select_company?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.companyList.hint = companyListHint

        val companyNameHint = SpannableString(vm.translationModel?.txt_company_name)
        companyNameHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_company_name?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.companyName.hint = companyNameHint

        val companyPhoneHint = SpannableString(vm.translationModel?.txt_company_phone)
        companyPhoneHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_company_phone?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.companyPhone.hint = companyPhoneHint

        val vehicleCountHint = SpannableString(vm.translationModel?.txt_number_of_vehicles)
        vehicleCountHint.setSpan(
            RelativeSizeSpan(0.75f),
            0,
            vm.translationModel?.txt_number_of_vehicles?.length!!,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.companyVehicleNumbers.hint = vehicleCountHint


    }
}