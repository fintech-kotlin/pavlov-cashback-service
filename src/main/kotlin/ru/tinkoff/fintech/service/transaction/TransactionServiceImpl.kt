package ru.tinkoff.fintech.service.transaction

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService

@Component
class TransactionServiceImpl(
    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val notificationService: NotificationService,
    private val cashbackCalculator: CashbackCalculator,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository,

    @Value("\${processing.sign}")
    private val serviceSign: String
) : TransactionService {
    override fun handle(transaction: Transaction) {
        with(transaction) {
            println(transaction)
            println(cardServiceClient.getCard(cardNumber))
        }

    }
}