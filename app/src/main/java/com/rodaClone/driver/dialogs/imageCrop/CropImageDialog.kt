package com.rodaClone.driver.dialogs.imageCrop

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseDialog
import com.rodaClone.driver.databinding.CropImageFragBinding
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.Utilz
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import javax.inject.Inject


class CropImageDialog:BaseDialog<CropImageFragBinding,CropImageDialogVm>(),CropImageNavigator{
    companion object {
        const val TAG = "CropImageDialog"
    }
    lateinit var imageView:CropImageView
    lateinit var ok_text:TextView
    lateinit var backImg:ImageView
    private var cropImage = registerForActivityResult(CropImageContract()) { result ->
        when {
            result.isSuccessful -> {
                Log.v("Bitmap", result.bitmap.toString())
                Log.v("File Path", context?.let { result.getUriFilePath(it) }.toString())

            }
            result is CropImage.CancelledResult -> {

            }
            else -> {

            }
        }
    }

    var imageUri:Uri? = null
        get() = field
        set(value) {
            field = value
        }

    private lateinit var binding: CropImageFragBinding


    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(CropImageDialogVm::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        setDialogFullSCreen()
        ok_text = view.findViewById(R.id.choose_crop)
        imageView = view.findViewById(R.id.cropImageView)
        imageView.setImageUriAsync(imageUri)
        imageView.isAutoZoomEnabled= true
        backImg = view.findViewById(R.id.back_crop)
        imageView.setMaxCropResultSize(1600,1200)
       // binding.showProfileImage.setImageURI(imageUri)
        //startCameraWithUri()

        ok_text.setOnClickListener{
            val bitmap = imageView.getCroppedImage(120,120)
            val uri = getImageUri(requireContext(),bitmap!!)
            if (uri != null){
                val intent = Intent(Config.RECEIVE_PROFILE_IMAGE)
                intent.putExtra(Config.PASS_URI,uri.toString())
                context?.let {
                        it1 -> LocalBroadcastManager.getInstance(it1).sendBroadcast(intent) }
                dialog?.dismiss()
            }

        }
        backImg.setOnClickListener{
            dialog?.dismiss()
        }
    }

    private fun startCameraWithUri() {
        cropImage.launch(
            options(imageUri) {
                setScaleType(CropImageView.ScaleType.FIT_CENTER)
                setCropShape(CropImageView.CropShape.RECTANGLE)
                setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                setAspectRatio(1, 1)
                setMaxZoom(4)
                setAutoZoomEnabled(true)
                setMultiTouchEnabled(true)
                setCenterMoveEnabled(true)
                setShowCropOverlay(true)
                setAllowFlipping(true)
                setSnapRadius(3f)
                setTouchRadius(48f)
                setInitialCropWindowPaddingRatio(0.1f)
                setBorderLineThickness(3f)
                setBorderLineColor(Color.argb(170, 255, 255, 255))
                setBorderCornerThickness(2f)
                setBorderCornerOffset(5f)
                setBorderCornerLength(14f)
                setBorderCornerColor(WHITE)
                setGuidelinesThickness(1f)
                setGuidelinesColor(R.color.white)
                setBackgroundColor(Color.argb(119, 0, 0, 0))
                setMinCropWindowSize(24, 24)
                setMinCropResultSize(20, 20)
                setMaxCropResultSize(99999, 99999)
                setActivityTitle("")
                setActivityMenuIconColor(0)
                setOutputUri(null)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setOutputCompressQuality(90)
                setRequestedSize(0, 0)
                setRequestedSize(0, 0, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                setInitialCropWindowRectangle(null)
                setInitialRotation(0)
                setAllowCounterRotation(false)
                setFlipHorizontally(false)
                setFlipVertically(false)
                setCropMenuCropButtonTitle("Crop")
                setCropMenuCropButtonIcon(0)
                setAllowRotation(true)
                setNoOutputImage(false)
                setFixAspectRatio(false)
            }
        )
    }

    private fun getImageUri(context: Context, inImage: Bitmap): URI? {
//        val bytes = ByteArrayOutputStream()
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//        val path =
//            MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
//        return Uri.parse(path)
        val file = File(context.cacheDir,"CUSTOM NAME") //Get Access to a local file.
        file.delete() // Delete the File, just in Case, that there was still another File
        file.createNewFile()
        val fileOutputStream = file.outputStream()
        val byteArrayOutputStream = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream)
        val bytearray = byteArrayOutputStream.toByteArray()
        fileOutputStream.write(bytearray)
        fileOutputStream.flush()
        fileOutputStream.close()
        byteArrayOutputStream.close()

        val URI = file.toURI()
        return URI
    }


    override fun getLayout(): Int = R.layout.crop_image_frag


    override fun getBindingVariable(): Int = BR.viewModel


    override fun getViewModel(): CropImageDialogVm = vm

    override fun showSnackBar(message: String) =
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

    override fun isNetworkConnected(): Boolean = Utilz.checkForInternet(requireActivity())

}