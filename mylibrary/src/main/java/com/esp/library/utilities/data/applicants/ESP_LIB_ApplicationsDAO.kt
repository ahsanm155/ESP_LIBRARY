package utilities.data.applicants

import com.esp.library.utilities.data.applicants.ESP_LIB_CardValuesDAO
import com.esp.library.utilities.data.applicants.ESP_LIB_SummaryDAO
import utilities.data.ESP_LIB_Base
import utilities.data.applicants.addapplication.ESP_LIB_SubDefintionParentDAO
import java.io.Serializable


class ESP_LIB_ApplicationsDAO : ESP_LIB_Base(), Serializable {


    var id: Int = 0
    var applicantName: String? = null

    var category: String? = null
    var definitionName: String? = null
    var applicationNumber: String? = null
    var definitionType: String? = null
    var status: String? = null
    var statusId: Int = 0
    var categoryId: Int = 0
    var numberOfSubmissions: Int = 0
    var submittedOn: String? = null
    var createdOn: String? = null
    var startedOn: String? = null
    var assessedOn: String? = null
    var isOverDue: Boolean = false
    var isSigned: Boolean = false
    var type: String? = null
    var ola: String? = null
    var dueDate: String? = null
    var definitionVersion: Int = 0
    var stageStatuses: List<String>? = null
    var summary: ESP_LIB_SummaryDAO? = null
    var parentApplicationId: Int=0
    var parentDefinitionName: String?=null

    companion object {
        var BUNDLE_KEY = "ApplicationsDAO"
    }
}
