package ru.tinkoff.fintech.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Component
class NotificationServiceClientImpl(
    @Value("\${processing.services.uri.notification}") private val uri: String
) : NotificationServiceClient{

    override fun sendNotification(clientId: String, message: String) {
        val headers = HttpHeaders()
        HttpHeaders().contentType = MediaType.APPLICATION_JSON_UTF8
        val builder = RestTemplateBuilder()
        builder.setConnectTimeout(Duration.ofMillis(10000L))
        RestTemplate().postForEntity("$uri/$clientId/message", HttpEntity(message, headers), String::class.java) ?: throw RestClientException("error")
    }
}