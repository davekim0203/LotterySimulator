package org.dave.lotterysimulatorwithstat.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.databinding.FragmentStatBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatDataViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatViewModel

@AndroidEntryPoint
class StatFragment : Fragment() {

    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()
    private val statViewModel: StatViewModel by activityViewModels()
    private val statDataViewModel: StatDataViewModel by activityViewModels()
    private lateinit var binding: FragmentStatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stat, container, false)
        binding.apply {
            statVM = statViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.stat_setting_menu, menu)
        menu.findItem(R.id.include_no_payout).isChecked = loadIsNoPayoutIncluded()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_chart_data -> {
                showClearConfirmDialog()
                true
            }

            R.id.clear_all_chart_data -> {
                showClearAllConfirmDialog()
                true
            }

            R.id.include_no_payout -> {
                item.isChecked = !item.isChecked
                statViewModel.setIsNoPayoutIncluded(item.isChecked)
                saveIsNoPayoutIncluded(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeViewModel() {
        statViewModel.selectedStatType.observe(viewLifecycleOwner, {
            when(it) {
                STAT_PIE_CHART_ID -> {
                    replaceChildFragment(PieChartFragment())
                    if(binding.toggleStatType.checkedButtonId != binding.togglePie.id) {
                        binding.toggleStatType.check(binding.togglePie.id)
                    }
                }
                STAT_BAR_CHART_ID -> {
                    replaceChildFragment(BarChartFragment())
                    if(binding.toggleStatType.checkedButtonId != binding.toggleBar.id) {
                        binding.toggleStatType.check(binding.toggleBar.id)
                    }
                }
            }
        })
    }

    private fun replaceChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(
            R.id.stat_container,
            fragment
        ).commit()
    }

    private fun showClearConfirmDialog() {
        val type = lotteryTypeViewModel.selected.value
        if(type != null) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.menu_title_clear_chart_data))
                .setMessage(
                    String.format(
                        getString(R.string.clear_graph_data_confirm_dialog_message),
                        getString(type.nameId)
                    )
                )
                .setPositiveButton(getString(R.string.clear_data_confirm_dialog_positive_button)) { dialog, _ ->
                    statDataViewModel.resetStatByType(type)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(getString(R.string.lottery_type_error_dialog_message))
                .setPositiveButton(getString(R.string.dialog_ok_button)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun showClearAllConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.menu_title_clear_all_chart_data))
            .setMessage(getString(R.string.clear_all_graph_data_confirm_dialog_message))
            .setPositiveButton(getString(R.string.clear_data_confirm_dialog_positive_button)) { dialog, _ ->
                statDataViewModel.resetAllStat()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun saveIsNoPayoutIncluded(isNoPayoutIncluded: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences(PREF_NO_PAYOUT, Context.MODE_PRIVATE)
        sharedPreferences
            .edit()
            .putBoolean(PREF_NO_PAYOUT, isNoPayoutIncluded)
            .apply()
    }

    private fun loadIsNoPayoutIncluded(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences(PREF_NO_PAYOUT, Context.MODE_PRIVATE)
        val savedValue = sharedPreferences.getBoolean(PREF_NO_PAYOUT, DEFAULT_IS_NO_PAYOUT_INCLUDED)
        statViewModel.setIsNoPayoutIncluded(savedValue)

        return savedValue
    }

    companion object {
        const val STAT_PIE_CHART_ID : Int = 0
        const val STAT_BAR_CHART_ID : Int = 1
        private const val PREF_NO_PAYOUT : String = "pref_no_payout_included"
        private const val DEFAULT_IS_NO_PAYOUT_INCLUDED : Boolean = true
    }
}
