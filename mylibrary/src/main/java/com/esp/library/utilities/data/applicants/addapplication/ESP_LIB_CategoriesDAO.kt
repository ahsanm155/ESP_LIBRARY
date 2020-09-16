package utilities.data.applicants.addapplication

import utilities.data.ESP_LIB_Base
import java.io.Serializable


class ESP_LIB_CategoriesDAO : ESP_LIB_Base(), Serializable {


    var id: Int = 0
    var name: String? = null
    var color: String? = null
    var definitions: List<ESP_LIB_DefinationsDAO>? = null

    companion object {
        var BUNDLE_KEY = "DefinationsCategoriesDAO"
    }
}
