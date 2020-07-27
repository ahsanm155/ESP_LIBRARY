package utilities.data.applicants

import java.io.Serializable

import utilities.data.ESP_LIB_Base

class ESP_LIB_ResponseApplicationsDAO : ESP_LIB_Base(), Serializable {

    var totalRecords: Int = 0
    var applications: List<ESP_LIB_ApplicationsDAO>? = null

    companion object {

        var BUNDLE_KEY = "ResponseApplicationsDAO"
    }
}
