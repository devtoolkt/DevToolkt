package dev.toolkt.js

@JsName("Object")
external object JsObjects {
    fun getPrototypeOf(o: dynamic): dynamic

    fun keys(o: dynamic): Array<String>

    fun values(o: dynamic): Array<dynamic>

    fun entries(o: dynamic): Array<Array<dynamic>>
}
