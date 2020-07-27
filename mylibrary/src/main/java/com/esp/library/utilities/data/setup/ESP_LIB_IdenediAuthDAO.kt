package utilities.data.setup

import utilities.data.ESP_LIB_Base

class ESP_LIB_IdenediAuthDAO : ESP_LIB_Base() {

    var AccessToken: String? = null
    var RefreshToken: String? = null
    var EmailAddress: String? = null
    var IdenediId: String? = null


    companion object {

        var BUNDLE_KEY = "IdenediAuthDAO"
    }
}
