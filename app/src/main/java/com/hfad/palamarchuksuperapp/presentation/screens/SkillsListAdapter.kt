package com.hfad.palamarchuksuperapp.presentation.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintProperties.WRAP_CONTENT
import androidx.constraintlayout.widget.Constraints.LayoutParams
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.databinding.ListItemSkillsBinding
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkill
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel.Companion.CHOOSE_SKILL
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel.Companion.NOT_CHOOSE_SKILL
import java.text.SimpleDateFormat
import java.util.Locale

class SkillsListAdapter(
    private val viewModel: SkillsViewModel,
    private val fragmentManager: FragmentManager
) :
    ListAdapter<RecyclerSkill, SkillsListAdapter.SkillHolder>(SkillDiffItemCallback()) {

    private val asyncListDiffer = AsyncListDiffer(this, SkillDiffItemCallback())

    fun setDate(skillList: List<RecyclerSkill>) {
        asyncListDiffer.submitList(skillList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillHolder {
        val binding =
            ListItemSkillsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillHolder(binding, viewModel, fragmentManager)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: SkillHolder, position: Int) {
        val skill = asyncListDiffer.currentList[position]
        holder.bind(skill)
    }


    class SkillHolder(
        private val binding: ListItemSkillsBinding,
        private val myViewModel: SkillsViewModel,
        private val parentFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(binding.root) {
        private val startedHeight = binding.skillCard.layoutParams.height
        fun bind(recyclerSkill: RecyclerSkill) {

            binding.materialCheckBox.isChecked = recyclerSkill.chosen

            binding.skillTitle.text = recyclerSkill.skill.name.uppercase(Locale.getDefault())
            //    binding.skillDescription.text = skill.description
            binding.skillDate.text =
                SimpleDateFormat("dd MMMM: HH:mm", Locale.US).format(recyclerSkill.skill.date)
            binding.moreButton.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.moreButton)
                popupMenu.inflate(R.menu.skill_recycler_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.menu_option_edit -> {
                            val bottomSheetFragment =
                                BottomSheetFragment(recyclerSkill, viewModel = myViewModel)
                            bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")

                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked edit ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_option_delete -> {
                            myViewModel.deleteSkill(myViewModel.date.value.indexOf(recyclerSkill))
                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked delete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_item_moveUp -> {
                            myViewModel.moveToFirstPosition(recyclerSkill)
                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked moving Up",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    true
                }
                popupMenu.show()
            }

            fun onCheckboxClicked() {
                recyclerSkill.chosen = !recyclerSkill.chosen
                if (recyclerSkill.chosen) myViewModel.updateSkill(recyclerSkill, CHOOSE_SKILL)
                else myViewModel.updateSkill(recyclerSkill, NOT_CHOOSE_SKILL)
                binding.materialCheckBox.isChecked = recyclerSkill.chosen
            }

            binding.cardSkill.setOnClickListener {
                onCheckboxClicked()
            }
            binding.materialCheckBox.setOnClickListener {
                onCheckboxClicked()
            }


            //Log.d("My lines: ","${binding.skillDescription.layout.lineCount}")

            binding.expandDetails.isEnabled = false
            binding.expandDetails.isVisible = false

//            if (recyclerSkill.isExpandable) {
//                binding.expandDetails.isEnabled = true
//                binding.expandDetails.isVisible = true
//                binding.expandDetails.setOnClickListener {
//                    expandOrHide(recyclerSkill)
//                }
//            }

            binding.expandDetails.post {
                if (binding.skillDescription.layout.getEllipsisCount(1) > 0) {
                    binding.expandDetails.isEnabled = true
                    binding.expandDetails.isVisible = true
                }
            }

//            if (recyclerSkill.isExpandable) {
//                binding.expandDetails.maxLines = 2
//                binding.expandDetails.isEnabled = true
//                binding.expandDetails.isVisible = true
//            }

            binding.expandDetails.setOnClickListener {
                expandOrHide(recyclerSkill)
            }

            binding.skillDescription.text = recyclerSkill.skill.description
            binding.materialCheckBox.isChecked = recyclerSkill.chosen


//            binding.expandDetails.post {
//                if (binding.skillDescription.lineCount > 2) {
//                    binding.expandDetails.isVisible = true
//                    binding.expandDetails.isEnabled = true
//                    binding.expandDetails.setOnClickListener {
//                        recyclerSkill.isExpandable = true
//                        expandOrHide(recyclerSkill)
//                    }
//                }
//            }
        }


        private fun expandOrHide(recyclerSkill: RecyclerSkill) {

            if (!recyclerSkill.isExpanded) {
                binding.skillDescription.maxLines = Int.MAX_VALUE
                binding.skillDescription.text = recyclerSkill.skill.description

                val layoutParamsDescription = binding.skillDescription.layoutParams
                layoutParamsDescription.height = LayoutParams.MATCH_PARENT
                binding.skillDescription.layoutParams = layoutParamsDescription

                val layoutParamsCard = binding.skillCard.layoutParams
                layoutParamsCard.height = LayoutParams.WRAP_CONTENT
                binding.skillCard.layoutParams = layoutParamsCard

                binding.expandDetails.text = "<< Hide"
                recyclerSkill.isExpanded = true

            } else {
//                binding.skillDescription.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(100))
//                binding.skillDescription.text = recyclerSkill.skill.description

                val layoutParamsDescription = binding.skillDescription.layoutParams
                layoutParamsDescription.height = WRAP_CONTENT
                binding.skillDescription.maxLines = 2
                binding.skillDescription.layoutParams = layoutParamsDescription

                val layoutParamsCard = binding.skillCard.layoutParams
                layoutParamsCard.height = startedHeight
                binding.skillCard.layoutParams = layoutParamsCard

                binding.expandDetails.text = "Details >>"
                recyclerSkill.isExpanded = false
            }
        }
    }

    class SkillDiffItemCallback : DiffUtil.ItemCallback<RecyclerSkill>() {
        override fun areItemsTheSame(oldItem: RecyclerSkill, newItem: RecyclerSkill): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: RecyclerSkill, newItem: RecyclerSkill): Boolean =
            oldItem.skill.description == newItem.skill.description
    }
}
