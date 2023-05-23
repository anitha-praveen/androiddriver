package com.rodaClone.driver.connection

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class LanguageModel {
    @SerializedName("success")
    @Expose
     val success: Boolean? = null

    @SerializedName("data")
    @Expose
     val data: Data? = null

    @SerializedName("message")
    @Expose
     val message: String? = null

    class Data{
        @Expose
        var translationModel: TranslationModel? = null
    }

}