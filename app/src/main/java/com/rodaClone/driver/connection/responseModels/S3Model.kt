package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class S3Model {
    @SerializedName("s3_bucket_name")
    @Expose
    val s3BucketName: String? = null

    @SerializedName("s3_bucket_key_id")
    @Expose
    val s3BucketKeyId: String? = null

    @SerializedName("s3_bucket_secret_access_key")
    @Expose
    val s3BucketSecretAccessKey: String? = null

    @SerializedName("s3_bucket_default_region")
    @Expose
    val s3BucketDefaultRegion: String? = null

    @SerializedName("places_api_key")
    @Expose
    val places_api_key: String? = null

    @SerializedName("distance_api_key")
    @Expose
    val distance_api_key: String? = null

    @SerializedName("geo_coder_api_key")
    @Expose
    val geo_coder_api_key: String? = null

    @SerializedName("directional_api_key")
    @Expose
    val directional_api_key: String? = null
}