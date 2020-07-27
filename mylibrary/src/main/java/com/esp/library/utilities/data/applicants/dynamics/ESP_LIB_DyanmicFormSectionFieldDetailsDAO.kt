package utilities.data.applicants.dynamics

import android.net.Uri
import utilities.data.ESP_LIB_Base

class ESP_LIB_DyanmicFormSectionFieldDetailsDAO : ESP_LIB_Base() {
    var name: String? = null
    var mimeType: String? = null
    var createdOn: String? = null
    var downloadUrl: String? = null
    var fileSize: String? = null
    var path: String? = null
    var uri: Uri? = null

    var isFileDownloaded: Boolean = false
    var isFileDownling: Boolean = false

    companion object {

        var BUNDLE_KEY = "DyanmicFormSectionFieldDetailsDAO"
    }
}
