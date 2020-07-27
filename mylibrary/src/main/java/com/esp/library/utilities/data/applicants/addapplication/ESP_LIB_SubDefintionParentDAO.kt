package utilities.data.applicants.addapplication

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_SubDefintionParentDAO : ESP_LIB_Base(), Serializable {

    var mainApplicationNumber: String? = null
    var submittedBy: String? = null
    var applicantEmail: String? = null
    var submittedOn: String? = null
    var titleFieldValue: String? = null
    var descriptionFieldValue: String? = null
    var applicationId: Int = 0
}
