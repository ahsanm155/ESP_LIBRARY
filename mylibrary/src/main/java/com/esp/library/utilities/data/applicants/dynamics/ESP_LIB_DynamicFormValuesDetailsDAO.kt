package utilities.data.applicants.dynamics

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_DynamicFormValuesDetailsDAO : ESP_LIB_Base(), Serializable {
    var name: String? = null
    var mimeType: String? = null
    var createdOn: String? = null
    var downloadUrl: String? = null

    companion object {

        var BUNDLE_KEY = "DynamicFormValuesDetailsDAO"
    }
}
