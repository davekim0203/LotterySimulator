package org.dave.lotterysimulatorwithstat.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.NoFilterArrayAdapter
import org.dave.lotterysimulatorwithstat.adapter.ViewPagerAdapter
import org.dave.lotterysimulatorwithstat.databinding.FragmentMainViewPagerBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel

@AndroidEntryPoint
class MainViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentMainViewPagerBinding
    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.pager
        val tabTitles = resources.getStringArray(R.array.tab_titles)
        viewPager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarOptions()
        subscribeViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.ticket_history -> {
            findNavController().navigate(MainViewPagerFragmentDirections
                .actionMainViewPagerFragmentToTicketHistoryFragment())
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    private fun setupToolbarOptions() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.mainViewPagerFragmentToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
        setHasOptionsMenu(true)
        setupTypeSpinner()
    }

    private fun setupTypeSpinner() {
        val types = resources.getStringArray(R.array.lottery_types).toList()
        val adapter = NoFilterArrayAdapter(requireContext(), R.layout.type_spinner_item, types)
        binding.lotteryTypeDropdown.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ ->
                lotteryTypeViewModel.select(i)
            }
        }
    }

    private fun subscribeViewModels() {
        lotteryTypeViewModel.selected.observe(viewLifecycleOwner, {
            binding.lotteryTypeDropdown.setText(getString(it.nameId), false)
        })
    }
}
