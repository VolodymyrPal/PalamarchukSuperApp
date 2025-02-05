package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.AppError
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.domain.models.Skill
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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher

class SkillsViewModel @Inject constructor(
    private val repository: SkillRepository,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GenericViewModel<PersistentList<Skill>, SkillsViewModel.Event, SkillsViewModel.Effect>() {

//    data class SkillStateExpanded(
//        val stateInfo: String = "Volodymyr",
//        val screen: ScreenState? = null,
//    )
//
//    sealed interface ScreenState {
//        data object Loading : ScreenState
//        data class Error(val error: AppError) : ScreenState
//        data class Content(
//            val items: PersistentList<SkillDomainRW>,
//            val contentState: ExpandedContentState,
//        ) : ScreenState
//    }
//
//    sealed interface ExpandedContentState {
//        data object Loading : ExpandedContentState
//        data object Paginating : ExpandedContentState
//        data object Error : ExpandedContentState
//    }
    /** Example of implementation of State to share stable data between different states */


    data class SkillState(
        val items: PersistentList<Skill> = persistentListOf(),
        val loading: Boolean = false,
        val error: AppError? = null,
    ) : State<PersistentList<Skill>>

    override val _dataFlow: Flow<Result<PersistentList<Skill>, AppError>> =
        repository.getSkillsFromDB().map { Result.Success(it) }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)

    override val uiState: StateFlow<SkillState> =
        combine(_dataFlow, _errorFlow, _loading) { data, error, loading ->
            when (data) {
                is Result.Error -> {
                    SkillState(
                        items = data.data ?: persistentListOf(),
                        error = data.error,
                        loading = loading
                    )
                }

                is Result.Success -> {
                    SkillState(
                        items = data.data,
                        loading = loading
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SkillState(loading = true)
        )


    sealed class Event : BaseEvent {
        object GetSkills : Event()
        data class MoveToFirstPosition(val item: Skill) : Event()
        data class EditItem(val item: Skill, val skillsChangeConst: SkillsChangeConst) :
            Event()

        data class DeleteItem(val item: Skill) : Event()
        object DeleteAllChosen : Event()
        data class AddItem(val item: Skill) : Event()
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

    fun moveToFirstPosition(skill: Skill) {
        viewModelScope.launch(mainDispatcher) {
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

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteSkill(skill)
        }
    }

    fun deleteAllChosenSkill() {
//        viewModelScope.launch (ioDispatcher) {
//            _dataFlow.first().filter { it.chosen }.forEach {
//                repository.deleteSkill(it)
//            }
//        }
    }


    private fun addSkill(skill: Skill) {
        viewModelScope.launch(mainDispatcher) {
            repository.addSkill(skill)
        }
    }

    fun updateSkillOrAdd(skill: Skill, changeConst: SkillsChangeConst) {
        viewModelScope.launch(mainDispatcher) {
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