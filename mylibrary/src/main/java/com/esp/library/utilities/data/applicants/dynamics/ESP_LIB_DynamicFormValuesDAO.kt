package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base
import utilities.data.lookup.ESP_LIB_LookupValuesDAO
import java.io.Serializable

class ESP_LIB_DynamicFormValuesDAO : ESP_LIB_Base(), Serializable {

    var id: Int = 0
    var customFieldLookupId: Int = 0
    var itemid: Int = 0
    var createdOn: String? = null
    var sectionCustomFieldId: Int = 0
    var value: String? = null
    var sectionId: Int = 0
    var details: ESP_LIB_DynamicFormValuesDetailsDAO? = null
    var selectedLookupText: String? = null
    var label: String? = null
    var type: Int = 0
    var lookupValuesDAO: ESP_LIB_LookupValuesDAO? = null
    var filterLookup: String? = null

    companion object {

        var BUNDLE_KEY = "DynamicFormValuesDAO"
    }
}
