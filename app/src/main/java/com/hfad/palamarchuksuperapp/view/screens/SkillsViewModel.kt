package com.hfad.palamarchuksuperapp.view.screens

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.palamarchuksuperapp.data.Skill
import com.hfad.palamarchuksuperapp.data.SkillsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class SkillsViewModel : ViewModel(), SkillsDataSource {

//    @Inject lateinit var myRepository: MyRepository

    private val one =
        Skill(name = "Compose", description = "One, Two, Three", id = UUID.randomUUID())
    private val two = Skill(name = "XML", description = "One, Two, Three", id = UUID.randomUUID())
    private val three = Skill(name = "Recycler View", description = "Paging, DiffUtil, Adapter", id = UUID.randomUUID() )
    private val four =
        Skill(name = "Retrofit", description = "One, Two, Three", id = UUID.randomUUID())
    private val five =
        Skill(name = "Async", description = "One, Two, Three", id = UUID.randomUUID())
    private val six = Skill(
        name = "Dependency injections",
        description = "Multi-module architecture, Component Dependencies, Multibindings, Components, Scope, Injections" +
                "\n Libraries: Dagger 2, Hilt, ---Kodein, ---Koin",
        id = UUID.randomUUID()
    )
    private val seven =
        Skill(name = "RxJava", description = "One, Two, Three", id = UUID.randomUUID())
    private val eight =
        Skill(name = "To learn", description = "Firebase!!!, GITFLOW, GIT, SOLID, MVVM, MVP, REST API!!!, JSON!, PUSH NOTIFICATIONS!!!, ", id = UUID.randomUUID())
    private val nine =
        Skill(name = "Recycler View", description = "One, Two, Three", id = UUID.randomUUID())
    private val ten = Skill(name = "Project Pattern", description = "MVVM", id = UUID.randomUUID())
    private val eleven = Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID())
    private val twelve = Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID())

    private val thirteen = Skill(name = "Work with Image", description = "FileProvider,Coil", id = UUID.randomUUID())



    private val dataListNewFlow = MutableStateFlow<MutableList<Skill>>(mutableListOf(one, two, three, four, five, six, seven, eight, nine, ten, eleven, twelve, thirteen))
    val date: StateFlow<List<Skill>> = this.dataListNewFlow.asStateFlow()

    override fun getSkill(): List<Skill> {
        return date.value
    }
    override fun moveToFirstPosition (skill: Skill) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.remove(skill)
            newListNew.add(0, skill)
            dataListNewFlow.value = newListNew
        }
    }

    override fun deleteSkill (position: Int) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.removeAt(position)
            dataListNewFlow.value = newListNew
        }
    }

    override fun addSkill(skill: Skill) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.add(skill)
            dataListNewFlow.value = newListNew
        }
    }

    fun updateSkill(skill: Skill, skillName: String, skillDescription: String) {
        viewModelScope.launch {
            val newListNew = date.value.toMutableList()
            newListNew.indexOf(skill).let {
                newListNew[it] = Skill(name = skillName, description = skillDescription, id = skill.id)
            }
            dataListNewFlow.value = newListNew
        }
    }

}