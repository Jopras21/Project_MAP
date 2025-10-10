package com.example.project_map // Sesuaikan dengan package Anda

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Ada 2 tab: Login dan Register
    override fun getItemCount(): Int = 2

    // Tentukan Fragment untuk setiap posisi tab
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment() // Posisi 0 adalah LoginFragment
            1 -> RegisterFragment() // Posisi 1 adalah RegisterFragment
            else -> throw IllegalStateException("Invalid position")
        }
    }
}