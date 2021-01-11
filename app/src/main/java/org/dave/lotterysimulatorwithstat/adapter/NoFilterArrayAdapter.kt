package org.dave.lotterysimulatorwithstat.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import org.dave.lotterysimulatorwithstat.R

class NoFilterArrayAdapter<T>(
    context: Context,
    textViewResourceId: Int,
    objects: List<T>
) : ArrayAdapter<T>(context, textViewResourceId, objects) {

    private val filter: Filter = NoFilter()
    var items: List<T> = objects

    override fun getFilter(): Filter {
        return filter
    }

    private inner class NoFilter : Filter() {
        override fun performFiltering(arg0: CharSequence): FilterResults {
            val result = FilterResults()
            result.values = items
            result.count = items.size
            return result
        }

        override fun publishResults(arg0: CharSequence, arg1: FilterResults) {
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        val tv = v.findViewById<TextView>(android.R.id.text1)
        tv.setCompoundDrawablesWithIntrinsicBounds(
            when (position) {
                0, 1 -> R.drawable.ic_united_states_of_america
                else -> R.drawable.ic_canada
            }, 0, 0, 0
        )
        return v
    }
}