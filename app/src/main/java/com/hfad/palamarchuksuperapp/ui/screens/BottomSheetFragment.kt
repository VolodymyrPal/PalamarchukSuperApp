package com.hfad.palamarchuksuperapp.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.hfad.palamarchuksuperapp.databinding.ListItemBottomSheetBinding
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsChangeConst
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BottomSheetFragment(
    private var skill: Skill = Skill(),
    private var viewModelEvent: (SkillsViewModel.Event) -> Unit,
) : BottomSheetDialogFragment() {

    private var _binding: ListItemBottomSheetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = ListItemBottomSheetBinding.inflate(inflater, container, false)
        binding.apply {
            skillNameField.setText(skill.name)
            skillDescriptionField.setText(skill.description)
            skillDateField.setText(
                SimpleDateFormat("dd MMMM: HH:mm", Locale.US).format(
                    skill.date
                )
            )
            skillDateField.inputType = EditorInfo.TYPE_NULL
            skillDateField.keyListener = null
            skillDateField.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    showDatePicker { selectedDate ->
                        skill =
                            skill.copy(
                                date = Date(
                                    selectedDate
                                )
                            )

                        binding.skillDateField.setText(
                            SimpleDateFormat(
                                "dd MMMM yyyy: HH:mm",
                                Locale.US
                            ).format(Date(selectedDate))
                        )
                    }
                }
            }

            skillDateField.setOnClickListener {
                showDatePicker { selectedDate ->
                    skill =
                        skill.copy(
                            date = Date(
                                selectedDate
                            )
                        )
                    binding.skillDateField.setText(
                        SimpleDateFormat(
                            "dd MMMM yyyy: HH:mm",
                            Locale.US
                        ).format(Date(selectedDate))
                    )
                }
            }
            saveSkillButton.setOnClickListener {
                skill = skill.copy(
                    uuid = skill.uuid,
                    name = binding.skillNameField.text.toString(),
                    description = binding.skillDescriptionField.text.toString()
                )

                viewModelEvent.invoke(
                    SkillsViewModel.Event.EditItem(
                        skill,
                        SkillsChangeConst.FullSkill
                    )
                )
                this@BottomSheetFragment.dismiss()
            }
        }

        val view = binding.root
        return view
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            onDateSelected(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}