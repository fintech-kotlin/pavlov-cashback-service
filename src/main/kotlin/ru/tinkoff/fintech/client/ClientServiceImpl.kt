package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.model.Client

@Component
class ClientServiceImpl(
    @Value("\${processing.services.uri.client}") private val uri: String
) : ClientService{

    override fun getClient(id: String): Client {
        return RestTemplate().getForObject("$uri/$id}", Client::class.java) ?: throw RestClientException("error")
    }
}