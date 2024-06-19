package com.hfad.palamarchuksuperapp.presentation.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.hfad.palamarchuksuperapp.data.entities.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.SkillToSkillDomain
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SkillsViewModel @Inject constructor(private val repository: SkillRepository) : UiStateViewModel<List<SkillDomainRW>>() {

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.EditItem -> {
                updateSkillOrAdd(event.item, SkillsChangeConst.FullSkill)
            }
            is UiEvent.DeleteItem -> {
                deleteSkill(event.item)
            }
            is UiEvent.MoveItemUp -> {
                moveToFirstPosition(event.item)
            }
            is UiEvent.AddItem -> {
                addSkill(skill = event.item.skill)
            }
            is UiEvent.GetSkills -> {
                fetchSkills()
            }
        }
    }

    fun moveToFirstPosition(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            val newList = (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.toMutableList()
            newList.remove(skillDomainRW)
            newList.add(0, skillDomainRW)
            emitState(newList)
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
            is RepoResult.Success -> viewModelScope.launch { onSuccess() }
            is RepoResult.Failure -> viewModelScope.launch { onFailure() }
            is RepoResult.Empty -> viewModelScope.launch { onEmpty() }
            is RepoResult.Processing -> viewModelScope.launch { onProcessing() }
            else -> {
                viewModelScope.launch { elseAction() }
            }
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            funWithState(
                onSuccess = {
                    val index = (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.indexOf(
                            skillDomainRW)
                    val newList = (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.toMutableList()
                    if (index != -1) { newList.remove(skillDomainRW) }
                    newList.removeIf { it.chosen }
                    emitState(newList)
                }
            )
        }
    }

    private fun addSkill(skill: Skill) {
        viewModelScope.launch {
            funWithState (onSuccess =  {
                val newList =
                    (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.toMutableList()
                newList.add(SkillDomainRW(skill))
                emitState(newList)
            })
        }
    }

    fun fetchSkills() {
        funWithState(
            onEmpty = {
                emitState(emitProcessing = true) { current ->
                    delay(4000)
                    if (current is RepoResult.Failure) {
                        return@emitState current
                    }
                    try {
                        val skills = repository.getSkillsFromDB()
                        return@emitState if (skills.isNotEmpty()) {
                            RepoResult.Success(data = skills.map { SkillToSkillDomain.map(it) })
                        } else RepoResult.Empty
                    } catch (e: Exception) {
                        RepoResult.Failure(e)
                    }
                }
            }
        )
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
                    funWithState(onSuccess = {
                        val newList =
                            (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.toMutableList()
                        newList.indexOf(skillDomainRW).let {
                            newList[it] = newList[it].copy(chosen = !skillDomainRW.chosen)
                        }
                        emitState(newList)
                    })
                }

                SkillsChangeConst.FullSkill -> {
                    funWithState( onSuccess = {
                        val newList =
                            (uiState.value as RepoResult.Success<List<SkillDomainRW>>).data.toMutableList()
                        val skillToChange =
                            newList.find { it.skill.uuid == skillDomainRW.skill.uuid }
                        if (skillToChange == null) {
                            newList.add(skillDomainRW)
                        } else {
                            newList.indexOf(skillToChange).let {
                                newList[it] =
                                    newList[it].copy(skill = skillDomainRW.skill)
                            }
                        }
                        emitState(newList)
                    })
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

sealed class RepoResult<out T> {
    data object Processing : RepoResult<Nothing>()

    data class Success<out T>(
        val data: T,
    ) : RepoResult<T>()

    data object Empty : RepoResult<Nothing>()

    data class Failure(
        val error: Throwable,
    ) : RepoResult<Nothing>()
}

abstract class UiStateViewModel<T> : ViewModel() {

    private val _uiState: MutableStateFlow<RepoResult<T>> =
        MutableStateFlow(RepoResult.Empty)

    val uiState: StateFlow<RepoResult<T>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RepoResult.Empty
        )

    protected fun emitState(
        emitProcessing: Boolean,
        block: suspend (current: RepoResult<T>) -> RepoResult<T>,
    ): Job =
        viewModelScope.launch {
            val current = _uiState.value
            if (emitProcessing) {
                emitProcessing()
            }
            _uiState.update { block(current) }
        }

    protected fun emitState(value: RepoResult<T>) {
        _uiState.update { value }
    }

    protected suspend fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.update { RepoResult.Success(value) }
        }
    }

    private fun emitEmpty() {
        _uiState.update { RepoResult.Empty }
    }

    private fun emitProcessing() {
        _uiState.update { RepoResult.Processing }
    }

    protected fun emitFailure(e: Throwable) {
        _uiState.update { RepoResult.Failure(e) }
    }
}

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(factory: ViewModelProvider.Factory): VM {
    val owner = LocalViewModelStoreOwner.current?: error(
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner")
    return ViewModelProvider(owner, factory)[VM::class.java]
}