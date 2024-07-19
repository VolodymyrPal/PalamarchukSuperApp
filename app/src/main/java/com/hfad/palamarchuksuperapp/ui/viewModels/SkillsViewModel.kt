package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.ui.common.toDomainRW
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(private val repository: SkillRepository) :
    GenericViewModel<List<SkillDomainRW>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

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
                fetchSkills()
            }

            is Event.MoveToFirstPosition -> {
                moveToFirstPosition(event.item)
            }

            is Event.AddItem -> {
                addSkill(event.item)
            }

            is Event.DeleteItem -> {
                deleteSkill(event.item)
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
                    val newSkills = (uiState.first() as State.Success).data.toMutableList()
                    newSkills.remove(skillDomainRW)
                    newSkills.add(0, skillDomainRW)
                    emitState(newSkills)
                }
            )
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        skillDomainRW.chosen = true
        viewModelScope.launch {
            try {
                funWithState(
                    onSuccess = {
                        val newList = (uiState.value as State.Success).data.toMutableList()
                        newList.filter { it.chosen }.forEach {
                            repository.deleteSkill(it)
                            newList.remove(it)
                        }
                        emitState(newList)
                    }
                )
            } catch (e: Exception) {
                emitFailure(e)
            }
        }
    }

    private fun addSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    val newSkills = (uiState.first() as State.Success).data.toMutableList()
                    newSkills.add(skillDomainRW)
                    emitState(newSkills)
                    repository.addSkill(skillDomainRW)
                },
                onFailure = {

                },
                onEmpty = {
                    repository.addSkill(skillDomainRW)
                    emitState(uiState.value)
                },
                onProcessing = {
                }
            )
        }
    }

    fun fetchSkills() {
//            if (uiState.value is RepoResult.Empty) {
//                emitState(RepoResult.Success(data = repository.getSkillsFromDB().first().map {
//                    SkillToSkillDomain.map(it)
//                }))              FOR TESTING ONLY
        funWithState(
            onEmpty = {
                emitState(emitProcessing = true) { current ->
                    if (current is State.Error) {
                        return@emitState current
                    }
                    try {
                        val skills = repository.getSkillsFromDB().map { skills ->
                            skills.map { it.toDomainRW() }
                        }
                        delay(1000)
                        return@emitState if (skills.first().isNotEmpty()) {
                            State.Success(
                                data = skills.first()
                            )
                        } else State.Empty
                    } catch (e: Exception) {
                        State.Error(e)
                    }
                }
            }
        )
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
                    funWithState(
                        onSuccess = {
                            val newSkills = (uiState.first() as State.Success).data.toMutableList()
                            newSkills.indexOf(skillDomainRW).let {
                                newSkills[it] = newSkills[it].copy(chosen = !newSkills[it].chosen)
                                emitState(newSkills)
                            }
                        }
                    )
                }

                SkillsChangeConst.FullSkill -> {
                    funWithState(
                        onSuccess = {
                            val newList =
                                (uiState.value as State.Success).data.toMutableList()
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
                            emitState(newList)
                        },
                        onEmpty = {
                            repository.addSkill(skill = skillDomainRW)
                            emitState(uiState.value)
                        },
                        onFailure = {
                            repository.addSkill(skill = skillDomainRW)
                            emitState(uiState.value)
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
        when (uiState.value) {
            is State.Success -> viewModelScope.launch { onSuccess() }
            is State.Error -> viewModelScope.launch { onFailure() }
            is State.Empty -> viewModelScope.launch { onEmpty() }
            is State.Processing -> viewModelScope.launch { onProcessing() }
            else -> {
                viewModelScope.launch { elseAction() }
            }
        }
    }
}

sealed interface SkillsChangeConst {
    object ChooseOrNotSkill : SkillsChangeConst
    object FullSkill : SkillsChangeConst
}