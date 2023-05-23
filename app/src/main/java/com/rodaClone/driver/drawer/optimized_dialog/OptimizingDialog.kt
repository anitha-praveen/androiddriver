package com.rodaClone.driver.drawer.optimized_dialog

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.TranslationModel
import java.util.*

class OptimizingDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "optimizingdialog"
        lateinit var translationModel: TranslationModel
        fun newInstance(mTranslationModel: TranslationModel): OptimizingDialog {
            translationModel = mTranslationModel
            return OptimizingDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.battery_optimization_dialog, container, false)
    }

    private var resultBatteryOptimization =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.logout_ok_button)?.setOnClickListener {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            resultBatteryOptimization.launch(intent)
            dismiss()
        }

    }

}