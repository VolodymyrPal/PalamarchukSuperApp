package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.SkillsRepositoryImpl
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.SkillToSkillDomain
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
        SkillDomainRW(
            Skill(
                name = "Compose",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val two =
        SkillDomainRW(Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID()))
    private val three = SkillDomainRW(
        Skill(
            name = "Recycler View",
            description = "Paging, DiffUtil, Adapter",
            id = UUID.randomUUID()
        )
    )
    private val four =
        SkillDomainRW(
            Skill(
                name = "Retrofit",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val five =
        SkillDomainRW(
            Skill(
                name = "Async",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val six = SkillDomainRW(
        Skill(
            name = "Dependency injections",
            description = "Multi-module architecture, Component Dependencies, Multibindings, " +
                    "Components, Scope, Injections" +
                    " Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
            id = UUID.randomUUID()
        )
    )
    private val seven =
        SkillDomainRW(
            Skill(
                name = "RxJava",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val eight =
        SkillDomainRW(
            Skill(
                name = "To learn",
                description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, " +
                        "RESTful API!!!, JSON!, PUSH NOTIFICATIONS!!!, ",
                id = UUID.randomUUID()
            )
        )
    private val nine =
        SkillDomainRW(
            Skill(
                name = "Recycler View",
                description = "One, Two, Three",
                id = UUID.randomUUID()
            )
        )
    private val ten =
        SkillDomainRW(Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID()))
    private val eleven = SkillDomainRW(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )
    private val twelve = SkillDomainRW(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )

    private val thirteen = SkillDomainRW(
        Skill(
            name = "Work with Image",
            description = "FileProvider,Coil",
            id = UUID.randomUUID()
        )
    )


    private val _skillsList = MutableStateFlow<MutableList<SkillDomainRW>>(
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
    val skillsList: StateFlow<List<SkillDomainRW>> = this._skillsList.asStateFlow()


    private val _state = MutableStateFlow(SkillViewState())
    val state: StateFlow<SkillViewState> = _state.asStateFlow()


    fun getSkill(): List<Skill> {
        return skillsList.value.map { it.skill }
    }

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.EditItem -> {
                updateSkillOrAdd(event.item, SkillsChangeConst.FullSkill)
                Log.d("Skill: ", "Asked for editing ${skillsList.value.size}")
            }
            is UiEvent.DeleteItem -> {
                deleteSkill(event.item)
            }
            is UiEvent.MoveItemUp -> {
                moveToFirstPosition(event.item)
                Log.d("Skill: ", "Asked for movingUp ${skillsList.value.size}")
            }
            is UiEvent.AddItem -> {
                addSkill(skill = event.item.skill)
            }

            is UiEvent.GetSkills -> {
                viewModelScope.launch {
                    fetchSkills()
                }
            }
        }
    }

    fun moveToFirstPosition(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            val newListNew = skillsList.value.toMutableList()
            newListNew.remove(skillDomainRW)
            newListNew.add(0, skillDomainRW)
            _skillsList.value = newListNew
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _skillsList.update { recyclerSkillList ->
                val index = recyclerSkillList.indexOf(skillDomainRW)
                val newList = recyclerSkillList.toMutableList()
                if (index != -1) {
                    newList.remove(skillDomainRW)
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
            _skillsList.update { currentList ->
                val newList = currentList.toMutableList()
                newList.add(SkillDomainRW(skill))
                newList
            }
        }
    }

    fun changeVisible(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _skillsList.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(skillDomainRW).let {
                    newList[it] = newList[it].copy(isVisible = !newList[it].isVisible)
                }
                newList
            }
        }
    }

    suspend fun fetchSkills() {

        _state.update { it.copy(loading = true) }

        try {
            val skills = myRepository.getSkillsFromDB()
            _state.update {
                it.copy(loading = false, skills = skills.map { SkillToSkillDomain.map(it) })
            }
        } catch (e: Exception) {
            _state.value =
                SkillViewState(loading = false, error = e.message ?: "Error fetching skills")
        }
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
                    _skillsList.update {
                        val newList = it.toMutableList()
                        newList.indexOf(skillDomainRW).let {
                            Log.d("Was called:", "$it")
                            newList[it] = newList[it].copy(chosen = !skillDomainRW.chosen)
                        }
                        newList
                    }

                }

                SkillsChangeConst.FullSkill -> {
                    _skillsList.update { myListFlow ->
                        val newList = myListFlow.toMutableList()
                        val skillToChange = newList.find { it.skill.id == skillDomainRW.skill.id }
                        if (skillToChange == null) {
                            newList.add(skillDomainRW)
                        } else {
                            newList.indexOf(skillToChange).let {
                                newList[it] =
                                    newList[it].copy(skill = skillDomainRW.skill)
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
    object ChooseOrNotSkill : SkillsChangeConst()
    object FullSkill : SkillsChangeConst()
}

sealed class UiEvent {
    data class EditItem(val item: SkillDomainRW) : UiEvent()
    data class DeleteItem(val item: SkillDomainRW) : UiEvent()
    data class MoveItemUp(val item: SkillDomainRW) : UiEvent()
    data class AddItem(val item: SkillDomainRW) : UiEvent()
    object GetSkills : UiEvent()
}

data class SkillViewState(
    var loading: Boolean = false,
    val skills: List<SkillDomainRW> = emptyList(),
    val error: String? = null,
)