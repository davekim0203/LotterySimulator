package org.dave.lotterysimulatorwithstat.adapter

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.databinding.ListItemPastWinningNumberBinding

/**
 * Removed DiffUtil because:
 *   - RandomNumberFragment: list is not scrolled to the top by default when
 *     old and new items are them same
 */
class PastMatchAdapter : RecyclerView.Adapter<PastMatchAdapter.ViewHolder>() {

    var data =  listOf<PastWinningNumber>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: ListItemPastWinningNumberBinding,
        context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val mContext = context

        fun bind(item: PastWinningNumber) {
            binding.pastWinningNumber = item
            binding.executePendingBindings()
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(
                adapterPosition,
                PAST_MATCH_COPY_TO_CLIPBOARD_MENU_ID,
                0,
                mContext.getString(R.string.menu_copy_to_clipboard)
            )
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemPastWinningNumberBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }

            const val PAST_MATCH_COPY_TO_CLIPBOARD_MENU_ID = 110
        }
    }
}

data class PastWinningNumber (
    var drawDate: String,
    var winningNumbers: String,
    var matchCount: Pair<Int, Boolean>
)