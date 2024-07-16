package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentStoreBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
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

        val adapter = StoreListAdapter(viewModel, parentFragmentManager)
        binding.section1RecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.section1RecyclerView.adapter = adapter

        val adapter2 = StoreListAdapter(viewModel, parentFragmentManager)
        binding.section2RecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.section2RecyclerView.adapter = adapter2

        val adapter3 = StoreListAdapter(viewModel, parentFragmentManager)
        binding.section3RecyclerView.layoutManager =
            GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        binding.section3RecyclerView.adapter = adapter3

        //  viewModel.event(StoreViewModel.Event.FetchSkills)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        handleState(state, adapter, adapter2, adapter3)
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
        adapter1: StoreListAdapter,
        adapter2: StoreListAdapter,
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
                Log.d("HANDLE STATE: ", "${state.data.size}")
                adapter1.setData(state.data.subList(0, 25))
                adapter2.setData(state.data.subList(0, 25))
                adapter3.setData(state.data.subList(0, 25))
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