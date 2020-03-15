package ru.tinkoff.fintech.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.model.Transaction

@Component
class TransactionListener {

    @KafkaListener(topics = ["\${app.topic.transaction}"])
    fun onMessage(message: Transaction) {
        println(message)
    }
}


