package com.rodaClone.driver.dialogs.notification_disclosure

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.TranslationModel


class NotificationPermissionDisclosure : DialogFragment() {

    companion object {
        const val TAG = "NotificationPermissionDisclosure"
        var translationModel: TranslationModel? = null
        fun newInstance(
            mTranslationModel: TranslationModel?,
        ): NotificationPermissionDisclosure {
            translationModel = mTranslationModel
            return NotificationPermissionDisclosure()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.dialog_notification_disclosure, container, false)
    }


    lateinit var subtitle: TextView
    lateinit var desc: TextView
    lateinit var imin: Button
    lateinit var skip: Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        subtitle = view.findViewById(R.id.subtitle)
        desc = view.findViewById(R.id.desc)
        imin = view.findViewById(R.id.imin)
        skip = view.findViewById(R.id.skip)

        subtitle.text = translationModel?.txt_get_notified
        desc.text = translationModel?.txt_notification_permission_desc_user
        imin.text = translationModel?.txt_imin
        skip.text = translationModel?.txt_skip

        imin.setOnClickListener {
            val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName())
                .putExtra(Settings.EXTRA_CHANNEL_ID, 0)
            startActivity(settingsIntent)
            dismiss ()
        }

        skip.setOnClickListener {
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }


}