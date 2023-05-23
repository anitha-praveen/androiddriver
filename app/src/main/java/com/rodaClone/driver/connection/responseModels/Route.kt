package com.rodaClone.driver.connection.responseModels

import java.io.Serializable
import java.util.ArrayList

class Route : Serializable{
     private var startAddress: String? = null
     private var endAddress: String? = null
     private var startLat = 0.0
     private var startLon = 0.0
     private var endLat = 0.0
     private var endLon = 0.0
     private var distanceText: String? = null
     private var distanceValue = 0
     private var durationText: String? = null
     private var durationValue = 0
     private var listStep = ArrayList<Step>()
     var polyPoints : String?= null

     fun getListStep(): ArrayList<Step>? {
          return listStep
     }

     fun setListStep(listStep: ArrayList<Step>) {
          this.listStep = listStep
     }

     fun Route() {
          listStep = ArrayList()
     }

     fun getStartAddress(): String? {
          return startAddress
     }

     fun setStartAddress(startAddress: String?) {
          this.startAddress = startAddress
     }

     fun getEndAddress(): String? {
          return endAddress
     }

     fun setEndAddress(endAddress: String?) {
          this.endAddress = endAddress
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

     fun getEndLon(): Double {
          return endLon
     }

     fun setEndLon(endLon: Double) {
          this.endLon = endLon
     }

     fun getDistanceText(): String? {
          return distanceText
     }

     fun setDistanceText(distanceText: String?) {
          this.distanceText = distanceText
     }

     fun getDistanceValue(): Int {
          return distanceValue
     }

     fun setDistanceValue(distanceValue: Int) {
          this.distanceValue = distanceValue
     }

     fun getDurationText(): String? {
          return durationText
     }

     fun setDurationText(durationText: String?) {
          this.durationText = durationText
     }

     fun getDurationValue(): Int {
          return durationValue
     }

     fun setDurationValue(durationValue: Int) {
          this.durationValue = durationValue
     }
}