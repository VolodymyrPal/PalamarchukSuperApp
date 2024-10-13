package com.hfad.palamarchuksuperapp.ui.screens.adapters

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
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.ListItemSkillsBinding
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.ui.screens.BottomSheetFragment
import com.hfad.palamarchuksuperapp.ui.viewModels.SkillsViewModel
import kotlinx.collections.immutable.PersistentList
import java.text.SimpleDateFormat
import java.util.Locale

class SkillsListAdapter(
    private val viewModel: SkillsViewModel,
    private val fragmentManager: FragmentManager,
) : ListAdapter<SkillDomainRW, SkillsListAdapter.SkillHolder>(SkillDiffItemCallback()) {


    private val asyncListDiffer = AsyncListDiffer(this, SkillDiffItemCallback())

    fun setData(skillList: PersistentList<SkillDomainRW>) {
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
        private val parentFragmentManager: FragmentManager,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val startedHeight = binding.skillCard.layoutParams.height
        private val vibe = this.binding.root.context.appComponent.appVibrator()

        fun bind(skill: SkillDomainRW) {
            binding.materialCheckBox.isChecked = skill.chosen
            binding.skillTitle.text =
                skill.skill.name.uppercase(Locale.getDefault())
            binding.skillDate.text =
                SimpleDateFormat("dd MMMM yyyy: HH:mm", Locale.US).format(skill.skill.date)
            binding.moreButton.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.moreButton)
                popupMenu.inflate(R.menu.skill_recycler_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_option_edit -> {
                            vibe.standardClickVibration()
                            val bottomSheetFragment =
                                BottomSheetFragment(
                                    skill,
                                    viewModelEvent = myViewModel::event
                                )
                            bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")

                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked edi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_option_delete -> {
                            vibe.standardClickVibration()
                            myViewModel.deleteSkill(skill)
                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked delete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_item_moveUp -> {
                            vibe.standardClickVibration()
                            myViewModel.moveToFirstPosition(skill)
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
                vibe.standardClickVibration()
                skill.chosen = !skill.chosen
                binding.materialCheckBox.isChecked = skill.chosen
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

            binding.expandDetails.post { // TODO Change View, not params
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
                vibe.standardClickVibration()
                expandOrHide(skill)
            }

            binding.skillDescription.text = skill.skill.description
            binding.materialCheckBox.isChecked = skill.chosen


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


        private fun expandOrHide(skillDomainRW: SkillDomainRW) {

            if (!skillDomainRW.isExpanded) {
                binding.skillDescription.maxLines = Int.MAX_VALUE
                binding.skillDescription.text = skillDomainRW.skill.description

                val layoutParamsDescription = binding.skillDescription.layoutParams
                layoutParamsDescription.height = LayoutParams.MATCH_PARENT
                binding.skillDescription.layoutParams = layoutParamsDescription

                val layoutParamsCard = binding.skillCard.layoutParams
                layoutParamsCard.height = LayoutParams.WRAP_CONTENT
                binding.skillCard.layoutParams = layoutParamsCard

                binding.expandDetails.text = "<< Hide"
                skillDomainRW.isExpanded = true

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
                skillDomainRW.isExpanded = false
            }
        }
    }

    class SkillDiffItemCallback : DiffUtil.ItemCallback<SkillDomainRW>() {
        override fun areItemsTheSame(
            oldItem: SkillDomainRW,
            newItem: SkillDomainRW,
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: SkillDomainRW,
            newItem: SkillDomainRW,
        ): Boolean =
            oldItem.skill.description == newItem.skill.description
    }
}
