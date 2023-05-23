package com.rodaClone.driver.drawer.document.uploadDocument

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import java.util.*
import javax.inject.Inject
import android.provider.MediaStore
import android.util.Log
import com.rodaClone.driver.connection.responseModels.DocumentUploadResult
import com.rodaClone.driver.connection.responseModels.GetDocument
import com.rodaClone.driver.connection.responseModels.GroupDocument
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull


class UploadDocumentVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, UploadDocumentNavigator>(session, mConnect) {

    var title = ObservableField("")
    var bitmapUrl = ObservableField("")
    var uploadIconVisibility: ObservableBoolean = ObservableBoolean(bitmapUrl.get()!!.isEmpty())
    var getDate = ObservableBoolean(false)
    var expiryOrIssueDate = ObservableField("")
    var dateHintText = ObservableField("")
    var isUploadClicked = ObservableBoolean(false)
    var datePickerDialog: DatePickerDialog? = null
    var cal = Calendar.getInstance()
    val apiExpiryorIssueDate = ObservableField("")
    var isDocumentChanged = ObservableBoolean(false)
    var isDateChanged = ObservableBoolean(false)
    var dateType = ObservableField(0)
    private var dateSetListener =
        OnDateSetListener { view, year, month, dayOfMonth ->
            expiryOrIssueDate.set(year.toString() + "-" + (month + 1) + "-" + dayOfMonth)
            if (groupDocument?.getDocument != null && !isDateChanged.get()) {
                groupDocument?.getDocument?.forEach {
                    it.uri = null
                    it.isFromapi = false
                }
            }
            isDateChanged.set(true)
            isDocumentChanged.set(true)
            groupDocument?.getDocument?.let { getNavigator().removeImages(it) }
        }
    var realPath: String? = null
    var realFile: File? = null
    var slug = ""
    var documentImageUploadTitle = ObservableField("")
    var getIdentifier = ObservableBoolean(false)
    var identifierTitleText = ObservableField("")
    var identifier = ObservableField("")
    var adapterPosition = ObservableField<Int>()

    var getDocument: GetDocument? = null

    var groupDocument: GroupDocument? = null
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.success == true) {
            val resultDoc = Gson().fromJson(Gson().toJson(response.data), DocumentUploadResult::class.java)
            groupDocument?.getDocument?.let { documentList ->
                for(document in documentList){
                    if(document.id == resultDoc.documentId){
                        document.documentImage = resultDoc.documentImage
                    }
                }
            }
            val intent = Intent(Config.NOTIFY_DOC_UPLOADED)
            getNavigator().getCtx()
                ?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }

        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        t?.exception?.let { getNavigator().showMessage(it) }
    }

    fun onEnableChoose(view: View) {
        isUploadClicked.set(true)
    }


    fun onClickDatePicker(view: View) {
        val date = Date()
        cal.time = date
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        datePickerDialog =
            getNavigator().getCtx()?.let { DatePickerDialog(it, dateSetListener, year, month, day) }
        if (dateType.get() == 2)
            datePickerDialog?.datePicker!!.maxDate = cal.timeInMillis
        else
            datePickerDialog?.datePicker!!.minDate = cal.timeInMillis
        if (datePickerDialog != null) {
            if (datePickerDialog?.isShowing!!) datePickerDialog!!.dismiss()
        }
        datePickerDialog?.show()
    }


    fun onClickSubmit(view: View) {
        if (isLoading.value == false){
            var isImageUploaded = true
            if (validation()) {
                if (isDateChanged.get() || isDocumentChanged.get()) {
                    groupDocument?.getDocument?.forEach first@{
                        if (it.documentImage.isNullOrEmpty()) {
                            if (it.requried == 1){
                                isImageUploaded = false
                                return@first
                            }
                        }
                    }
                    if (!isImageUploaded) {
                        translationModel?.txt_upload_document_image?.let { getNavigator().showMessage(it) }

                    } else {
                        getNavigator().goToDocumentList()
                    }
                } else if (!getIdentifier.get() && !getDate.get()) {
                    groupDocument?.getDocument?.forEach second@{
                        Log.e("ImageUpload", "onClickSubmit: ${it.documentName}, isRequired : ${it.requried}, documentImage : ${it.documentName}, uri: ${it.uri}, isImageUploaded: $isImageUploaded", )
                        if (it.documentImage.isNullOrEmpty()) {
                            if (it.requried == 1){
                                isImageUploaded = false
                                return@second
                            }
                        }
                    }
                    if (isImageUploaded) {
                        getNavigator().goToDocumentList()
                    } else {
                        translationModel?.txt_upload_document_image?.let { getNavigator().showMessage(it) }
                    }
                } else {
                    getNavigator().goToDocumentList()
                }
            }
        }
    }

    fun onCaptureImageResult(data: Intent?) {
        if (data != null) {
            if (data.hasExtra("data")) {
                realPath = getNavigator().getCtx()?.let {
                    Utilz.getImageUri(
                        it, data.extras?.get(
                            "data"
                        ) as Bitmap
                    )
                }
                realFile = File(realPath!!)
                bitmapUrl.set(realPath)
            }
        }

    }

    fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            val imageUri: Uri? = data.data
            val fullBitmap =
                MediaStore.Images.Media.getBitmap(
                    getNavigator().getCtx()?.contentResolver,
                    imageUri
                )

            realPath = getNavigator().getCtx()?.let { Utilz.getImageUri(it, fullBitmap) }
            realFile = File(if (realPath == null) "" else realPath ?: "")
            getNavigator().notifyAdapterImage(realFile.toString())
            bitmapUrl.set(realPath ?: "")
            uploadData()
        }
    }

    fun validation(): Boolean {
        var msg = ""
        if (getDate.get() && expiryOrIssueDate.get()!!.isEmpty()) {
            msg = if (dateType.get() == 1) translationModel?.text_error_doc_expiry_empty ?: ""
            else translationModel?.txt_errror_issue_date_empty ?: ""
        } else if (getIdentifier.get() && identifier.get()!!.trim().isEmpty()) {
            msg = identifierTitleText.get()!!
        }
        return if (msg.isEmpty())
            true
        else {
            getNavigator().showSnackBar(msg)
            false
        }
    }

    fun uploadData() {
        if (getNavigator().isNetworkConnected()) {
            if (validation()) {
                requestbody.clear()
                requestbody[Config.document_id] =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), slug)
                requestbody[Config.identifier] =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), identifier.get()!!)
                if (dateType.get() == 1)
                    requestbody[Config.expiry_date] =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), expiryOrIssueDate.get()!!)
                else if (dateType.get() == 2)
                    requestbody[Config.issue_date] =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), expiryOrIssueDate.get()!!)
                if (realFile != null) {
                    val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), realFile!!)
                    body = MultipartBody.Part.createFormData(
                        Config.document_image,
                        realFile!!.name,
                        reqFile
                    )
                    uploadDocumentBase()
                }
            }
        } else
            getNavigator().showNetworkUnAvailable()
    }

    fun onNumberChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (identifier.get()?.isNotEmpty() == true) {
            if (s.toString() != identifier.get() && !isDocumentChanged.get()) {
                if (groupDocument?.getDocument != null) {
                    groupDocument?.getDocument?.forEach {
                        it.uri = null
                        it.isFromapi = false
                    }
                }
                isDocumentChanged.set(true)
                groupDocument?.getDocument?.let { getNavigator().removeImages(it) }
            }
        }
    }

}

