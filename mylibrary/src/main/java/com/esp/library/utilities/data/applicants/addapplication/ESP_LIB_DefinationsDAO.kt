package utilities.data.applicants.addapplication

import utilities.data.ESP_LIB_Base
import java.io.Serializable


class ESP_LIB_DefinationsDAO : ESP_LIB_Base(), Serializable {


    var id: Int = 0
    var name: String? = null
    var isActive: Boolean = false
    var typeId: Int = 0
    var categoryId: Int = 0
    var parentApplicationId: Int = 0
    var description: String? = null
    var category: String? = null
    var isPublished: Boolean = false
    var isChecked: Boolean = false
    var createdOn: String? = null
    var iconName: String? = null
    var parentApplicationInfo: ESP_LIB_SubDefintionParentDAO? = null

    companion object {
        var BUNDLE_KEY = "CategoriesDAO"
    }
}
