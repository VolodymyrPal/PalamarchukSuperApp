package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(
    private val repository: SkillRepository,
) :
    GenericViewModel<List<SkillDomainRW>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

    suspend fun getData(): suspend () -> List<SkillDomainRW> {
        return { emptyList() }
    }

    override val uiState: StateFlow<State<List<SkillDomainRW>>> =
        emptyFlow<State<List<SkillDomainRW>>>().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = State(loading = true)
        )

    override val _dataFlow: Flow<List<SkillDomainRW>> = flow { emit(emptyList()) }

    suspend fun getDataFlow(): Flow<List<SkillDomainRW>> {
        TODO("Not yet implemented")
    }

    sealed class Event : BaseEvent {
        object GetSkills : Event()
        data class MoveToFirstPosition(val item: SkillDomainRW) : Event()
        data class EditItem(val item: SkillDomainRW) : Event()
        data class DeleteItem(val item: SkillDomainRW) : Event()
        data class AddItem(val item: SkillDomainRW) : Event()
    }

    sealed class Effect : BaseEffect {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.GetSkills -> {

            }

            is Event.MoveToFirstPosition -> {
                moveToFirstPosition(event.item)
            }

            is Event.AddItem -> {
                addSkill(event.item)
            }

            is Event.DeleteItem -> {
                deleteSkill(event.item)
                effect(Effect.Vibration)
            }

            is Event.EditItem -> {
                updateSkillOrAdd(event.item, SkillsChangeConst.FullSkill)
            }
        }
    }

    fun moveToFirstPosition(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    val newSkills = uiState.first().items!!.toMutableList()
                    newSkills.remove(skillDomainRW)
                    newSkills.add(0, skillDomainRW)
                }
            )
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        skillDomainRW.chosen = true
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    val newList = uiState.value.items!!.toMutableList()
                    newList.filter { it.chosen }.forEach {
                        repository.deleteSkill(it)
                        newList.remove(it)
                    }
                }
            )
        }
    }


    private fun addSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    val newSkills = uiState.first().items!!.toMutableList()
                    newSkills.add(skillDomainRW)

                    repository.addSkill(skillDomainRW)
                },
                onFailure = {

                },
                onEmpty = {
                    repository.addSkill(skillDomainRW)

                },
                onProcessing = {
                }
            )
        }
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
                    funWithState(
                        onSuccess = {
                            val newSkills = uiState.first().items!!.toMutableList()
                            newSkills.indexOf(skillDomainRW).let {
                                newSkills[it] = newSkills[it].copy(chosen = !newSkills[it].chosen)
                            }
                        }
                    )
                }

                SkillsChangeConst.FullSkill -> {
                    funWithState(
                        onSuccess = {
                            val newList =
                                uiState.value.items!!.toMutableList()
                            val skillToChange =
                                newList.find { it.skill.uuid == skillDomainRW.skill.uuid }
                            if (skillToChange == null) {
                                newList.add(skillDomainRW)
                                repository.addSkill(skill = skillDomainRW)
                            } else {
                                newList.indexOf(skillToChange).let {
                                    repository.updateSkill(skill = skillDomainRW)
                                    newList[it] =
                                        newList[it].copy(skill = skillDomainRW.skill)
                                }
                            }
                        },
                        onEmpty = {
                            repository.addSkill(skill = skillDomainRW)
                        },
                        onFailure = {
                            repository.addSkill(skill = skillDomainRW)
                        }
                    )
                }
            }
        }
    }

    private fun funWithState(
        onSuccess: suspend () -> Unit = {},
        onFailure: suspend () -> Unit = {},
        onEmpty: suspend () -> Unit = {},
        onProcessing: suspend () -> Unit = {},
        elseAction: suspend () -> Unit = {},
    ) {
        viewModelScope.launch { elseAction() }

    }
}

sealed interface SkillsChangeConst {
    object ChooseOrNotSkill : SkillsChangeConst
    object FullSkill : SkillsChangeConst
}