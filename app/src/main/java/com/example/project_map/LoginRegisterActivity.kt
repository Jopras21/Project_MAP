package com.example.project_map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // Buat dan pasang adapter
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Hubungkan TabLayout dengan ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Login" else "Register"
        }.attach()

        // Ambil data tab yang dikirim dari AuthActivity
        val selectedTabIndex = intent.getIntExtra(AuthActivity.EXTRA_SELECTED_TAB, 0)

        // Pindahkan ViewPager2 ke tab yang sesuai
        viewPager.setCurrentItem(selectedTabIndex, false)
    }
}