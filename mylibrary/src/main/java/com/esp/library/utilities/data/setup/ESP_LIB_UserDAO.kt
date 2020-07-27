package utilities.data.setup

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_UserDAO : ESP_LIB_Base(), Serializable {

    var loginResponse: ESP_LIB_TokenDAO? = null
    var profileStatus: String? = null
    var role: String? = null


    companion object {
        var BUNDLE_KEY = "UserDAO"
    }
}
