package com.hfad.palamarchuksuperapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class SkillsViewModel @Inject constructor() : ViewModel() {

    private val one =
        Skill(name = "Compose", description = "One, Two, Three", id = UUID.randomUUID())
    private val two = Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID())
    private val three = Skill(
        name = "Recycler View",
        description = "Paging, DiffUtil, Adapter",
        id = UUID.randomUUID()
    )
    private val four =
        Skill(name = "Retrofit", description = "One, Two, Three", id = UUID.randomUUID())
    private val five =
        Skill(name = "Async", description = "One, Two, Three", id = UUID.randomUUID())
    private val six = Skill(
        name = "Dependency injections",
        description = "Injections in code, injections in constructor, injections via developer class. " +
                "\nPlus libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
        id = UUID.randomUUID()
    )
    private val seven =
        Skill(name = "RxJava", description = "One, Two, Three", id = UUID.randomUUID())
    private val eight =
        Skill(name = "Recycler View", description = "One, Two, Three", id = UUID.randomUUID())
    private val nine =
        Skill(name = "Recycler View", description = "One, Two, Three", id = UUID.randomUUID())
    private val ten = Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID())


    private val dataListNewFlow = MutableStateFlow<MutableList<Skill>>(mutableListOf(one, two, three, four, five, six, seven, eight, nine, ten))
    val date: StateFlow<List<Skill>> = this.dataListNewFlow.asStateFlow()

    fun moveSkillToFirstPosition (skill: Skill) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.remove(skill)
            newListNew.add(0, skill)
            dataListNewFlow.value = newListNew

        }
    }

    fun deleteItem(index: Int) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.removeAt(index)
            dataListNewFlow.value = newListNew
        }
    }
}