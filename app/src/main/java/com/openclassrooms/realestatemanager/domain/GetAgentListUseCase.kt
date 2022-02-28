package com.openclassrooms.realestatemanager.domain

import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAgentListUseCase @Inject constructor(
    private val agentRepository: AgentRepository,
) {

    operator fun invoke(): Flow<List<AgentEntity>> = agentRepository.getAgentListFlow()
}