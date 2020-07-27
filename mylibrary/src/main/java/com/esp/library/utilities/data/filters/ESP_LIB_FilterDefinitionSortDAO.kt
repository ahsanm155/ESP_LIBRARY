package utilities.data.filters

class ESP_LIB_FilterDefinitionSortDAO() {

    var name: String? = null
    var isCheck: Boolean = false
    var id:Int=0


    constructor(name: String?, isCheck: Boolean, id: Int) : this() {
        this.name = name
        this.isCheck = isCheck
        this.id = id
    }
}
