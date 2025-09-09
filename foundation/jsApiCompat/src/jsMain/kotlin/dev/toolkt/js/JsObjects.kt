package dev.toolkt.js

import kotlin.js.collections.JsArray

@JsName("Object")
external object JsObjects {
    fun getPrototypeOf(o: dynamic): dynamic

    fun keys(o: dynamic): JsArray<String>

    fun values(o: dynamic): JsArray<dynamic>

    fun entries(o: dynamic): JsArray<JsArray<dynamic>>
}
