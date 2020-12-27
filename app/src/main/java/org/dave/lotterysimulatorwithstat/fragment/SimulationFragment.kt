package org.dave.lotterysimulatorwithstat.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter
import org.dave.lotterysimulatorwithstat.adapter.TicketAdapter.ViewHolder.Companion.TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID
import org.dave.lotterysimulatorwithstat.databinding.FragmentSimulationBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.SimulationViewModel

@AndroidEntryPoint
class SimulationFragment : Fragment() {

    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()
    private val simulationViewModel: SimulationViewModel by viewModels()
    private lateinit var binding: FragmentSimulationBinding
    private lateinit var ticketAdapter: TicketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_simulation, container, false)
        binding.apply {
            simulationVM = simulationViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeViewModels()
        registerForContextMenu(binding.tvWinningNumbers)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.simulation_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.simulation_result -> {
                simulationViewModel.toggleShowResults()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.tv_winning_numbers && binding.tvWinningNumbers.text != getString(R.string.start_simulation_text)) {
            val inflater: MenuInflater = requireActivity().menuInflater
            inflater.inflate(R.menu.copy_to_clipboard, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.copy_to_clipboard -> {
                simulationViewModel.copyNumbersToClipboard(requireContext())
            }

            TICKET_NUMBERS_COPY_TO_CLIPBOARD_MENU_ID -> {
                val textToCopy: String =
                    HtmlCompat.fromHtml(
                        ticketAdapter.data[item.groupId].numbers,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString().trim()
                simulationViewModel.copyNumbersToClipboard(requireContext(), textToCopy)
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun setupRecyclerView() {
        ticketAdapter = TicketAdapter()
        binding.ticketList.adapter = ticketAdapter
    }

    private fun subscribeViewModels() {
        lotteryTypeViewModel.selected.observe(viewLifecycleOwner, {
            simulationViewModel.setTicketType(it)
        })

        simulationViewModel.showResults.observe(viewLifecycleOwner, {
            if(it) showResults() else hideResults()
        })

        simulationViewModel.dialogId.observe(viewLifecycleOwner, {
            when(it) {
                NO_NUMBER_WARNING_DIALOG_ID ->
                    showWarningDialog(getString(R.string.num_of_tickets_warning_dialog_message_no_number))
                ZERO_NUMBER_WARNING_DIALOG_ID ->
                    showWarningDialog(getString(R.string.num_of_tickets_warning_dialog_message_greater_than_0))
            }
        })

        simulationViewModel.showCopiedToClipboardToast.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_numbers_copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun showResults() {
        TransitionManager.beginDelayedTransition(
            binding.resultContainer, TransitionSet()
                .addTransition(ChangeBounds())
        )

        TransitionManager.beginDelayedTransition(
            binding.ticketList, TransitionSet()
                .addTransition(ChangeBounds())
        )

        val params: ViewGroup.LayoutParams = binding.tvResults.layoutParams
        params.height = 120
        binding.tvResults.layoutParams = params
    }

    private fun hideResults() {
        TransitionManager.beginDelayedTransition(
            binding.resultContainer, TransitionSet()
                .addTransition(ChangeBounds())
        )

        TransitionManager.beginDelayedTransition(
            binding.ticketList, TransitionSet()
                .addTransition(ChangeBounds())
        )

        val params: ViewGroup.LayoutParams = binding.tvResults.layoutParams
        params.height = 0
        binding.tvResults.layoutParams = params
    }

    private fun showWarningDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.num_of_tickets_warning_dialog_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.dialog_ok_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        const val NO_NUMBER_WARNING_DIALOG_ID = 0
        const val ZERO_NUMBER_WARNING_DIALOG_ID = 1
    }
}
