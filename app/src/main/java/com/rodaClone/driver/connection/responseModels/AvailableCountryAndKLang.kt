package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AvailableCountryAndKLang {
    @SerializedName("languages")
    @Expose
    val languages: List<Languages>? = null

    @SerializedName("country")
    @Expose
    val country: List<Country>? = null

}