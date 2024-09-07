package com.kodekolektif._core.utils

object Constant {
    const val baseUrl: String = "https://8091-103-100-175-211.ngrok-free.app/danaku/public"
//    const val baseUrl: String = "http://34.1.199.112/danaku/public"
    const val apiUrl: String = "$baseUrl/api/v1/"

    const val sharedPref: String = "app_prefs"


    // VALUE
    const val requestTimeout = 10L


    // KEY
    const val nameRegex = "name_regex_key"
    const val priceRegex = "price_regex_key"

    const val xiaomi_alert_do_not_show_again = "do_not_show_again"
}

