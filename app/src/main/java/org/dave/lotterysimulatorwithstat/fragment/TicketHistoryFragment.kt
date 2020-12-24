package org.dave.lotterysimulatorwithstat.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter.ViewHolder.Companion.TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID
import org.dave.lotterysimulatorwithstat.databinding.FragmentTicketHistoryBinding
import org.dave.lotterysimulatorwithstat.util.copyToClipboard
import org.dave.lotterysimulatorwithstat.viewmodel.TicketHistoryViewModel

@AndroidEntryPoint
class TicketHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTicketHistoryBinding
    private val ticketHistoryViewModel: TicketHistoryViewModel by viewModels()
    private lateinit var ticketAdapter: TicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_ticket_history, container, false
        )
        binding.ticketHistoryViewModel = ticketHistoryViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        setupRecyclerView()
        subscribeViewModels()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.clear_history -> {
            showConfirmDialog()
            true
        }

        R.id.history_info -> {
            showInfoDialog()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID -> {
                val textToCopy: String = HtmlCompat.fromHtml(
                    ticketAdapter.data[item.groupId].numbers,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString().trim()
                copyToClipboard(requireContext(), textToCopy)
                ticketHistoryViewModel.showCopiedToClipboardToast()
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun setupRecyclerView() {
        ticketAdapter = TicketAdapter()
        binding.ticketHistoryList.adapter = ticketAdapter
    }

    private fun subscribeViewModels() {
        ticketHistoryViewModel.showCopiedToClipboardToast.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_numbers_copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.clear_data_confirm_dialog_title))
            .setMessage(getString(R.string.clear_data_confirm_dialog_message))
            .setPositiveButton(getString(R.string.clear_data_confirm_dialog_positive_button)) { dialog, _ ->
                ticketHistoryViewModel.clearAllTickets()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.info_dialog_title))
            .setMessage(getString(R.string.info_dialog_message))
            .setPositiveButton(getString(R.string.info_dialog_positive_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}