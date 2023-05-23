package com.rodaClone.driver.drawer.profile

import android.app.Activity
import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView
import com.rodaClone.driver.BR
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseFragment
import com.rodaClone.driver.connection.responseModels.Languages
import com.rodaClone.driver.databinding.ProfileFragLayBinding
import com.rodaClone.driver.dialogs.imageCrop.CropImageDialog
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.loginSignup.getStarteedScrn.LanguageAdapter
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import javax.inject.Inject


class ProfileFragment : BaseFragment<ProfileFragLayBinding, ProfileFragVM>(),
    ProfileFragNavigator{

    lateinit var _binding: ProfileFragLayBinding
    var permissionCamera = false
    var permissionReadExternalStorage = false
    var permissionWriteExternalStorage = false
    var camera: Boolean = false
    var dialog: Dialog? = null
    var isReceiverRegistered = false
    /*
   The below declaration receive the result of camera captured image
    */

//    val resultLauncherCamera = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//        if (uri != null) {
//            Log.d("PhotoPicker", "Selected URI: $uri")
//            binding.imggViewer.setImageURI(uri)
//        } else {
//            Toast.makeText(applicationContext,"", Toast.LENGTH_SHORT).show()
//            Log.d("PhotoPicker", "No profile selected")
//        }
//    }
    var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data!=null ) {
                val data :Intent? = result.data

                vm.onCaptureImageResult(data!!)
            }
        }

    /*
    The below declaration receive the result of selected image from gallery
     */
    var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data!=null) {
                val data: Intent? = result.data
                val cropImageDialog = CropImageDialog()
                data?.data.let {
                    if (it != null) {
                        cropImageDialog.imageUri = it
                        cropImageDialog.show(requireActivity().supportFragmentManager, CropImageDialog.TAG)
                    }
                }


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
                if (permissionName.equals("android.permission.CAMERA")) {
                    permissionCamera = isGranted
                } else if (permissionName.equals("android.permission.READ_EXTERNAL_STORAGE")) {
                    permissionReadExternalStorage = isGranted
                } else {
                    permissionWriteExternalStorage = isGranted
                }
            }
            //if (permissionCamera && permissionReadExternalStorage && permissionWriteExternalStorage) {
                if (camera) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {

                        putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
                        putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                        putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                        putExtra("android.intent.extras.CAMERA_FACING", 1)

                        // Samsung
                        putExtra("camerafacing", "front")
                        putExtra("previous_mode", "front")

                        // Huawei
                        putExtra("default_camera", "1")
                        putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode")
                       }
                    resultLauncherCamera.launch(intent)
                } else {
                    val pickIntent = Intent(Intent.ACTION_PICK)
                    pickIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
                    )
                    resultLauncherGallery.launch(pickIntent)
                }
        //    }

        }

    @Inject
    lateinit var viewModelFagtory: ViewModelProvider.Factory
    val vm by lazy {
        ViewModelProvider(this, viewModelFagtory).get(ProfileFragVM::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (!isReceiverRegistered){
            isReceiverRegistered = true
            context?.let {
                LocalBroadcastManager.getInstance(it).registerReceiver(profileImageUri,
                    IntentFilter(Config.RECEIVE_PROFILE_IMAGE)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(profileImageUri) }
        isReceiverRegistered = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getmBinding()
        vm.setNavigator(this)
        _binding.backImg.setOnClickListener {closeFragment()}
        vm.getProfileDetails()
    }

    override fun getLayoutId() = R.layout.profile_frag_lay

    override fun getBR() = BR.viewModel

    override fun getVMClass() = vm
    override fun setImage(url: String) {
        if(isAdded){
            context?.let {
                Glide.with(it).load(url).apply(
                    RequestOptions.circleCropTransform().error(R.drawable.simple_profile_bg)
                        .placeholder(R.drawable.simple_profile_bg)
                ).into(_binding.imgProfilePicProfile)
            }
        }
    }

    override fun openVehicleInfo() {
        if(vm.driver?.user!=null){
            val bundle = bundleOf("vehicleInfo" to vm.driver?.user!!.carDetails)
            findNavController().navigate(R.id.profile_to_veh_info,bundle)
        }

    }

    override fun getCtx(): Context {
        return requireContext()
    }

    override fun alertSelectCameraGallery() {
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

    override fun openEditProfile(mode: Int,value: String) {
//        val action = ProfileFragmentDirections.profileToEditProfile(mode, value)
//        findNavController().navigate(action)
    }

    override fun openLanguageSelection() {
        vm.data = vm.session.getAvailableCountryAndLanguages()!!
        dialog = Dialog(requireContext())
        dialog!!.setContentView(R.layout.dialogue_language)

        val recyclerView: RecyclerView = dialog!!.findViewById(R.id.lang_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val languageAdapter = LanguageAdapter(vm.data.languages!!, this,vm.session.getString(SessionMaintainence.CURRENT_LANGUAGE) ?: "")
        recyclerView.adapter = languageAdapter

        val setLanguage : MaterialTextView = dialog!!.findViewById(R.id.set_lang)
        val choose : MaterialTextView = dialog!!.findViewById(R.id.title_txt)
        setLanguage.text = vm.translationModel?.txt_set_lang
        choose.text = vm.translationModel?.txt_choose_language

        setLanguage.setOnClickListener {
            if(vm.selectedLanguage.get()?.length == 0){
                dialog!!.dismiss()
            }else{
                vm.getSelectedLanguageTranslation(vm.selectedLanguage.get()!!)
                dialog!!.dismiss()
            }
        }
        dialog!!.show()

    }
    private val profileImageUri: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            val uri = Uri.parse(intent?.getStringExtra(Config.PASS_URI))
            vm.onSelectFromGalleryResult(uri)

        }

    }

    override fun setSelectedLanguage(languages: Languages) {
        vm.selectedLanguage.set(languages.code!!)
        vm.session.saveLong(SessionMaintainence.TRANSLATION_TIME_NOW,languages.updatedDate!!.toLong())
    }

    override fun refresh() {
        startActivity(Intent(requireContext(), DrawerActivity::class.java))
        requireActivity().finish()
    }

    override fun changeProfileDetails(fName:String,lName:String,driverProfile:String) {
        (mActivity as DrawerActivity).updateProfileInfo(fName,lName,driverProfile)
    }


    var imageDialog: Dialog? = null
    override fun showProfileImg(url: String) {
        if (imageDialog?.isShowing == true)
            imageDialog?.dismiss()
        imageDialog = Dialog(requireContext())
        imageDialog?.setContentView(R.layout.dialog_show_profile)
        val profileTitle = imageDialog?.findViewById<TextView>(R.id.profile_txt)
        val camera = imageDialog?.findViewById<TextView>(R.id.camera)
        val gallery = imageDialog?.findViewById<TextView>(R.id.gallery)
        val profile = imageDialog?.findViewById<ImageView>(R.id.profile_image)
        camera?.text = vm.translationModel?.text_camera
        gallery?.text = vm.translationModel?.text_galary
        profileTitle?.text = vm.firstName.get()
        if (isAdded) {
            context?.let {
                if (profile != null) {
                    Glide.with(it).load(url).apply(
                        RequestOptions.errorOf(R.drawable.ic_user)
                            .placeholder(R.drawable.ic_user)
                    ).into(profile)
                }
            }
        }
        camera?.setOnClickListener {
            imageDialog?.dismiss()
            cameraIntent()
        }

        gallery?.setOnClickListener {
            imageDialog?.dismiss()
            galleryIntent()
        }
        imageDialog?.show()
    }


    /** Open camera  */
    private fun cameraIntent() {
        if (!hasPermissions(requireContext(), Config.storagePermission)) {
            requestMultiplePermissions.launch(Config.storagePermission)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            intent.putExtra("com.google.assistant.extra.USE_FRONT_CAMERA", true)
//            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
//            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
//            intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
//
//            // Samsung
//            intent.putExtra("camerafacing", "front")
//            intent.putExtra("previous_mode", "front")
//
//            // Huawei
//            intent.putExtra("default_camera", "1")
//            intent.putExtra("default_mode", "com.huawei.camera2.mode.photo.PhotoMode")
            try {
                resultLauncherCamera.launch(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("TAG", "cameraIntent : "+e.message )
            }

        }
    }

    /** Open Gallery */
    private fun galleryIntent() {
        if (!hasPermissions(requireContext(), Config.storagePermission)) {
            requestMultiplePermissions.launch(Config.storagePermission)
        } else {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            try {
                resultLauncherGallery.launch(pickIntent)
            } catch (e: ActivityNotFoundException) {
                Log.e("TAG", "galleryIntent : "+e.message )
            }

        }
    }

    fun closeFragment(){
        if (vm.session.getString(SessionMaintainence.ReqID)?.isEmpty() == true)
            findNavController().popBackStack()
        else
            (requireActivity() as DrawerActivity).reqProgress()
    }

}