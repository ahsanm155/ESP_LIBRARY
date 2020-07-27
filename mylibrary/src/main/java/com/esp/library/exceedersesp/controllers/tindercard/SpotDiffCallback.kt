package com.esp.library.exceedersesp.controllers.tindercard

import androidx.recyclerview.widget.DiffUtil
import utilities.data.applicants.ESP_LIB_ApplicationsDAO

class SpotDiffCallback(
        private val old: List<ESP_LIB_ApplicationsDAO>,
        private val aNew: List<ESP_LIB_ApplicationsDAO>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return aNew.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == aNew[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == aNew[newPosition]
    }

}
