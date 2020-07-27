package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base

class ESP_LIB_DynamicFormSectionFieldLookupValuesDAO : ESP_LIB_Base() {

    var id: Int = 0
    var label: String? = null
    var isSelected: Boolean = false
    var customFieldId: Int = 0
    var order: Int = 0

    companion object {

        var BUNDLE_KEY = "DynamicFormSectionFieldLookupValuesDAO"
    }
}
