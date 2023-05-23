package com.rodaClone.driver.connection.responseModels

import android.view.View
import androidx.databinding.ObservableBoolean
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Faq {
    var clickedArrow = ObservableBoolean(true)

    @Expose
    var isExpanded = false

    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("question")
    @Expose
    val question: String? = null

    @SerializedName("answer")
    @Expose
    val answer: String? = null

    @SerializedName("category")
    @Expose
    val category: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("deleted_at")
    @Expose
    val deletedAt: Any? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null

    var faqModelList: List<Faq>? = null

    fun onClickArrow(v: View?) {
        for (faqModel in faqModelList!!) {
            faqModel.clickedArrow.set(true)
        }
        clickedArrow.set(!clickedArrow.get())
    }
}