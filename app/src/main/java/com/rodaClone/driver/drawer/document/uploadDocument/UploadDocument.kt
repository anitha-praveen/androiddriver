package com.rodaClone.driver.drawer.document.uploadDocument

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.GetDocument
import com.rodaClone.driver.connection.responseModels.GroupDocument
import com.rodaClone.driver.databinding.UploadDocumentFragmentBinding
import com.rodaClone.driver.drawer.document.uploadDocument.adapter.DcUploadImageAdapter
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SpaceDecoration
import com.rodaClone.driver.ut.Utilz
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/* val document: Document */
class UploadDocument :
    BaseFragment<UploadDocumentFragmentBinding, UploadDocumentVM>(),
    UploadDocumentNavigator {

    private lateinit var binding: UploadDocumentFragmentBinding
    private lateinit var adapter: DcUploadImageAdapter
    private lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var vmProviderFactory: ViewModelProvider.Factory
    private val vm by lazy {
        ViewModelProvider(this, vmProviderFactory).get(UploadDocumentVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getmBinding()
        vm.setNavigator(this)
        arguments.let {
            if (it != null) {
                val rawData = it.getSerializable(Config.groupDocument_b)
                val groupDocumentRaw =
                    Gson().fromJson(Gson().toJson(rawData), GroupDocument::class.java)
                vm.groupDocument = groupDocumentRaw
                setUpadapter()
            }
        }
        initializeValues()
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpadapter() {
        adapter =
            vm.groupDocument?.getDocument?.let { DcUploadImageAdapter(it, this, vm.session) }!!
        layoutManager = GridLayoutManager(requireContext(), 2)
        binding.imageUploadRecycler.adapter = adapter
        binding.imageUploadRecycler.layoutManager = layoutManager
        binding.imageUploadRecycler.addItemDecoration(SpaceDecoration(20))

    }

    private fun initializeValues() {

        vm.groupDocument?.getDocument?.forEach first@{ document ->
            if (document.identifier == 1) {
                vm.getIdentifier.set(true)
                document.identifier_document?.let { vm.identifier.set(it) }
                return@first
            }
        }
        if (vm.getIdentifier.get())
            vm.identifierTitleText.set("${vm.translationModel?.txt_enter} ${vm.groupDocument?.name} ${vm.translationModel?.txt_number}")
        vm.documentImageUploadTitle.set("${vm.groupDocument?.name}")
        /*
         if expiry date-> 0 No expiry or issue date
                             1 Expiry date required
                             2 Issue date required
         */
        vm.groupDocument?.getDocument?.get(0)?.expiryDate?.let { vm.dateType.set(it) }
        if (vm.groupDocument?.getDocument?.get(0)?.expiryDate == 0)
            vm.getDate.set(false)
        else {
            vm.getDate.set(true)
            if (vm.groupDocument?.getDocument?.get(0)?.expiryDate == 1) {
                vm.dateHintText.set(vm.translationModel?.txt_Exp_date)
                if (vm.groupDocument?.getDocument?.get(0)?.expiryDated != null && vm.groupDocument?.getDocument?.get(
                        0
                    )?.expiryDated != "0.0"
                ) {
                    vm.expiryOrIssueDate.set(vm.groupDocument?.getDocument?.get(0)?.expiryDated)
                    vm.apiExpiryorIssueDate.set(vm.groupDocument?.getDocument?.get(0)?.issueDate)
                }
            } else if (vm.groupDocument?.getDocument?.get(0)?.expiryDate == 2) {
                vm.dateHintText.set(vm.translationModel?.txt_issued_date)
                if (vm.groupDocument?.getDocument?.get(0)?.issueDate != null && vm.groupDocument?.getDocument?.get(
                        0
                    )?.issueDate != "0.0"
                ) {
                    vm.expiryOrIssueDate.set(vm.groupDocument?.getDocument?.get(0)?.issueDate)
                    vm.apiExpiryorIssueDate.set(vm.groupDocument?.getDocument?.get(0)?.issueDate)
                }
            }
        }

    }


    /*
    The below variables and methods are declared for handling camera or gallery permissions and get data from corresponding selection
     */
    var permissionCamera = false
    var permissionReadExternalStorage = false
    var permissionWriteExternalStorage = false
    var camera: Boolean = false

    /*
      The below declaration receive the result of camera captured image
       */
    private var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.hasExtra("data")) {
                        if (isAdded) {
                            val realPath: String? = Utilz.getImageUri(requireContext(), data.extras?.get("data") as Bitmap
                            )
                            vm.realFile = File(realPath!!)
                            vm.getDocument?.isFromapi = false
                            vm.getDocument?.uri = vm.realFile.toString()
                            if (this::adapter.isInitialized) {
                                vm.getDocument?.let {
                                    vm.adapterPosition.get()
                                        ?.let { position -> adapter.addImage(it, position) }
                                }
                                vm.uploadData()
                            }
                            vm.bitmapUrl.set(realPath)
                        }

                    }
                }
            }
        }

    /*
  The below declaration receive the result of selected image from gallery
   */
    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                vm.onSelectFromGalleryResult(data)
            }
        }

    /*
      The below declaration receive the result of permission allowed or not
       */
    val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.forEach { actionMap ->
                val permissionName = actionMap.key
                val isGranted = actionMap.value
                when {
                    permissionName.equals("android.permission.CAMERA") -> {
                        permissionCamera = isGranted
                    }
                    permissionName.equals("android.permission.READ_EXTERNAL_STORAGE") -> {
                        permissionReadExternalStorage = isGranted
                    }
                    else -> {
                        permissionWriteExternalStorage = isGranted
                    }
                }
            }
            //if (permissionCamera && permissionReadExternalStorage && permissionWriteExternalStorage) {
                if (camera) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    resultLauncherCamera.launch(intent)
                } else {
                    val pickIntent = Intent(Intent.ACTION_PICK)
                    pickIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
                    )
                    resultLauncherGallery.launch(pickIntent)
                }
           // }

        }

    var photoFile: File? = null
    override fun getCtx() = if(isAdded) requireContext() else null

    /** Open camera  */
    override fun cameraIntent() {
        camera = true
        if (!hasPermissions(requireContext(), Config.storagePermission)) {
            requestMultiplePermissions.launch(Config.storagePermission)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncherCamera.launch(intent)
        }
    }


    /** Open Gallery */
    override fun galleryIntent() {
        camera = false
        if (!hasPermissions(requireContext(), Config.storagePermission)) {
            requestMultiplePermissions.launch(Config.storagePermission)
        } else {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            resultLauncherGallery.launch(pickIntent)
        }
    }

    override fun openGalleryorCamera(document: GetDocument, position: Int) {
        if (vm.validation()) {
            vm.slug = document.slug.toString()
            vm.getDocument = document
            vm.adapterPosition.set(position)
            openGalleryOrCamera()
        }
    }

    override fun notifyAdapterImage(file: String) {
        if (this::adapter.isInitialized) {
            vm.getDocument?.isFromapi = false
            vm.getDocument?.uri = file
            vm.getDocument?.let { getDocument ->
                vm.adapterPosition.get()?.let { adapter.addImage(getDocument, it) }
            }
        }
    }

    override fun goToDocumentList() {
        findNavController().navigate(R.id.document_list)
    }

    override fun removeImages(document: ArrayList<GetDocument>) {
        if (this::adapter.isInitialized) {
            vm.groupDocument?.getDocument?.let { adapter.addNullImageList(it) }
        }
    }


    override fun getLayoutId() = R.layout.upload_document_fragment

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm

    private fun openGalleryOrCamera() {
        val items = arrayOf(
            vm.translationModel?.text_camera,
            vm.translationModel?.text_galary
        )
        val builder1 = AlertDialog.Builder(requireContext())
        builder1.setTitle(vm.translationModel?.txt_Choose)
        builder1.setCancelable(true)
        builder1.setItems(
            items
        ) { dialog: DialogInterface?, which: Int ->
            if (which == 0) {
                cameraIntent()
                camera = true
            } else if (which == 1) {
                galleryIntent()
                camera = false
            }
        }.show()
    }

}