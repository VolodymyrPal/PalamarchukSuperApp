package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.presentation.common.RecyclerSkillForViewModel
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
        RecyclerSkillForViewModel(
            Skill(
                name = "Compose",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val two =
        RecyclerSkillForViewModel(Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID()))
    private val three = RecyclerSkillForViewModel(
        Skill(
            name = "Recycler View",
            description = "Paging, DiffUtil, Adapter",
            id = UUID.randomUUID()
        )
    )
    private val four =
        RecyclerSkillForViewModel(
            Skill(
                name = "Retrofit",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val five =
        RecyclerSkillForViewModel(
            Skill(
                name = "Async",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val six = RecyclerSkillForViewModel(
        Skill(
            name = "Dependency injections",
            description = "Multi-module architecture, Component Dependencies, Multibindings, " +
                    "Components, Scope, Injections" +
                    " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
            id = UUID.randomUUID()
        )
    )
    private val seven =
        RecyclerSkillForViewModel(
            Skill(
                name = "RxJava",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val eight =
        RecyclerSkillForViewModel(
            Skill(
                name = "To learn",
                description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, " +
                        "REST API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",
                id = UUID.randomUUID()
            )
        )
    private val nine =
        RecyclerSkillForViewModel(
            Skill(
                name = "Recycler View",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val ten =
        RecyclerSkillForViewModel(Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID()))
    private val eleven = RecyclerSkillForViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )
    private val twelve = RecyclerSkillForViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )

    private val thirteen = RecyclerSkillForViewModel(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )


    private val dataListNewFlow = MutableStateFlow<MutableList<RecyclerSkillForViewModel>>(
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
    val date: StateFlow<List<RecyclerSkillForViewModel>> = this.dataListNewFlow.asStateFlow()

    fun getSkill(): List<Skill> {
        return date.value.map { it.skill }
    }

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.EditItem -> {
                updateSkillOrAdd(event.item, SkillsChangeConst.FullSkill)
                Log.d("Skill: ", "Asked for editing ${date.value.size}")
            }
            is UiEvent.DeleteItem -> {
                deleteSkill(event.item)
                Log.d("Skill: ", "Asked for deleting ${date.value.size}")
            }
            is UiEvent.MoveItemUp -> {
                moveToFirstPosition(event.item)
                Log.d("Skill: ", "Asked for movingUp ${date.value.size}")
            }
            is UiEvent.ChangeVisible -> {
                changeVisible(event.item)
            }
            is UiEvent.AddItem -> {
                addSkill(skill = event.item.skill)
            }
        }
    }

    fun moveToFirstPosition(recyclerSkillForViewModel: RecyclerSkillForViewModel) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.remove(recyclerSkillForViewModel)
            newListNew.add(0, recyclerSkillForViewModel)
            dataListNewFlow.value = newListNew
        }
    }

    fun deleteSkill(recyclerSkillForViewModel: RecyclerSkillForViewModel) {
        viewModelScope.launch {
            dataListNewFlow.update { recyclerSkillList ->
                val index = recyclerSkillList.indexOf(recyclerSkillForViewModel)
                val newList = recyclerSkillList.toMutableList()
                if (index != -1) {
                    newList.remove(recyclerSkillForViewModel)
                }
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
                newList.add(RecyclerSkillForViewModel(skill))
                newList
            }
        }
    }

    fun changeVisible(recyclerSkillForViewModel: RecyclerSkillForViewModel) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(recyclerSkillForViewModel).let {
                    newList[it] = newList[it].copy(isVisible = !newList[it].isVisible)
                }
                newList
            }
        }
    }

    fun updateSkillOrAdd(recyclerSkillForViewModel: RecyclerSkillForViewModel, skillName: String, skillDescription: String) {
        viewModelScope.launch {
            dataListNewFlow.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(recyclerSkillForViewModel).let {
                    if (it != -1) {
                        newList[it] = RecyclerSkillForViewModel(
                            skill = Skill(
                                name = skillName,
                                description = skillDescription,
                                id = recyclerSkillForViewModel.skill.id
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

    fun updateSkillOrAdd(recyclerSkillForViewModel: RecyclerSkillForViewModel, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseSkill -> {
                    dataListNewFlow.update {
                        val newList = it.toMutableList()
                        newList.indexOf(recyclerSkillForViewModel).let {
                            Log.d("Was called:", "$it")
                            newList[it] = newList[it].copy(chosen = true)
                        }
                        newList
                    }

                }

                SkillsChangeConst.NotChooseSkill -> {
                    val newListNew = date.value.toMutableList()
                    newListNew.indexOf(recyclerSkillForViewModel).let {
                        newListNew[it] = newListNew[it].copy(chosen = false)
                    }
                    dataListNewFlow.value = newListNew
                }

                SkillsChangeConst.FullSkill -> {
                    dataListNewFlow.update { myListFlow ->
                        val newList = myListFlow.toMutableList()
                        val skillToChange = newList.find { it.skill.id == recyclerSkillForViewModel.skill.id }
                        if (skillToChange == null) {
                            newList.add(recyclerSkillForViewModel)
                        } else {
                            newList.indexOf(skillToChange).let {
                                newList[it] =
                                    newList[it].copy(skill = recyclerSkillForViewModel.skill)
                            }
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

sealed class UiEvent {
    data class EditItem(val item: RecyclerSkillForViewModel) : UiEvent()
    data class DeleteItem(val item: RecyclerSkillForViewModel) : UiEvent()
    data class MoveItemUp(val item: RecyclerSkillForViewModel) : UiEvent()
    data class ChangeVisible(val item: RecyclerSkillForViewModel) : UiEvent()
    data class AddItem(val item: RecyclerSkillForViewModel) : UiEvent()
}