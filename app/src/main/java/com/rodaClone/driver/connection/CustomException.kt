package com.rodaClone.driver.connection


class CustomException constructor(code: Int, exception: String?): Exception() {
    var code = code
    var exception = exception

}