package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.presentation.common.ProductDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(private val repository: SkillRepository) :
    GenericViewModel<Flow<List<SkillDomainRW>>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

    sealed class Event : BaseEvent() {
        object GetSkills : Event()
        data class moveToFirstPosition(val item: SkillDomainRW) : Event()
        data class EditItem(val item: SkillDomainRW) : Event()
        data class DeleteItem(val item: SkillDomainRW) : Event()
        data class AddItem(val item: SkillDomainRW) : Event()
    }

    sealed class Effect : BaseEffect() {
        object OnBackPressed : Effect()
        data class ShowToast(val message: String) : Effect()
        object Vibration : Effect()
    }

    override fun event(event: Event) {
        when (event) {
            is Event.GetSkills -> {
                fetchSkills()
            }

            is Event.moveToFirstPosition -> {
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
                    val currentSkills = (uiState.first() as State.Success).data.first()
                    currentSkills.forEach {
                        if (it == skillDomainRW) {
                            repository.updateSkill(
                                skill = skillDomainRW.copy(
                                    skill = skillDomainRW.skill.copy(id = 1)
                                )
                            )
                        } else {
                            repository.updateSkill(
                                skill = skillDomainRW.copy(
                                    skill = skillDomainRW.skill.copy(id = it.skill.id + 1)
                                )
                            )
                        }
                    }
                    emitState(uiState.value)
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
                        repository.deleteSkill(skillDomainRW)
                        emitState(uiState.value)
                    }
                )
            } catch (e: Exception) {
                emitFailure(e)
            }
        }
    }

    private fun addSkill(skill: SkillDomainRW) {
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    repository.addSkill(skill)
                    emitState(uiState.value)
                },
                onFailure = {

                },
                onEmpty = {
                    repository.addSkill(skill)
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
                        val skills = repository.getSkillsFromDB()
                        delay(1000)
                        return@emitState if (skills.first().isNotEmpty()) {
                            State.Success(
                                data = skills
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
                            repository.updateSkill(skillDomainRW.copy(chosen = !skillDomainRW.chosen))
                            emitState(uiState.value)
                        })
                }

                SkillsChangeConst.FullSkill -> {
                    funWithState(
                        onSuccess = {
                            repository.updateSkill(skill = skillDomainRW)
                            emitState(uiState.value)
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

sealed interface RepoResult<out T> {
    data object Processing : RepoResult<Nothing>

    data class Success<out T>(
        val data: T,
    ) : RepoResult<T>

    data object Empty : RepoResult<Nothing>

    data class Error(
        val exception: Throwable,
    ) : RepoResult<Nothing>
}