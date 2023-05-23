package com.rodaClone.driver.ut

import java.io.File


class GenericsType {
     private var t: File? = null

     fun get(): File? {
          return t
     }

     fun set(t1: File?) {
          t = t1
     }
}