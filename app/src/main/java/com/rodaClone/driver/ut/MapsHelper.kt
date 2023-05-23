package com.rodaClone.driver.ut

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsHelper {
    @GET("maps/api/geocode/json?")
    fun GetAddressFromLatLng(
        @Query("latlng") name: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?


    /*   @Query("components") String components,@Query("radius") String radius*/

    /*   @Query("components") String components,@Query("radius") String radius*/
    @GET("maps/api/place/autocomplete/json?radius=500&components=country:IN")
    fun GetPlaceApi(
        @Query("language") language: String?,
        @Query("location") locat: String?,
        @Query("input") input: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?


//    @GET("maps/api/place/autocomplete/json?radius=500")
//    fun GetPlaceApi(
//        @Query("location") locat: String?,
//        @Query("input") input: String?,
//        @Query("sensor") a: Boolean,
//        @Query("key") key: String?
//    ): Call<JsonObject?>?

    /**
     * 50Km radius search
     */
    @GET("maps/api/place/autocomplete/json?radius=50000&components=country:IN")
    fun GetPlaceApi(
        @Query("location") locat: String?,
        @Query("input") input: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?


    @GET(Config.DirectionURL)
    fun GetDrawpath(
        @Query("origin") origin: String?,
        @Query("destination") destionation: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?

    /*to draw multiple path*/
    @GET(Config.DirectionURL)
    fun getDrawPathMultipleStops(
        @Query("origin") origin: String?,
        @Query("destination") destionation: String?,
        @Query("waypoints") wayPoints: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?


    @GET("maps/api/geocode/json?")
    fun GetLatLngFromAddress(
        @Query("address") name: String?,
        @Query("sensor") a: Boolean,
        @Query("key") key: String?
    ): Call<JsonObject?>?


}