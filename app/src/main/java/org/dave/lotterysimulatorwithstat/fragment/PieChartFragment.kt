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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.ResultCount
import org.dave.lotterysimulatorwithstat.databinding.FragmentPieChartBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatDataViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.StatViewModel

@AndroidEntryPoint
class PieChartFragment : Fragment() {

    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()
    private val statViewModel: StatViewModel by activityViewModels()
    private val statDataViewModel: StatDataViewModel by activityViewModels()
    private lateinit var binding: FragmentPieChartBinding
    private lateinit var chart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pie_chart,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        chart = binding.pieChart

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeViewModels()
        setupPieChart()
    }

    private fun subscribeViewModels() {
        lotteryTypeViewModel.selected.observe(viewLifecycleOwner, {
            statDataViewModel.setLotteryType(it)
            chart.centerText = getString(it.nameId)
        })

        statViewModel.isNoPayoutIncluded.observe(viewLifecycleOwner, {
            statDataViewModel.setIsNoPayoutIncluded(it)
        })

        statDataViewModel.resultCountsByType.observe(viewLifecycleOwner, {
            setPieChartData(it)
        })
    }

    private fun setupPieChart() {
        chart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(10f, 0f, 10f, 0f)

            //Center hole
            setDrawCenterText(true)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setCenterTextColor(getColor(requireContext(), android.R.color.tab_indicator_text))
            setCenterTextSize(17f)

            //Default angle
            rotationAngle = 0f

            //Rotation
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            //Entry style
            setDrawEntryLabels(false)

            //Legend
            legend.apply {
                isEnabled = true
                textSize = 15f
                textColor = Color.WHITE
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 0f
                yOffset = 0f
            }

            //Animation
            animateY(1400, Easing.EaseInOutQuad)
        }
    }

    private fun setPieChartData(results: List<ResultCount>) {
        val entries = mutableListOf<PieEntry>().apply {
            for(r in results) {
                if(r.count > 0) {
                    add(PieEntry(r.count.toFloat(), r.matchCount))
                }
            }
        }

        val colors = ArrayList<Int>()
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        val dataSet = PieDataSet(entries, getString(R.string.empty_string)).apply {
            setDrawIcons(false)
            sliceSpace = 3f
            this.colors = colors
            selectionShift = 0f
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(CustomPieNumberFormatter(chart))
            setValueTextSize(20f)
            setValueTextColor(Color.WHITE)
        }

        chart.apply {
            this.data = data

            // undo all highlights
            highlightValues(null)
            invalidate()
        }
    }
}

class CustomPieNumberFormatter(private var pieChart: PieChart?) : PercentFormatter(pieChart) {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return if (pieChart != null && pieChart!!.isUsePercentValuesEnabled) {
            if (value < OVERLAP_LIMIT_PERCENT)
                ""
            else
                getFormattedValue(value)
        } else {
            // raw value, skip percent sign
            mFormat.format(value.toDouble())
        }
    }

    companion object {
        private const val OVERLAP_LIMIT_PERCENT = 1
    }
}
