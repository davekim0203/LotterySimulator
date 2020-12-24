package org.dave.lotterysimulatorwithstat.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.dave.lotterysimulatorwithstat.fragment.RandomNumberFragment
import org.dave.lotterysimulatorwithstat.fragment.SimulationFragment
import org.dave.lotterysimulatorwithstat.fragment.StatFragment


class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        RandomNumberFragment(),
        SimulationFragment(),
        StatFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}