package com.example.marketapp.Adapters

// ViewPagerAdapter.kt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.marketapp.Fragments.AllRecipesFragment
import com.example.marketapp.Fragments.HomeFragment
import com.example.marketapp.Fragments.MyRecipesFragment


class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MyRecipesFragment()
            1 -> HomeFragment()
            2 -> AllRecipesFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "My Recipes"
            1 -> "Home"
            2 -> "All Recipes"
            else -> null
        }
    }
}
