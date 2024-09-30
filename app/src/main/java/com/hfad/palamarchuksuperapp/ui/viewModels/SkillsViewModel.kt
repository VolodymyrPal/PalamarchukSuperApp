package com.hfad.palamarchuksuperapp.ui.viewModels

import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.DataError
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.ui.common.SkillDomainRW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.hfad.palamarchuksuperapp.domain.models.Result

class SkillsViewModel @Inject constructor(
    private val repository: SkillRepository,
) :
    GenericViewModel<List<SkillDomainRW>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

    data class SkillState(
        val items: List<SkillDomainRW> = emptyList(),
        val loading: Boolean = false,
        val error: DataError? = null,
    ) : State<List<SkillDomainRW>>

    override val _dataFlow: Flow<Result<List<SkillDomainRW>, DataError>> =
        repository.getSkillsFromDB().map { Result.Success(it) }

    override val _errorFlow: MutableStateFlow<DataError?> = MutableStateFlow(null)

    override val uiState: StateFlow<SkillState> =
        combine(_dataFlow, _errorFlow, _loading) { data, error, loading ->
            SkillState(
                items = (data as Result.Success).data,
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
        data class EditItem(val item: SkillDomainRW, val skillsChangeConst: SkillsChangeConst) :
            Event()

        data class DeleteItem(val item: SkillDomainRW) : Event()
        object DeleteAllChosen : Event()
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

            Event.DeleteAllChosen -> {
                deleteAllChosenSkill()
            }
        }
    }

    fun moveToFirstPosition(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
//            val minPosition = _dataFlow.first().minOfOrNull { it.skill.position } ?: 0
//            repository.updateSkill(
//                skillDomainRW.copy(
//                    skillDomainRW.skill.copy(
//                        position = minPosition - 1
//                    )
//                )
//            )
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            repository.deleteSkill(skillDomainRW)
        }
    }

    fun deleteAllChosenSkill() {
//        viewModelScope.launch {
//            _dataFlow.first().filter { it.chosen }.forEach {
//                repository.deleteSkill(it)
//            }
//        }
    }


    private fun addSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            repository.addSkill(skillDomainRW)
        }
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
//                            val newSkills = uiState.first().items!!.toMutableList()
//                            newSkills.indexOf(skillDomainRW).let {
//                                newSkills[it] = newSkills[it].copy(chosen = !newSkills[it].chosen)
//                            }

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