package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class SkillsViewModel : ViewModel() {

    @Inject lateinit var myRepository: SkillsRepositoryImpl

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

    fun getSkill(): List<Skill> {
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

    fun deleteSkill (position: Int) {
        viewModelScope.launch {
            dataListNewFlow.update {
                val newList = it.toMutableList()
                newList.removeAt(position)
                newList
            }
        }
    }

    fun addSkill(skill: Skill) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.add(RecyclerSkill(skill))
                newList
            }
        }
    }

    fun updateSkill(recyclerSkill: RecyclerSkill, skillName: String, skillDescription: String) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(recyclerSkill).let {
                    if (it != -1) {
                        newList[it] = RecyclerSkill(
                            skill = Skill(
                                name = skillName,
                                description = skillDescription,
                                id = recyclerSkill.skill.id
                            )
                        )
                    } else {
                        throw Exception("Skill not found")
                    }
                }
                newList
            }
        }
    }
    fun updateSkill(recyclerSkill: RecyclerSkill, changeConst: Int) {
        viewModelScope.launch {
            when (changeConst) {
                EXPANDED_SKILL -> {
                    dataListNewFlow.update {
                        val newList = it.toMutableList()
                        newList.indexOf(recyclerSkill).let {
                            newList[it] = newList[it].copy(isExpanded = true)
                        }
                        newList
                    }
                }
                NON_EXPANDED_SKILL -> {

                    dataListNewFlow.update {
                        val newList = it.toMutableList()
                        newList.indexOf(recyclerSkill).let {
                            newList[it] = newList[it].copy(isExpanded = false)
                        }
                        newList
                    }
                }
                CHOOSE_SKILL -> {
                    dataListNewFlow.update {
                        val newList = it.toMutableList()
                        newList.indexOf(recyclerSkill).let {
                            newList[it] = newList[it].copy(chosen = true)
                        }
                        newList
                    }

                }
                NOT_CHOOSE_SKILL -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkill).let {
                        newListNew[it] = newListNew[it].copy(chosen = false)
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
    }
}