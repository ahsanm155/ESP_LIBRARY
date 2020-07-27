package utilities.data.lookup

import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormValuesDAO

class ESP_LIB_LookupItemDetailDAO {

    var id: Int = 0
    var isVisible: Boolean = false
    lateinit var importKey: String
    lateinit var lookup: ESP_LIB_LookupInfoListDetailDAO.LookupTemplate
    lateinit var values: List<ESP_LIB_DynamicFormValuesDAO>
}
