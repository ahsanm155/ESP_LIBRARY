package utilities.data.applicants.addapplication

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_CurrencyDAO : ESP_LIB_Base(), Serializable {


    var id: Int = 0
    var name: String? = null
    var code: String? = null
    var category: String? = null
    var symobl: String? = null
    var rateToBase: Double = 0.toDouble()
    var isBase: Boolean = false

    companion object {
        var BUNDLE_KEY = "CurrencyDAO"
    }
}
