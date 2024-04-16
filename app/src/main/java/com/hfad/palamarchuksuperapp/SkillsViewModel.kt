package com.hfad.palamarchuksuperapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.reflect.Constructor
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


    private var dataList = mutableListOf(one, two, three, four, five, six, seven, eight, nine, ten)

    val dataListFlow: MutableStateFlow<List<Skill>> = MutableStateFlow(dataList)

    fun updateDataListFlow(skill: Skill) {
        viewModelScope.launch {
            val newList = dataListFlow.value.toMutableList()
            newList.remove(skill)
            newList.add(0, skill)
            dataListFlow.value = newList
        }
    }

    fun deleteItem(index: Int) {
        viewModelScope.launch {
            val newList = dataListFlow.value.toMutableList()
            newList.removeAt(index)
            dataListFlow.value = newList
        }
    }
}