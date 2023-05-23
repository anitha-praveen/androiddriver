package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class Country : Serializable{
    @SerializedName("id")
    @Expose
    val id: String? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("dial_code")
    @Expose
    val dialCode: String? = null

    @SerializedName("code")
    @Expose
    val code: String? = null

    @SerializedName("currency_name")
    @Expose
    val currencyName: Any? = null

    @SerializedName("currency_code")
    @Expose
    val currencyCode: String? = null

    @SerializedName("currency_symbol")
    @Expose
    val currencySymbol: String? = null

    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("capital")
    @Expose
    val capital: String? = null

    @SerializedName("citizenship")
    @Expose
    val citizenship: String? = null

    @SerializedName("country_code")
    @Expose
    val countryCode: String? = null

    @SerializedName("currency")
    @Expose
    val currency: String? = null

    @SerializedName("currency_sub_unit")
    @Expose
    val currencySubUnit: String? = null

    @SerializedName("full_name")
    @Expose
    val fullName: String? = null

    @SerializedName("iso_3166_3")
    @Expose
    val iso31663: String? = null

    @SerializedName("region_code")
    @Expose
    val regionCode: String? = null

    @SerializedName("sub_region_code")
    @Expose
    val subRegionCode: String? = null

    @SerializedName("eea")
    @Expose
    val eea: String? = null

    @SerializedName("currency_decimals")
    @Expose
    val currencyDecimals: String? = null

    @SerializedName("flag")
    @Expose
    val flag: String? = null

    @SerializedName("flag_base_64")
    @Expose
    val flagBase64: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null
}