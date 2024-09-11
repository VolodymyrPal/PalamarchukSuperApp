package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(
    private val repository: SkillRepository,
) :
    GenericViewModel<List<SkillDomainRW>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

    data class SkillState(
        val items: List<SkillDomainRW> = emptyList(),
        val loading: Boolean = false,
        val error: Throwable? = null,
    ) : State<List<SkillDomainRW>>

    override val _dataFlow: Flow<List<SkillDomainRW>> = repository.getSkillsFromDB()

    override val _errorFlow: MutableStateFlow<Exception?> = MutableStateFlow(null)

    override val uiState: StateFlow<SkillState> =
        combine(_dataFlow, _errorFlow, _loading) { data, error, loading ->
            SkillState(
                items = data,
                error = error,
                loading = loading
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SkillState(loading = true)
        )


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
            val newSkills = uiState.first().items.toMutableList()
            newSkills.remove(skillDomainRW)
            newSkills.add(0, skillDomainRW)
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        skillDomainRW.chosen = true
        viewModelScope.launch {
//            funWithState(
//                onSuccess = {
//                    val newList = uiState.value.items!!.toMutableList()
//                    newList.filter { it.chosen }.forEach {
//                        repository.deleteSkill(it)
//                        newList.remove(it)
//                    }
//                }
//            )
        }
    }


    private fun addSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
//            funWithState(
//                onSuccess = {
//                    val newSkills = uiState.first().items!!.toMutableList()
//                    newSkills.add(skillDomainRW)
//
//                    repository.addSkill(skillDomainRW)
//                },
//                onFailure = {
//
//                },
//                onEmpty = {
//                    repository.addSkill(skillDomainRW)
//
//                },
//                onProcessing = {
//                }
//            )
        }
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
//                    funWithState(
//                        onSuccess = {
//                            val newSkills = uiState.first().items!!.toMutableList()
//                            newSkills.indexOf(skillDomainRW).let {
//                                newSkills[it] = newSkills[it].copy(chosen = !newSkills[it].chosen)
//                            }
//                        }
//                    )
                }

                SkillsChangeConst.FullSkill -> {
//                    funWithState(
//                        onSuccess = {
//                            val newList =
//                                uiState.value.items.toMutableList()
//                            val skillToChange =
//                                newList.find { it.skill.uuid == skillDomainRW.skill.uuid }
//                            if (skillToChange == null) {
//                                newList.add(skillDomainRW)
//                                repository.addSkill(skill = skillDomainRW)
//                            } else {
//                                newList.indexOf(skillToChange).let {
//                                    repository.updateSkill(skill = skillDomainRW)
//                                    newList[it] =
//                                        newList[it].copy(skill = skillDomainRW.skill)
//                                }
//                            }
//                        },
//                        onEmpty = {
//                            repository.addSkill(skill = skillDomainRW)
//                        },
//                        onFailure = {
//                            repository.addSkill(skill = skillDomainRW)
//                        }
//                    )
                }
            }
        }
    }
}

sealed interface SkillsChangeConst {
    object ChooseOrNotSkill : SkillsChangeConst
    object FullSkill : SkillsChangeConst
}