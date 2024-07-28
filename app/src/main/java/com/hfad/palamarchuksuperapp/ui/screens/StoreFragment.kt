package com.hfad.palamarchuksuperapp.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentStoreBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.ui.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.ui.compose.WIDTH_ITEM
import com.hfad.palamarchuksuperapp.ui.screens.adapters.StoreListAdapter
import com.hfad.palamarchuksuperapp.ui.viewModels.State
import com.hfad.palamarchuksuperapp.ui.viewModels.StoreViewModel
import com.hfad.palamarchuksuperapp.ui.viewModels.GenericViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    @Inject
    lateinit var genericViewModelFactory: GenericViewModelFactory
    private val viewModel: StoreViewModel by viewModels { genericViewModelFactory }

    @Inject
    lateinit var vibe: AppVibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.context?.appComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        val view = binding.root

        val density = view.context.resources.displayMetrics.density
        val numSpan =
            (resources.displayMetrics.widthPixels / density / WIDTH_ITEM).coerceAtLeast(1f)
        val numOfRecyclerRows = 2
        val adapter3 = StoreListAdapter(viewModel, numOfRecyclerRows )
        binding.section3RecyclerView.layoutManager =
            GridLayoutManager(context, numSpan.toInt(), LinearLayoutManager.VERTICAL, false)
        binding.section3RecyclerView.adapter = adapter3
        (binding.section3RecyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position < numOfRecyclerRows) {
                        true -> {
                            numSpan.toInt()
                        }
                        else -> {
                            1
                        }
                    }
                }
            }

        binding.storeFab.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        //  viewModel.event(StoreViewModel.Event.FetchSkills)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        handleState(state, adapter3)
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        handleEffect(effect)
                    }
                }
            }
        }
        return view
    }


    private fun handleState(
        state: State<List<ProductDomainRW>>,
        adapter3: StoreListAdapter,
    ) {
        when (state) {
            is State.Empty -> {
                Log.d("HANDLE STATE: ", "$state")
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
            }

            is State.Error -> {
                Log.d("HANDLE STATE: ", "$state")
                Toast.makeText(
                    requireContext(),
                    state.exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            is State.Processing -> {
                Log.d("HANDLE STATE: ", "$state")

                Toast.makeText(requireContext(), "Processing", Toast.LENGTH_SHORT).show()
            }

            is State.Success -> {
                adapter3.setData(state.data)
            }
        }
    }

    private fun handleEffect(effect: StoreViewModel.Effect) {
        when (effect) {
            is StoreViewModel.Effect.OnBackPressed -> {
                requireActivity().onBackPressed()
            }

            is StoreViewModel.Effect.ShowToast -> {
                Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            }

            is StoreViewModel.Effect.Vibration -> {
                vibe.standardClickVibration()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}