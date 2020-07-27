package utilities.data.applicants.feedback

import java.io.Serializable

import utilities.data.ESP_LIB_Base


class ESP_LIB_ApplicationsFeedbackAttachmentsDAO : ESP_LIB_Base(), Serializable {


    var name: String? = null
    var mimeType: String? = null
    var createdOn: String? = null
    var downloadUrl: String? = null
    var isFileDownloaded: Boolean = false
    var isFileDownling: Boolean = false

    companion object {
        var BUNDLE_KEY = "ApplicationsFeedbackAttachmentsDAO"
    }
}

