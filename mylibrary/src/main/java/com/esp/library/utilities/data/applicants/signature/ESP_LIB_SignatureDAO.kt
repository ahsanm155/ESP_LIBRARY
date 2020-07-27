package com.esp.library.utilities.data.applicants.signature

import java.io.Serializable

class ESP_LIB_SignatureDAO : Serializable{
    var id = 0
    var personaId = 0
    var type: String? = null
    var signatoryName: String? = null
    var fontFamily: String? = null
    var createdOn: String? = null
    var fileGuid: String? = null
    var file: fileDAO? = null

    inner class fileDAO : Serializable {
        var name: String? = null
        var mimeType: String? = null
        var createdOn: String? = null
        var downloadUrl: String? = null
        var fileId: String? = null
    }
}