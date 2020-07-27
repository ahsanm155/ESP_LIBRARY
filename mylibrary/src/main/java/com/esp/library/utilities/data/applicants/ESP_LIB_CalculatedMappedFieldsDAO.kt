package utilities.data.applicants

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.addapplication.ESP_LIB_LookUpDAO
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import java.io.Serializable

class ESP_LIB_CalculatedMappedFieldsDAO : ESP_LIB_Base(),Serializable {

    var sectionId: Int = 0
    var sectionIndex: Int = 0
    var sectionCustomFieldId: Int = 0
    var targetFieldType: Int = 0
    var value: String = ""
    var details: ESP_LIB_DyanmicFormSectionFieldDetailsDAO? = null
    var lookupItems: List<ESP_LIB_LookUpDAO>? = null
}
