package utilities.model

import utilities.data.ESP_LIB_Base
import java.io.Serializable

class ESP_LIB_Labels : ESP_LIB_Base(), Serializable {


     var applications: String?=null
     var applicants: String?=null
     var definitions: String?=null
     var organization: String?=null
     var settings: String?=null
     var application: String?=null
     var applicant: String?=null
     var definition: String?=null
     var submissionRequests: String?=null
     var application_short_name: String?=null
     var application_long_name: String?=null
}
