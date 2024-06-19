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
            _state.update { myState ->
                val newList = myState.skills.toMutableList()
                newList.remove(skillDomainRW)
                newList.add(0, skillDomainRW)
                myState.copy(skills = newList)
            }
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _state.update { myState ->
                val index = myState.skills.indexOf(skillDomainRW)
                val newList = myState.skills.toMutableList()
                if (index != -1) {
                    newList.remove(skillDomainRW)
                }
                newList.removeIf {
                    it.chosen
                }
                myState.copy(skills = newList)
            }
        }
    }

    fun addSkill(skill: Skill) {
        viewModelScope.launch {
            _state.update { myState ->
                val newList = myState.skills.toMutableList()
                newList.add(SkillDomainRW(skill))
                myState.copy(skills = newList)
            }
        }
    }

    suspend fun fetchSkills() {
        if (
            !(uiState.value is RepoResult.Empty || uiState.value is RepoResult.Processing)
//            state.value.skills.isEmpty()
            ) {
            _state.update { it.copy(loading = true) }
            try {
                val skills = repository.getSkillsFromDB()

                _state.update { state ->
                    emitState(RepoResult.Success(data = skills.map { SkillToSkillDomain.map(it) }))
                    state.copy(loading = false, skills = skills.map { SkillToSkillDomain.map(it) })
                }
            } catch (e: Exception) {
                _state.value =
                    SkillViewState(loading = false, error = e.message ?: "Error fetching skills")
                emitFailure(e)
            }
        }
    }

    fun updateSkillOrAdd(skillDomainRW: SkillDomainRW, changeConst: SkillsChangeConst) {
        viewModelScope.launch {
            when (changeConst) {
                SkillsChangeConst.ChooseOrNotSkill -> {
                    _state.update { myState ->
                        val newList = myState.skills.toMutableList()
                        newList.indexOf(skillDomainRW).let {
                            Log.d("Was called:", "$it")
                            newList[it] = newList[it].copy(chosen = !skillDomainRW.chosen)
                        }
                        myState.copy(skills = newList)
                    }

                }

                SkillsChangeConst.FullSkill -> {
                    _state.update { myState ->
                        val newList = myState.skills.toMutableList()
                        val skillToChange = newList.find { it.skill.uuid == skillDomainRW.skill.uuid }
                        if (skillToChange == null) {
                            newList.add(skillDomainRW)
                        } else {
                            newList.indexOf(skillToChange).let {
                                newList[it] =
                                    newList[it].copy(skill = skillDomainRW.skill)
                            }
                        }
                        myState.copy(skills = newList)
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
        MutableStateFlow(RepoResult.Processing)

    val uiState: StateFlow<RepoResult<T>> =
        _uiState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RepoResult.Processing
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
            _uiState.update { block.invoke(current) }
        }

    protected suspend fun emitState(value: RepoResult<T>) {
        _uiState.update { value }
    }

    protected suspend fun emitState(value: T?) {
        if (value == null) {
            emitEmpty()
        } else {
            _uiState.update { RepoResult.Success(value) }
        }
    }

    protected suspend fun emitEmpty() {
        _uiState.update { RepoResult.Empty }
    }

    protected suspend fun emitProcessing() {
        _uiState.update { RepoResult.Processing }
    }

    protected suspend fun emitFailure(e: Throwable) {
        _uiState.update { RepoResult.Failure(e) }
    }
}

@Composable
inline fun <reified VM : ViewModel> daggerViewModel(factory: ViewModelProvider.Factory): VM {
    val owner = LocalViewModelStoreOwner.current?: error(
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner")
    return ViewModelProvider(owner, factory)[VM::class.java]
}