package com.hfad.palamarchuksuperapp.view.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.Skill
import com.hfad.palamarchuksuperapp.data.SkillsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class RecyclerSkill (
    val skill: Skill,
    var chosen: Boolean = false,
    var isExpandable: Boolean = false,
    var isExpanded: Boolean = false
    )

class SkillsViewModel : ViewModel(), SkillsDataSource {

//    @Inject lateinit var myRepository: MyRepository

    private val one =
        RecyclerSkill(Skill(name = "Compose", description = "One, Two, Three", id = UUID.randomUUID()))
    private val two = RecyclerSkill(Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID()))
    private val three = RecyclerSkill(Skill(name = "Recycler View", description = "Paging, DiffUtil, Adapter", id = UUID.randomUUID() ))
    private val four =
        RecyclerSkill(Skill(name = "Retrofit", description = "One, Two, Three", id = UUID.randomUUID()))
    private val five =
        RecyclerSkill(Skill(name = "Async", description = "One, Two, Three", id = UUID.randomUUID()))
    private val six = RecyclerSkill(Skill(
        name = "Dependency injections",
        description = "Multi-module architecture, Component Dependencies, Multibindings, Components, Scope, Injections" +
                " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
        id = UUID.randomUUID()
    ))
    private val seven =
        RecyclerSkill(Skill(name = "RxJava", description = "One, Two, Three", id = UUID.randomUUID()))
    private val eight =
        RecyclerSkill(Skill(name = "To learn", description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, REST API!!!, JSON!, PUSH NOTIFICATIONS!!!, ", id = UUID.randomUUID()))
    private val nine =
        RecyclerSkill(Skill(name = "Recycler View", description = "One, Two, Three", id = UUID.randomUUID()))
    private val ten = RecyclerSkill(Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID()))
    private val eleven = RecyclerSkill(Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID()))
    private val twelve = RecyclerSkill(Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID()))

    private val thirteen = RecyclerSkill(Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID()))



    private val dataListNewFlow = MutableStateFlow<MutableList<RecyclerSkill>>(mutableListOf(one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen))
    val date: StateFlow<List<RecyclerSkill>> = this.dataListNewFlow.asStateFlow()

    override fun getSkill(): List<Skill> {
        return date.value.map { it.skill }
    }
    fun moveToFirstPosition (recyclerSkill: RecyclerSkill) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.remove(recyclerSkill)
            newListNew.add(0, recyclerSkill)
            dataListNewFlow.value = newListNew
        }
    }

    override fun deleteSkill (position: Int) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.removeAt(position)
            dataListNewFlow.value = newListNew
        }
    }

    override fun addSkill(skill: Skill) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.add(RecyclerSkill(skill))
            dataListNewFlow.value = newListNew
        }
    }

    fun updateSkill(recyclerSkill: RecyclerSkill, skillName: String, skillDescription: String) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.indexOf(recyclerSkill).let {
                newListNew[it] = RecyclerSkill(skill = Skill(name = skillName, description = skillDescription, id = recyclerSkill.skill.id))
            }
            dataListNewFlow.value = newListNew
        }
    }
    fun updateSkill(recyclerSkill: RecyclerSkill, changeConst: Int) {
        viewModelScope.launch {
            when (changeConst) {
                EXPANDED_SKILL -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(isExpanded = true)
                    }
                    dataListNewFlow.value = newListNew
                }
                NON_EXPANDED_SKILL -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(isExpanded = false)
                    }
                    dataListNewFlow.value = newListNew
                }
                CHOOSE_SKILL -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(chosen = true, isExpandable = false, isExpanded = false)
                    }
                    dataListNewFlow.value = newListNew
                }
                NOT_CHOOSE_SKILL -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(chosen = false, isExpandable = false, isExpanded = false)
                    }
                    dataListNewFlow.value = newListNew
                }
                EXPANDABLE -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(isExpandable = true)
                    }
                    dataListNewFlow.value = newListNew
                }
                NOT_EXPANDABLE -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(isExpandable = false)
                    }
                    dataListNewFlow.value = newListNew
                }
            }
        }
    }

    companion object {
        const val EXPANDED_SKILL = 1
        const val NON_EXPANDED_SKILL = 2
        const val CHOOSE_SKILL = 3
        const val NOT_CHOOSE_SKILL = 4
        const val EXPANDABLE = 5
        const val NOT_EXPANDABLE = 6
    }

}