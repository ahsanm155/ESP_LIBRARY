package utilities.data.applicants.addapplication

import utilities.data.ESP_LIB_Base
import java.io.Serializable


class ESP_LIB_DefinationsCategoriesDAO : ESP_LIB_Base(), Serializable {


    var id: Int = 0
    var name: String? = null
    var definitions: List<ESP_LIB_CategoryAndDefinationsDAO>? = null

    companion object {
        var BUNDLE_KEY = "DefinationsCategoriesDAO"
    }
}
