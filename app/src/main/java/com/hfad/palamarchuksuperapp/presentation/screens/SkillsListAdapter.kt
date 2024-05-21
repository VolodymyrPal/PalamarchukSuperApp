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
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkillFowViewModel
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsChangeConst.*
import com.hfad.palamarchuksuperapp.presentation.viewModels.SkillsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SkillsListAdapter(
    private val viewModel: SkillsViewModel,
    private val fragmentManager: FragmentManager
) :
    ListAdapter<RecyclerSkillFowViewModel, SkillsListAdapter.SkillHolder>(SkillDiffItemCallback()) {

    private val asyncListDiffer = AsyncListDiffer(this, SkillDiffItemCallback())

    fun setDate(skillList: List<RecyclerSkillFowViewModel>) {
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
        fun bind(recyclerSkillFowViewModel: RecyclerSkillFowViewModel) {

            binding.materialCheckBox.isChecked = recyclerSkillFowViewModel.chosen

            binding.skillTitle.text = recyclerSkillFowViewModel.skill.name.uppercase(Locale.getDefault())
            //    binding.skillDescription.text = skill.description
            binding.skillDate.text =
                SimpleDateFormat("dd MMMM yyyy: HH:mm", Locale.US).format(recyclerSkillFowViewModel.skill.date)
            binding.moreButton.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.moreButton)
                popupMenu.inflate(R.menu.skill_recycler_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.menu_option_edit -> {
                            val bottomSheetFragment =
                                BottomSheetFragment(recyclerSkillFowViewModel, viewModel = myViewModel)
                            bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")

                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked edit ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_option_delete -> {
                            myViewModel.deleteSkill(myViewModel.date.value.indexOf(recyclerSkillFowViewModel))
                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked delete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_item_moveUp -> {
                            myViewModel.moveToFirstPosition(recyclerSkillFowViewModel)
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
                recyclerSkillFowViewModel.chosen = !recyclerSkillFowViewModel.chosen
                if (recyclerSkillFowViewModel.chosen) myViewModel.updateSkill(recyclerSkillFowViewModel, ChooseSkill)
                else myViewModel.updateSkill(recyclerSkillFowViewModel, NotChooseSkill)
                binding.materialCheckBox.isChecked = recyclerSkillFowViewModel.chosen
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
                expandOrHide(recyclerSkillFowViewModel)
            }

            binding.skillDescription.text = recyclerSkillFowViewModel.skill.description
            binding.materialCheckBox.isChecked = recyclerSkillFowViewModel.chosen


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


        private fun expandOrHide(recyclerSkillFowViewModel: RecyclerSkillFowViewModel) {

            if (!recyclerSkillFowViewModel.isExpanded) {
                binding.skillDescription.maxLines = Int.MAX_VALUE
                binding.skillDescription.text = recyclerSkillFowViewModel.skill.description

                val layoutParamsDescription = binding.skillDescription.layoutParams
                layoutParamsDescription.height = LayoutParams.MATCH_PARENT
                binding.skillDescription.layoutParams = layoutParamsDescription

                val layoutParamsCard = binding.skillCard.layoutParams
                layoutParamsCard.height = LayoutParams.WRAP_CONTENT
                binding.skillCard.layoutParams = layoutParamsCard

                binding.expandDetails.text = "<< Hide"
                recyclerSkillFowViewModel.isExpanded = true

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
                recyclerSkillFowViewModel.isExpanded = false
            }
        }
    }

    class SkillDiffItemCallback : DiffUtil.ItemCallback<RecyclerSkillFowViewModel>() {
        override fun areItemsTheSame(oldItem: RecyclerSkillFowViewModel, newItem: RecyclerSkillFowViewModel): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: RecyclerSkillFowViewModel, newItem: RecyclerSkillFowViewModel): Boolean =
            oldItem.skill.description == newItem.skill.description
    }
}
