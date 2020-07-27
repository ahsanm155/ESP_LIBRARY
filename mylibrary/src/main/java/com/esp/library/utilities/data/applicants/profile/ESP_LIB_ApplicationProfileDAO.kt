package utilities.data.applicants.profile

import utilities.data.ESP_LIB_Base
import utilities.data.applicants.dynamics.ESP_LIB_DyanmicFormSectionFieldDetailsDAO
import utilities.data.applicants.dynamics.ESP_LIB_DynamicFormSectionDAO
import java.io.Serializable

class ESP_LIB_ApplicationProfileDAO : ESP_LIB_Base(), Serializable {


    lateinit var applicant: Applicant
    lateinit var sections: List<ESP_LIB_DynamicFormSectionDAO>

    inner class Applicant : ESP_LIB_Base(), Serializable {

        var createdOn: String? = null
        var emailAddress: String? = null
        var imageUrl: String? = null
        var invitedOn: String? = null
        var name: String? = null
        var profileTemplateString: String? = null
        var status: String? = null
        var id: Int = 0
        var applicantStatus: Int = 0
        var version: Int = 0
        var idenediKey: String? = null
        var isProfileSubmitted: Boolean = false
        var applicantSections: List<ApplicationSection>? = null
    }

    inner class ApplicationSection : ESP_LIB_Base(), Serializable {

        var sectionId: Int = 0
        var index: Int = 0
        var lastUpdatedOn: String? = null
        var values: List<Values>? = null

    }

    class Values : ESP_LIB_Base(), Serializable {
        var value: String? = null
        var type:Int=0
        var sectionFieldId: Int = 0
        var lookupValue: String? = null
        var details: ESP_LIB_DyanmicFormSectionFieldDetailsDAO? = null
    }

}
