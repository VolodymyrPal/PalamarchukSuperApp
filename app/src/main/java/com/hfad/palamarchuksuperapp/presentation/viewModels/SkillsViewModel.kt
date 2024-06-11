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

class SkillsViewModel @AssistedInject constructor(
    @Assisted val repository: SkillRepository) : ViewModel() {
    // @Inject lateinit var myRepository: SkillRepository

    @AssistedFactory
    interface Factory {
        fun create(repository: SkillRepository): SkillsViewModel
    }

    private val _state = MutableStateFlow(SkillViewState())
    val state: StateFlow<SkillViewState> = _state.asStateFlow()


    fun getSkill(): List<Skill> {
        return skillsList.value.map { it.skill }
    }

    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.EditItem -> {
                updateSkillOrAdd(event.item, SkillsChangeConst.FullSkill)
                Log.d("Skill: ", "Asked for editing ${skillsList.value.size}")
            }
            is UiEvent.DeleteItem -> {
                deleteSkill(event.item)
            }
            is UiEvent.MoveItemUp -> {
                moveToFirstPosition(event.item)
                Log.d("Skill: ", "Asked for movingUp ${skillsList.value.size}")
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
            val newListNew = skillsList.value.toMutableList()
            newListNew.remove(skillDomainRW)
            newListNew.add(0, skillDomainRW)
            _skillsList.value = newListNew
        }
    }

    fun deleteSkill(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _skillsList.update { recyclerSkillList ->
                val index = recyclerSkillList.indexOf(skillDomainRW)
                val newList = recyclerSkillList.toMutableList()
                if (index != -1) {
                    newList.remove(skillDomainRW)
                }
                newList.removeIf {
                    it.chosen
                }
                newList
            }
        }
    }

    fun addSkill(skill: Skill) {
        viewModelScope.launch {
            _skillsList.update { currentList ->
                val newList = currentList.toMutableList()
                newList.add(SkillDomainRW(skill))
                newList
            }
        }
    }

    fun changeVisible(skillDomainRW: SkillDomainRW) {
        viewModelScope.launch {
            _skillsList.update { currentList ->
                val newList = currentList.toMutableList()
                newList.indexOf(skillDomainRW).let {
                    newList[it] = newList[it].copy(isVisible = !newList[it].isVisible)
                }
                newList
            }
        }
    }

    suspend fun fetchSkills() {

        _state.update { it.copy(loading = true) }

        try {
            val skills = myRepository.getSkillsFromDB()
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
                    _skillsList.update {
                        val newList = it.toMutableList()
                        newList.indexOf(skillDomainRW).let {
                            Log.d("Was called:", "$it")
                            newList[it] = newList[it].copy(chosen = !skillDomainRW.chosen)
                        }
                        newList
                    }

                }

                SkillsChangeConst.FullSkill -> {
                    _skillsList.update { myListFlow ->
                        val newList = myListFlow.toMutableList()
                        val skillToChange = newList.find { it.skill.id == skillDomainRW.skill.id }
                        if (skillToChange == null) {
                            newList.add(skillDomainRW)
                        } else {
                            newList.indexOf(skillToChange).let {
                                newList[it] =
                                    newList[it].copy(skill = skillDomainRW.skill)
                            }
                        }
                        newList
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

class DaggerViewModelFactory
@Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?:
        creators.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
        ?: throw IllegalArgumentException("unknown model class " + modelClass)

        return try {
            creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
