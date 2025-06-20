package com.hfad.palamarchuksuperapp.ui.viewModels

import IoDispatcher
import MainDispatcher
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.core.domain.AppError
import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEffect
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.BaseEvent
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.GenericViewModel
import com.hfad.palamarchuksuperapp.core.ui.genericViewModel.ScreenState
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(
    private val repository: SkillRepository,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : GenericViewModel<ScreenState, SkillsViewModel.Event, SkillsViewModel.Effect>() {

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
    ) : ScreenState

    override val _dataFlow: Flow<AppResult<PersistentList<Skill>, AppError>> =
        repository.getSkillsFromDB().map { AppResult.Success(it) }

    override val _errorFlow: MutableStateFlow<AppError?> = MutableStateFlow(null)

    override val uiState: StateFlow<SkillState> =
        combine(_dataFlow, _errorFlow, _loading) { data, error, loading ->
            when (data) {
                is AppResult.Error -> {
                    SkillState(
                        items = data.data ?: persistentListOf(),
                        error = data.error,
                        loading = loading
                    )
                }

                is AppResult.Success -> {
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
                updateSkillOrAdd(event.item, event.skillsChangeConst)
            }

            Event.DeleteAllChosen -> {
                deleteAllChosenSkill()
            }
        }
    }

    fun moveToFirstPosition(skill: Skill) {
        viewModelScope.launch(mainDispatcher) {
            repository.moveToFirstPosition(skill)
        }
    }

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteSkill(skill)
        }
    }

    fun deleteAllChosenSkill() {
        viewModelScope.launch(ioDispatcher) {
            _dataFlow.take(1).collect {
                when (it) {
                    is AppResult.Success -> {
                        (it as AppResult.Success).data.filter { it.chosen }
                            .forEach { repository.deleteSkill(it) }
                    }

                    else -> {}
                }
            }
        }
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
                    repository.updateSkill(
                        skill.copy(chosen = !skill.chosen)
                    )
                }

                SkillsChangeConst.FullSkill -> {
                    val id = skill.skillEntity.uuid
                    if (uiState.value.items.find { it.skillEntity.uuid == id } == null) {
                        repository.addSkill(
                            skill.copy(
                                skillEntity = skill.skillEntity.copy(
                                    name = skill.skillEntity.name,
                                    description = skill.skillEntity.description,
                                    date = skill.skillEntity.date,
                                    position = uiState.value.items.last().skillEntity.position + 1
                                )
                            )
                        )
                    } else {
                        repository.updateSkill(skill)
                    }
                }
            }
        }
    }
}

sealed interface SkillsChangeConst {
    object ChooseOrNotSkill : SkillsChangeConst
    object FullSkill : SkillsChangeConst
}