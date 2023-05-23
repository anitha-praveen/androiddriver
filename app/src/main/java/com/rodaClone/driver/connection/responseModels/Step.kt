package com.rodaClone.driver.connection.responseModels

import com.google.android.gms.maps.model.LatLng
import java.util.ArrayList

class Step {
     private var startLat = 0.0
     private var startLon = 0.0
     private var endLat = 0.0
     private var endLong = 0.0
     private var html_instructions: String? = null
     private var strPoint: String? = null
     private var listPoints: List<LatLng>? = null

     fun Step() {
          listPoints = ArrayList()
     }

     fun getListPoints(): List<LatLng>? {
          return listPoints
     }

     fun setListPoints(listPoints: List<LatLng>?) {
          this.listPoints = listPoints
     }

     fun getStartLat(): Double {
          return startLat
     }

     fun setStartLat(startLat: Double) {
          this.startLat = startLat
     }

     fun getStartLon(): Double {
          return startLon
     }

     fun setStartLon(startLon: Double) {
          this.startLon = startLon
     }

     fun getEndLat(): Double {
          return endLat
     }

     fun setEndLat(endLat: Double) {
          this.endLat = endLat
     }

     fun getEndLong(): Double {
          return endLong
     }

     fun setEndLong(endLong: Double) {
          this.endLong = endLong
     }

     fun getHtml_instructions(): String? {
          return html_instructions
     }

     fun setHtml_instructions(html_instructions: String?) {
          this.html_instructions = html_instructions
     }

     fun getStrPoint(): String? {
          return strPoint
     }

     fun setStrPoint(strPoint: String?) {
          this.strPoint = strPoint
     }

}