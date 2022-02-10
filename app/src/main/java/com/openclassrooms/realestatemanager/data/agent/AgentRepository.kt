package com.openclassrooms.realestatemanager.data.agent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor() {

    private val agentListMutableLiveData = MutableLiveData(listOf(
        AgentEntity("1", "Agent K"),
        AgentEntity("2", "Agent J"),
        AgentEntity("3", "Agent Z"),
        AgentEntity("4", "Agent L"),
    ))

    fun getAgentList(): LiveData<List<AgentEntity>> = agentListMutableLiveData
}