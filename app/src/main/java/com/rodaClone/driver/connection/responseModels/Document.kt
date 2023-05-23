package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Document {


    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null

    @SerializedName("document_name")
    @Expose
    val documentName: String? = null

    @SerializedName("requried")
    @Expose
    val requried: Int? = null

    @SerializedName("expiry_date")
    @Expose
    val expiryDate: String? = null

    @SerializedName("date_required")
    @Expose
    val dateRequired: Int? = null

    @SerializedName("issue_date")
    @Expose
    val issueDate: String? = null

    @SerializedName("is_uploaded")
    @Expose
    val isUploaded: Int? = null

    @SerializedName("document_image")
    @Expose
    val documentImage: String? = null

    @SerializedName("identifier")
    val identifier : Int? = null

    @SerializedName("identifier_value")
    val identifierValue : String? = null

    @SerializedName("document_expiry")
    val document_expiry:Int? = null


}