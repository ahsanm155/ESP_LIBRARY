package utilities.data.applicants.addapplication

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_ResponseFileUploadDAO : ESP_LIB_Base(), Serializable {


    var fileId: String? = null
    var downloadUrl: String? = null

    companion object {
        var BUNDLE_KEY = "ResponseFileUploadDAO"
    }
}
