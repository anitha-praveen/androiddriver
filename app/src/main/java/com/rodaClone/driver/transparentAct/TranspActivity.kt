package com.rodaClone.driver.transparentAct

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseAppActivity
import com.rodaClone.driver.databinding.ActivityTranspBinding
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject

class TranspActivity : BaseAppActivity<ActivityTranspBinding,TransparentVM>(),
    TransparentActNav {

    private lateinit var binding: ActivityTranspBinding

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory

    val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(TransparentVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        if (!vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE).isNullOrEmpty()) {
            vm.setLanguage(this, vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE)!!)
        }else{
            vm.setLanguage(this,"en")
        }
        setMax(15)
        vm.tripTxt.set(vm.translationModel?.txt_new_trip_req ?:"")
        vm.mainHandler.post(vm.progressRunner)

    }

    override fun getLayoutId() = R.layout.activity_transp
    override fun getBR() = BR.viewModel
    override fun getVMClass() = vm
    override fun getCtx(): Context {
        return this
    }

    override fun setProg(pro: Int) {
        binding.progressBar.progress = pro
    }

    override fun setMax(max: Int) {
        binding.progressBar.max = max
    }

    override fun onResume() {
        super.onResume()
        vm.playSound()
    }

    override fun onPause() {
        super.onPause()
        vm.stopSound()
    }

    override fun onBackPressed() {
    }

}