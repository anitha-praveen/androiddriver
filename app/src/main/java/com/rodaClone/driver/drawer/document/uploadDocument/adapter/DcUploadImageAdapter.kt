package com.rodaClone.driver.drawer.document.uploadDocument.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseViewHolder
import com.rodaClone.driver.connection.responseModels.GetDocument
import com.rodaClone.driver.databinding.ChildUploadImageBinding
import com.rodaClone.driver.drawer.document.uploadDocument.UploadDocumentNavigator
import com.rodaClone.driver.ut.SessionMaintainence
import kotlinx.coroutines.*
import java.net.URL
import java.util.*

class DcUploadImageAdapter(
    private var list: ArrayList<GetDocument>,
    private val navigator: UploadDocumentNavigator,
    private val session: SessionMaintainence
) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mBinding =
            ChildUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount() = list.size

    fun addImage(document: GetDocument, position: Int) {
        list[position] = document
        notifyDataSetChanged()
    }

    fun addNullImageList(docList: ArrayList<GetDocument>) {
        list = docList
        notifyDataSetChanged()
    }

    private inner class ChildViewHolder(private val mBinding: ChildUploadImageBinding) :
        BaseViewHolder(mBinding.root), DcuploadImageItemVm.DcImageSelected {
        private lateinit var vm: DcuploadImageItemVm
        override fun onBind(position: Int) {
            val document = list[position]
            vm = DcuploadImageItemVm(document, this, position)
            mBinding.viewModel = vm
            mBinding.executePendingBindings()
            if (document.isFromapi) {
               // document.documentImage?.let { getImageFroms3(it, mBinding) }
                document.documentImage?.let { loadImage(it, mBinding)}
            } else {
            if (document.uri == null) {
                mBinding.imgPicDocupload.visibility = View.VISIBLE
                mBinding.imgProgressBar.visibility = View.GONE
                mBinding.imgUploadIcon.visibility = View.VISIBLE
                mBinding.imgPicDocupload.setImageResource(0)
            } else {
                document.uri?.let { loadImage(it, mBinding) }
            }
        }

        }

        override fun isNetworkConnected(): Boolean {
            TODO("Not yet implemented")
        }

        override fun showNetworkUnAvailable() {
            TODO("Not yet implemented")
        }

        override fun onItemSelected(document: GetDocument, position: Int) {
            navigator.openGalleryorCamera(document, position)
        }

    }

    fun getImageFroms3(urlString: String, binding: ChildUploadImageBinding) {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        uiScope.launch {
            withContext(Dispatchers.IO) {
                val cal = Calendar.getInstance()
                cal.time = Date()
                cal.add(Calendar.HOUR, +1)
                val oneHourLater: Date = cal.time
                val s3: AmazonS3 = AmazonS3Client(
                    BasicAWSCredentials(
                        session.getString(SessionMaintainence.AWS_ACCESS_KEY_ID),
                        session.getString(SessionMaintainence.AWS_SECRET_ACCESS_KEY)
                    )
                )
                s3.setRegion(Region.getRegion(session.getString(SessionMaintainence.AWS_DEFAULT_REGION)))
                val url: URL = s3.generatePresignedUrl(
                    session.getString(SessionMaintainence.AWS_BUCKET),
                    urlString,
                    oneHourLater
                )
                //setImage(url.toString())
                //vm.refURL.set(url.toString())
                withContext(Dispatchers.Main) {
                    loadImage(url.toString(), binding)
                }
            }
        }

    }

    fun loadImage(url: String, binding: ChildUploadImageBinding) {
        val imageView = binding.imgPicDocupload
        val uploadImage = binding.imgUploadIcon
        val progressBar = binding.imgProgressBar
        Glide.with(imageView.context).load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    uploadImage.visibility = View.VISIBLE
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    resource?.let { imageView.setImageDrawable(it) }
                    progressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    uploadImage.visibility = View.GONE
                    return true
                }

            })
            .apply(
                RequestOptions().error(R.drawable.ic_upload)
            ).into(imageView)
    }
}