package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base

class ESP_LIB_DynamicFormDAO : ESP_LIB_Base() {
    var id: Int = 0
    var sections: List<ESP_LIB_DynamicFormSectionDAO>? = null

    companion object {

        var BUNDLE_KEY = "DynamicFormDAO"
    }
}
