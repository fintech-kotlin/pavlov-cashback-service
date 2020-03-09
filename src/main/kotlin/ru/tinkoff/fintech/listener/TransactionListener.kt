package ru.tinkoff.fintech.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TransactionListener {


    @KafkaListener(topics = ["\${app.topic.example}"])
    fun onMessage(message: String) {
        println(message)
    }
}


