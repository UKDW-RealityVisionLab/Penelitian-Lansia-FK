package com.example.sinauopencvkotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.example.sinauopencvkotlin.databinding.ActivityChartBinding


class ChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chartView.setProgressBar(binding.progressBar)

        var data : ArrayList<Record>? = intent.getParcelableArrayListExtra("romData")
        var cartesian : Cartesian = AnyChart.line()

        cartesian.animation(true)
        cartesian.padding(10.0, 5.0, 5.0, 5.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair().yLabel(true)
        cartesian.crosshair().yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("Range of Motion")

        cartesian.xAxis(0).title("Time (s)")
        cartesian.yAxis(0).labels().padding(1.0, 1.0, 1.0, 1.0)

        var seriesData: MutableList<DataEntry> = ArrayList()

        if (data != null) {
            for (i in data) {
                seriesData.add(CustomDataEntry(i.sec.toString(), i.angle))
            }
        }

        var set : Set = Set.instantiate()
        set.data(seriesData)

        var drawLine : Mapping = set.mapAs("{ x: 'x', value: 'value' }")

        var series1 : Line = cartesian.line(drawLine)
        series1.name("ROM")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        binding.chartView.setChart(cartesian)
    }

    private class CustomDataEntry(x: String, value: Double)
        : ValueDataEntry(x, value) {
    }
}