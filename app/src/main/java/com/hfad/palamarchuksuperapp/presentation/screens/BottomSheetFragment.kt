package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.databinding.ListItemBottomSheetBinding
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsChangeConst
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BottomSheetFragment(
    private val skillDomainRW: SkillDomainRW = SkillDomainRW(
        Skill()
    ),
    private var viewModel: SkillsViewModel = SkillsViewModel(SkillsRepositoryImpl())
) : BottomSheetDialogFragment() {

    private var _binding: ListItemBottomSheetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }

    private var tempRecSkill = SkillDomainRW(Skill())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = ListItemBottomSheetBinding.inflate(inflater, container, false)
        if (skillDomainRW.skill.id != null) {
            binding.apply {
                skillNameField.setText(skillDomainRW.skill.name)
                skillDescriptionField.setText(skillDomainRW.skill.description)
                skillDateField.setText(
                    SimpleDateFormat("dd MMMM: HH:mm", Locale.US).format(
                        skillDomainRW.skill.date
                    )
                )
            }
        }
        binding.skillDateField.inputType = EditorInfo.TYPE_NULL
        binding.skillDateField.keyListener = null
        binding.skillDateField.setText(SimpleDateFormat("dd MMMM: HH:mm", Locale.US).format(Date()))



        binding.skillDateField.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                picker.show(parentFragmentManager, picker.toString())
                picker.addOnPositiveButtonClickListener {
                    tempRecSkill = tempRecSkill.copy(skill = Skill(date = Date(it)))
                    binding.skillDateField.setText(
                        SimpleDateFormat(
                            "dd MMMM yyyy: HH:mm",
                            Locale.US
                        ).format(Date(it))
                    )
                }
            }
        }

        binding.skillDateField.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.show(parentFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                tempRecSkill = tempRecSkill.copy(skill = Skill(date = Date(it)))
                binding.skillDateField.setText(
                    SimpleDateFormat(
                        "dd MMMM yyyy: HH:mm",
                        Locale.US
                    ).format(Date(it))
                )
            }
        }


        binding.saveSkillButton.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.show(parentFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                tempRecSkill = tempRecSkill.copy(skill = Skill(date = Date(it)))
                binding.skillDateField.setText(
                    SimpleDateFormat(
                        "dd MMMM yyyy: HH:mm",
                        Locale.US
                    ).format(Date(it))
                )
            }
        }

        binding.saveSkillButton.setOnClickListener {

            tempRecSkill = tempRecSkill.copy(
                skill = Skill(
                    id = skillDomainRW.skill.id ?: UUID.randomUUID(),
                    name = binding.skillNameField.text.toString(),
                    description = binding.skillDescriptionField.text.toString(),
                    date = Date(tempRecSkill.skill.date.time)
                )
            )

            viewModel.updateSkillOrAdd(tempRecSkill, SkillsChangeConst.FullSkill)
            this.dismiss()
        }
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}