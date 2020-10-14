package com.esp.library.utilities.setup

import android.app.FragmentManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.ArrayList

class ViewPagerAdapter(manager: androidx.fragment.app.FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList = ArrayList<androidx.fragment.app.Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
}