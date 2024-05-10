package com.hfad.palamarchuksuperapp.view.screens

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.palamarchuksuperapp.R
import com.hfad.palamarchuksuperapp.data.Skill
import com.hfad.palamarchuksuperapp.databinding.ListItemSkillsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SkillsListAdapter (private val viewModel: SkillsViewModel, private val fragmentManager: FragmentManager) :
    ListAdapter<Skill, SkillsListAdapter.SkillHolder>(SkillDiffItemCallback()) {

    private val asyncListDiffer = AsyncListDiffer(this, SkillDiffItemCallback())

    fun setDate (skillList: List<Skill>) {
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

        fun bind(skill: Skill) {

            binding.skillTitle.text = skill.name.uppercase(Locale.getDefault())
            binding.skillDescription.text = skill.description
            binding.skillDate.text =
                SimpleDateFormat("dd MMMM: HH:mm", Locale.US).format(skill.date)
            binding.moreButton.setOnClickListener {
                val popupMenu = PopupMenu(binding.root.context, binding.moreButton)
                popupMenu.inflate(R.menu.skill_recycler_menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.menu_option_edit -> {
                                val bottomSheetFragment = BottomSheetFragment(skill)
                                bottomSheetFragment.show(parentFragmentManager, "BSDialogFragment")

                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked edit ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        R.id.menu_option_delete -> {
                            myViewModel.deleteSkill(myViewModel.date.value.indexOf(skill))
                           // bindingAdapter?.notifyItemRemoved(bindingAdapterPosition)
                           // myViewModel.dataList.removeAt(myViewModel.dataList.indexOf(skill))
                            Toast.makeText(
                                this.binding.root.context,
                                "Clicked delete",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        R.id.menu_item_moveUp -> {
                            myViewModel.moveToFirstPosition(skill)

                         //   myViewModel.dataList.remove(skill)
                         //   myViewModel.dataList.add(0, skill)
                         //   bindingAdapter?.notifyItemMoved(bindingAdapterPosition, 0)

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
        }
    }

    class SkillDiffItemCallback : DiffUtil.ItemCallback<Skill>() {
        override fun areItemsTheSame(oldItem: Skill, newItem: Skill): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Skill, newItem: Skill): Boolean = oldItem.id == newItem.id
    }
}
