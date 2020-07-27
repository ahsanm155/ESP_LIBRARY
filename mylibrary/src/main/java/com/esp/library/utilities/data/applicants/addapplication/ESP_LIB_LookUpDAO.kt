package utilities.data.applicants.addapplication

import utilities.data.ESP_LIB_Base
import java.io.Serializable




class ESP_LIB_LookUpDAO : ESP_LIB_Base(), Serializable {

    var applicantName: String? = null
    var id: Int = 0
    var name: String? = null

    companion object {
        var BUNDLE_KEY = "LookUpDAO"
    }

}
