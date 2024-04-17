package com.hfad.palamarchuksuperapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.compose.animation.core.keyframes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.hfad.palamarchuksuperapp.data.Skill
import com.hfad.palamarchuksuperapp.databinding.ListItemBottomSheetBinding
import javax.inject.Inject

class BottomSheetFragment (private val skill: Skill = Skill()) : BottomSheetDialogFragment() {

    private var _binding: ListItemBottomSheetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListItemBottomSheetBinding.inflate(inflater, container, false)
        if (skill.id != null) {
            binding.apply {
                skillNameField.setText(skill.name)
                skillDescriptionField.setText(skill.description)
                skillDateField.setText(skill.date.toString())
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

        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}