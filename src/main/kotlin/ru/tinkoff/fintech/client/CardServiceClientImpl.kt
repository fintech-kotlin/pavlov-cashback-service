package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Card


@Component
class CardServiceClientImpl(
    @Value("\${processing.services.uri.card}") private val uri: String
) : CardServiceClient {

    override fun getCard(id: String): Card {
        return RestTemplate().getForObject("$uri/$id", Card::class.java) ?: throw RestClientException("error")
    }
}