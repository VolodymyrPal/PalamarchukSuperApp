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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentStoreBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
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
            viewModel.uiState.collectLatest {
                handleState(it, adapter)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.effect.flowWithLifecycle(
                viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
            ).collect {
                when (it) {
                    is RepoResult.Success -> {
                        adapter.setData(it.data)
                    }
                    else -> {}
                }
            }
                handleEffect(it)
            }

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}