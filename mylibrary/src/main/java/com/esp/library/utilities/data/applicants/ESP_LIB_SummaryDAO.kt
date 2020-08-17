package com.esp.library.utilities.data.applicants

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_SummaryDAO: ESP_LIB_Base(), Serializable {

    var cardValues: List<ESP_LIB_CardValuesDAO>? = null
    var isMine: Boolean = false
    var name: String? = null
    var title: String? = null
}