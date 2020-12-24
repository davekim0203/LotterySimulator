package org.dave.lotterysimulatorwithstat.adapter

import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.database.Ticket
import org.dave.lotterysimulatorwithstat.databinding.ListItemTicketBinding

/**
 * Removed DiffUtil because:
 *   - TicketHistoryFragment: list is not scrolled to the top by default when
 *     new items are inserted to database in TicketHistoryFragment
 *   - SimulationFragment: old and new items are (almost) never be the same anyway
 */
class TicketAdapter : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    var data =  listOf<Ticket>()
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
        private val binding: ListItemTicketBinding,
        context: Context
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val mContext = context

        fun bind(item: Ticket) {
            binding.ticket = item
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
                TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID,
                0,
                mContext.getString(R.string.menu_copy_to_clipboard)
            )
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTicketBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }

            const val TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID = 111
        }
    }
}