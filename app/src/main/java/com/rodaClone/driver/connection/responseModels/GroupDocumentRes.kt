package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GroupDocument:Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("expired_status")
    @Expose
    var expiryStatus:Int?= null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: String? = null

    @SerializedName("document_count")
    @Expose
    var documentCount: Int? = null

    @SerializedName("upload_status")
    @Expose
    var uploadStatus:Int? = null

    @SerializedName("get_document")
    @Expose
    var getDocument: ArrayList<GetDocument> = arrayListOf()

}

class GetDocument:Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("document_name")
    @Expose
    var documentName: String? = null

    @SerializedName("requried")
    @Expose
    var requried: Int? = null

    @SerializedName("identifier")
    @Expose
    var identifier: Int? = null

    @SerializedName("expiry_date")
    @Expose
    var expiryDate: Int? = null

    @SerializedName("expiry_dated")
    @Expose
    var expiryDated:String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("denaited_status")
    @Expose
    var deniedStatus:Boolean? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    var deletedAt: String? = null

    @SerializedName("group_by")
    @Expose
    var groupBy: Int? = null

    @SerializedName("document_image")
    var documentImage: String? = null

    @SerializedName("issue_date")
    @Expose
    var issueDate: String? = null

    @SerializedName("is_uploaded")
    @Expose
    var isUploaded: Int? = null
    
    @SerializedName("identifier_document")
    @Expose
    var identifier_document:String? = null

    var isFromapi:Boolean = true

    var uri:String? = null

}