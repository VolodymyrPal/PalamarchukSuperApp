package com.hfad.palamarchuksuperapp.view.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentSkillsBinding
import kotlinx.coroutines.launch
import javax.inject.Inject


class SkillsFragment: Fragment() {
    private var _binding: FragmentSkillsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    @Inject lateinit var viewModel: SkillsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        val view = binding.root

        view.context.appComponent.inject(this)

        binding.skillsRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SkillsListAdapter(viewModel, parentFragmentManager)
        binding.skillsRecyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
                viewModel.date.collect {
                    adapter.setDate(viewModel.date.value)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}