package com.hfad.palamarchuksuperapp.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.domain.models.Skill
import com.hfad.palamarchuksuperapp.domain.repository.SkillRepository
import com.hfad.palamarchuksuperapp.presentation.common.SkillDomainRW
import com.hfad.palamarchuksuperapp.presentation.common.SkillToSkillDomain
import dagger.MapKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.internal.Provider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

class SkillsViewModel (private val repository: SkillRepository) : ViewModel() {

    class Factory (
        private val repository: SkillRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SkillsViewModel::class.java)
            return SkillsViewModel(repository) as T
        }
    }

    private val _state = MutableStateFlow(SkillViewState())
    val state: StateFlow<SkillViewState> = _state.asStateFlow()


    fun getSkill(): List<Skill> {
        return _state.value.skills.map { it.skill }
    }

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
                viewModelScope.launch {
                    fetchSkills()
                }
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

    fun changeVisible(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _state.update { myState ->
                val newList = myState.skills.toMutableList()
                newList.indexOf(skillDomainRW).let {
                    newList[it] = newList[it].copy(isVisible = !newList[it].isVisible)
                }
                myState.copy(skills = newList)
            }
        }
    }

    suspend fun fetchSkills() {

        _state.update { it.copy(loading = true) }

        try {
            val skills = repository.getSkillsFromDB()
            delay(1000)
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
                        val skillToChange = newList.find { it.skill.id == skillDomainRW.skill.id }
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