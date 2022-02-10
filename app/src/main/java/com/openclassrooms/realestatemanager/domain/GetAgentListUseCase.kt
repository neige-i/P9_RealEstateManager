package com.openclassrooms.realestatemanager.domain

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.agent.AgentEntity
import com.openclassrooms.realestatemanager.data.agent.AgentRepository
import javax.inject.Inject

class GetAgentListUseCase @Inject constructor(
    private val agentRepository: AgentRepository,
) {

    operator fun invoke(): LiveData<List<AgentEntity>> = agentRepository.getAgentList()
}