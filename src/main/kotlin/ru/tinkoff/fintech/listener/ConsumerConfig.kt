package ru.tinkoff.fintech.listener

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import ru.tinkoff.fintech.model.Transaction
import java.util.*


@EnableKafka
@Configuration
class ConsumerConfig {

    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String = ""

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Transaction> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, Transaction>()
        factory.consumerFactory = kafkaConsumerFactory()
        return factory
    }

    @Bean
    fun kafkaConsumerConfigs(): Map<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "tinkoff"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        return props
    }



    @Bean
    fun kafkaConsumerFactory(): ConsumerFactory<String, Transaction> {
        val jsonDeserializer = JsonDeserializer(Transaction::class.java, getObjectMapper(),false)
        jsonDeserializer.addTrustedPackages("ru.tinkoff.bpm.kotlincoursepaymentprocessing.kafka")
        return DefaultKafkaConsumerFactory<String, Transaction>(kafkaConsumerConfigs(), StringDeserializer(), jsonDeserializer)
    }


    @Bean
    fun getObjectMapper(): ObjectMapper {
        return ObjectMapper().registerModules(KotlinModule(), JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setDateFormat(ISO8601DateFormat()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}