package utilities.data.applicants

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.addapplication.ESP_LIB_SubDefintionParentDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicSectionValuesDAO
import java.io.Serializable


class ESP_LIB_SubmittalApplicationsDAO : ESP_LIB_Base(), Serializable {


    var applicationCard:ESP_LIB_ApplicationsDAO?=null
    var sectionValues: List<ESP_LIB_DynamicSectionValuesDAO>? = null
    var form: ESP_LIB_DynamicFormDAO? = null
    var mySubmissions: List<ESP_LIB_ApplicationsDAO>? = null


    companion object {
        var BUNDLE_KEY = "ESP_LIB_SubmittalApplicationsDAO"
    }

}

