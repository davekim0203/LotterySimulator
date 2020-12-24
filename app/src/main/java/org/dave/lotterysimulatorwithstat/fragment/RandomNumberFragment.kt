package org.dave.lotterysimulatorwithstat.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import org.dave.lotterysimulatorwithstat.R
import org.dave.lotterysimulatorwithstat.adapter.PastMatchAdapter
import org.dave.lotterysimulatorwithstat.adapter.PastMatchAdapter.ViewHolder.Companion.PAST_MATCH_COPY_TO_CLIPBOARD_MENU_ID
import org.dave.lotterysimulatorwithstat.databinding.FragmentRandomNumberBinding
import org.dave.lotterysimulatorwithstat.viewmodel.LotteryTypeViewModel
import org.dave.lotterysimulatorwithstat.viewmodel.RandomNumberViewModel

@AndroidEntryPoint
class RandomNumberFragment : Fragment() {

    private val lotteryTypeViewModel: LotteryTypeViewModel by activityViewModels()
    private lateinit var randomNumberViewModel: RandomNumberViewModel
    private lateinit var binding: FragmentRandomNumberBinding
    private lateinit var pastMatchAdapter: PastMatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        randomNumberViewModel =
            ViewModelProvider(requireActivity()).get(RandomNumberViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_random_number,
            container,
            false
        )
        binding.apply {
            lotteryTypeVM = lotteryTypeViewModel
            randomNumberVM = randomNumberViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeViewModel()
        registerForContextMenu(binding.tvRandomNumbers)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.tv_random_numbers && binding.tvRandomNumbers.text != getString(R.string.default_numbers_text)) {
            val inflater: MenuInflater = requireActivity().menuInflater
            inflater.inflate(R.menu.copy_to_clipboard, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.copy_to_clipboard -> {
                randomNumberViewModel.copyNumbersToClipboard(requireContext())
            }

            PAST_MATCH_COPY_TO_CLIPBOARD_MENU_ID -> {
                val textToCopy: String =
                    HtmlCompat.fromHtml(
                        pastMatchAdapter.data[item.groupId].winningNumbers,
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    ).toString().trim()
                randomNumberViewModel.copyNumbersToClipboard(requireContext(), textToCopy)
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun setupRecyclerView() {
        pastMatchAdapter = PastMatchAdapter()
        binding.pastMatchesList.adapter = pastMatchAdapter
    }

    private fun subscribeViewModel() {
        lotteryTypeViewModel.selected.observe(viewLifecycleOwner, {
            randomNumberViewModel.setLotteryType(it)
        })

        randomNumberViewModel.showCopiedToClipboardToast.observe(viewLifecycleOwner, {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_numbers_copied_to_clipboard),
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}
