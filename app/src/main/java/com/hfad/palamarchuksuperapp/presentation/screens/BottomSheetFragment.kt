package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.databinding.ListItemBottomSheetBinding
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkill
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import java.util.UUID

class BottomSheetFragment (private val recyclerSkill: RecyclerSkill = RecyclerSkill(Skill()), private val viewModel: SkillsViewModel) : BottomSheetDialogFragment() {

    private var _binding: ListItemBottomSheetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListItemBottomSheetBinding.inflate(inflater, container, false)
        if (recyclerSkill.skill.id != null) {
            binding.apply {
                skillNameField.setText(recyclerSkill.skill.name)
                skillDescriptionField.setText(recyclerSkill.skill.description)
                skillDateField.setText(recyclerSkill.skill.date.toString())
            }
        }
        binding.skillDateField.inputType = EditorInfo.TYPE_NULL
        binding.skillDateField.keyListener = null

        binding.skillDateField.setOnClickListener {
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
                .show(parentFragmentManager, "")
        }

        binding.saveSkillButton.setOnClickListener {
            if (recyclerSkill.skill.id == null) {
                val skillName = binding.skillNameField.text.toString()
                val skillDescription = binding.skillDescriptionField.text.toString()
                viewModel.addSkill(Skill(UUID.randomUUID(), skillName, skillDescription))
                this.dismiss()
            } else {
                val skillName = binding.skillNameField.text.toString()
                val skillDescription = binding.skillDescriptionField.text.toString()
                viewModel.updateSkill(recyclerSkill, skillName, skillDescription)
                this.dismiss()
            }
        }

        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}