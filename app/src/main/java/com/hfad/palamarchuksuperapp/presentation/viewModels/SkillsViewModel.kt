package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkillFowViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class SkillsViewModel : ViewModel() {
    @Inject
    lateinit var myRepository: SkillsRepositoryImpl

    private val one =
        RecyclerSkillFowViewModel(
            Skill(
                name = "Compose",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val two =
        RecyclerSkillFowViewModel(Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID()))
    private val three = RecyclerSkillFowViewModel(
        Skill(
            name = "Recycler View",
            description = "Paging, DiffUtil, Adapter",
            id = UUID.randomUUID()
        )
    )
    private val four =
        RecyclerSkillFowViewModel(
            Skill(
                name = "Retrofit",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val five =
        RecyclerSkillFowViewModel(
            Skill(
                name = "Async",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val six = RecyclerSkillFowViewModel(
        Skill(
            name = "Dependency injections",
            description = "Multi-module architecture, Component Dependencies, Multibindings, Components, Scope, Injections" +
                    " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
            id = UUID.randomUUID()
        )
    )
    private val seven =
        RecyclerSkillFowViewModel(
            Skill(
                name = "RxJava",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val eight =
        RecyclerSkillFowViewModel(
            Skill(
                name = "To learn",
                description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, REST API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",
                id = UUID.randomUUID()
            )
        )
    private val nine =
        RecyclerSkillFowViewModel(
            Skill(
                name = "Recycler View",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val ten =
        RecyclerSkillFowViewModel(Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID()))
    private val eleven = RecyclerSkillFowViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )
    private val twelve = RecyclerSkillFowViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )

    private val thirteen = RecyclerSkillFowViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )


    private val dataListNewFlow = MutableStateFlow<MutableList<RecyclerSkillFowViewModel>>(
        mutableListOf(
            one,
            two,
            three,
            four,
            five,
            six,
            seven,
            eight,
            nine,
            ten,
            eleven,
            twelve,
            thirteen
        )
    )
    val date: StateFlow<List<RecyclerSkillFowViewModel>> = this.dataListNewFlow.asStateFlow()

    fun getSkill(): List<Skill> {
        return date.value.map { it.skill }
    }

    fun moveToFirstPosition(recyclerSkillFowViewModel: RecyclerSkillFowViewModel) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.remove(recyclerSkillFowViewModel)
            newListNew.add(0, recyclerSkillFowViewModel)
            dataListNewFlow.value = newListNew
        }
    }

    fun deleteSkill(position: Int) {

        viewModelScope.launch {
            dataListNewFlow.update { it ->
                val newList = it.toMutableList()
                newList.removeAt(position)
                newList.removeIf {
                    it.chosen
                }
                newList
            }
        }
    }

    fun addSkill(skill: Skill) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.add(RecyclerSkillFowViewModel(skill))
                newList
            }
        }
    }

    fun updateSkill(recyclerSkillFowViewModel: RecyclerSkillFowViewModel, skillName: String, skillDescription: String) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(recyclerSkillFowViewModel).let {
                    if (it != -1) {
                        newList[it] = RecyclerSkillFowViewModel(
                            skill = Skill(
                                name = skillName,
                                description = skillDescription,
                                id = recyclerSkillFowViewModel.skill.id
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

    fun updateSkill(recyclerSkillFowViewModel: RecyclerSkillFowViewModel, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {

                SkillsChangeConst.ChooseSkill -> {
                    dataListNewFlow.update {
                        val newList = it.toMutableList()
                        newList.indexOf(recyclerSkillFowViewModel).let {
                            Log.d("Was called:", "$it")
                            newList[it] = newList[it].copy(chosen = true)
                        }
                        newList
                    }

                }

                SkillsChangeConst.NotChooseSkill -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkillFowViewModel).let {
                        newListNew[it] = newListNew[it].copy(chosen = false)
                    }
                    dataListNewFlow.value = newListNew
                }

                SkillsChangeConst.FullSkill -> {
                    dataListNewFlow.update { myListFlow ->
                        val newList = myListFlow.toMutableList()
                        val skillToChange = newList.find { it.skill.id == recyclerSkillFowViewModel.skill.id }
                        newList.indexOf(skillToChange)?.let {
                            newList[it] = newList[it].copy(skill = recyclerSkillFowViewModel.skill)
                        }
                        newList
                    }
                }
            }
        }
    }
}

sealed class SkillsChangeConst {
    object ChooseSkill : SkillsChangeConst()
    object NotChooseSkill : SkillsChangeConst()
    object FullSkill : SkillsChangeConst()
}