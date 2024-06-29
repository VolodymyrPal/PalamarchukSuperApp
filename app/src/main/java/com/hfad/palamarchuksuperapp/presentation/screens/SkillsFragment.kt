package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentSkillsBinding
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.State
import kotlinx.coroutines.launch
import javax.inject.Inject


class SkillsFragment : Fragment() {
    private var _binding: FragmentSkillsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    @Inject
    lateinit var skillsViewModelFactory: GenericViewModelFactory<SkillsViewModel>
    private val viewModel by lazy {
        ViewModelProvider(this, skillsViewModelFactory)[SkillsViewModel::class.java]
    }

    @Inject
    lateinit var vibe: AppVibrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        val view = binding.root

        view.context.appComponent.inject(this)

        binding.skillsRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SkillsListAdapter(viewModel, parentFragmentManager)
        binding.skillsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch { viewModel.fetchSkills() }
                launch {
                    viewModel.uiState.collect {
                        when (it) {
                            State.Empty -> {
                                binding.progressBarCalories.visibility = View.GONE
                                binding.errorEmptyText.isVisible = true
                            }

                            State.Processing -> {
                                binding.errorEmptyText.isVisible = false
                                binding.progressBarCalories.visibility = View.VISIBLE
                            }

                            is State.Error -> {
                                binding.progressBarCalories.visibility = View.GONE
                                binding.errorEmptyText.text = it.exception.message
                                binding.errorEmptyText.isVisible = true
                            }

                            is State.Success -> {
                                binding.errorEmptyText.isVisible = false
                                binding.progressBarCalories.visibility = View.GONE
                                lifecycleScope.launch {
                                    adapter.setData(it.data)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.floatingActionButton.setOnClickListener {
            vibe.standardClickVibration()
            val bottomSheetFragment = BottomSheetFragment(viewModel = viewModel)
            bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}