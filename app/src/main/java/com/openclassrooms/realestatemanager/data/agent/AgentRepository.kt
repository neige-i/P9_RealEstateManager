package com.openclassrooms.realestatemanager.data.agent

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor() {

    private val agentList = listOf(
        AgentEntity("1", "Agent K"),
        AgentEntity("2", "Agent J"),
        AgentEntity("3", "Agent Z"),
        AgentEntity("4", "Agent L"),
    )

    fun getAgentListFlow(): Flow<List<AgentEntity>> = flowOf(agentList)

    fun getAgentByName(agentName: String): AgentEntity? = agentList.firstOrNull {
        agentName == it.name
    }
}