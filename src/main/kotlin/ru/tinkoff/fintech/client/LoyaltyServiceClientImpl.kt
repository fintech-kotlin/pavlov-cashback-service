package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.LoyaltyProgram
@Component
class LoyaltyServiceClientImpl(
    @Value("\${processing.services.uri.loyalty}") private val uri: String
) : LoyaltyServiceClient{

    override fun getLoyaltyProgram(id: String): LoyaltyProgram {
        return RestTemplate().getForObject("$uri/$id}", LoyaltyProgram::class.java) ?: throw RestClientException("error")
    }
}