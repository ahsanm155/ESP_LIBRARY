package utilities.data.applicants.dynamics

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_DynamicCriteriaCustomFieldsDetailDAO : ESP_LIB_Base(), Serializable {


    var name: String? = null
    var mimeType: String? = null
    var createdOn: String? = null
    var downloadUrl: String? = null

    companion object {
        var BUNDLE_KEY = "DynamicCriteriaCustomFieldsDetailDAO"
    }
}
