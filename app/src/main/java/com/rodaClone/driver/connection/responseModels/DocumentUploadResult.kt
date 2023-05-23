package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DocumentUploadResult {

    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("user_id")
    @Expose
    val userId: Int? = null

    @SerializedName("document_id")
    @Expose
    val documentId: Int? = null

    @SerializedName("document_image")
    @Expose
    val documentImage: String? = null

    @SerializedName("expiry_date")
    @Expose
    val expiryDate: String? = null

    @SerializedName("document_status")
    @Expose
    val documentStatus: Int? = null

    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    val deletedAt: Any? = null

    @SerializedName("issue_date")
    @Expose
    val issueDate: String? = null

    @SerializedName("exprienc_status")
    @Expose
    val expriencStatus: Int? = null

    @SerializedName("exprience_reson")
    @Expose
    val exprienceReson: String? = null

    @SerializedName("identifier")
    @Expose
    val identifier: Any? = null

    @SerializedName("document_number")
    @Expose
    val documentNumber: Any? = null

    @SerializedName("document")
    @Expose
    val document: Document? = null
}