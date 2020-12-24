package org.dave.lotterysimulatorwithstat.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.ResultCount
import org.dave.lotterysimulatorwithstat.databinding.FragmentBarChartBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatDataViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatViewModel

@AndroidEntryPoint
class BarChartFragment : Fragment() {

    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()
    private val statViewModel: StatViewModel by activityViewModels()
    private val statDataViewModel: StatDataViewModel by activityViewModels()
    private lateinit var binding: FragmentBarChartBinding
    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bar_chart,
            container,
            false
        )
        binding.apply {
            statDataVM = statDataViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        chart = binding.barChart
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeViewModels()
    }

    private fun subscribeViewModels() {
        lotteryTypeViewModel.selected.observe(viewLifecycleOwner, {
            statDataViewModel.setLotteryType(it)
        })

        statViewModel.isNoPayoutIncluded.observe(viewLifecycleOwner, {
            statDataViewModel.setIsNoPayoutIncluded(it)
        })

        statDataViewModel.resultCountsByType.observe(viewLifecycleOwner, {
            setupBarChart(it)
        })
    }

    private fun setupBarChart(results: List<ResultCount>) {
        val xAxisLabel = mutableListOf<String>().apply {
            for(r in results) {
                add(r.matchCount)
            }
        }

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setPinchZoom(false)
            setScaleEnabled(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                labelCount = results.size - 1
                valueFormatter = IndexAxisValueFormatter(xAxisLabel)
                textColor = getColor(requireContext(), android.R.color.tab_indicator_text)
            }

            axisLeft.apply {
                valueFormatter = BAR_CHART_VALUE_FORMATTER
                axisMinimum = 0f
                granularity = 1.0f
                isGranularityEnabled = true
                textColor = getColor(requireContext(), android.R.color.tab_indicator_text)
            }
            axisRight.isEnabled = false
            animateY(1500)
        }

        setData(results)
    }

    private fun setData(results: List<ResultCount>) {
        val entries = mutableListOf<BarEntry>().apply {
            for(i in results.indices) {
                add(BarEntry(i.toFloat(), results[i].count.toFloat()))
            }
        }

        val barColors = ArrayList<Int>()
        for (c in ColorTemplate.COLORFUL_COLORS) barColors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) barColors.add(c)

        val set1: BarDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = entries
            chart.apply {
                data.notifyDataChanged()
                notifyDataSetChanged()
            }
        } else {
            set1 = BarDataSet(entries, getString(R.string.empty_string)).apply {
                setDrawValues(true)
                colors = barColors
                valueTextColor = Color.WHITE
                valueTextSize = 12f
                valueFormatter = BAR_CHART_VALUE_FORMATTER
            }
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val barData = BarData(dataSets)
            chart.apply {
                data = barData
                setFitBars(true)
            }
        }

        chart.invalidate()
    }

    companion object {
        private val BAR_CHART_VALUE_FORMATTER = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
    }
}
