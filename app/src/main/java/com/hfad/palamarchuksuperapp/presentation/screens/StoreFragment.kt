package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.data.entities.Product
import com.hfad.palamarchuksuperapp.databinding.FragmentStoreBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import kotlinx.coroutines.flow.Flow
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
    lateinit var storeViewModelFactory: GenericViewModelFactory<StoreViewModel>
    private val viewModel by lazy {
        ViewModelProvider(this, storeViewModelFactory)[StoreViewModel::class.java]
    }

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
        viewModel.event(StoreViewModel.Event.FetchSkills)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        handleState(state, adapter)
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


    private fun handleState(state: State<Flow<List<Product>>>, adapter: StoreListAdapter) {
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
                Log.d("HANDLE STATE: ", "$state")
                lifecycleScope.launch {
                    adapter.submitData(state.data)
                }
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