package ru.tinkoff.fintech.service.transaction

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.tinkoff.fintech.client.CardServiceClient
import ru.tinkoff.fintech.client.ClientService
import ru.tinkoff.fintech.client.LoyaltyServiceClient
import ru.tinkoff.fintech.db.entity.LoyaltyPaymentEntity
import ru.tinkoff.fintech.db.repository.LoyaltyPaymentRepository
import ru.tinkoff.fintech.model.NotificationMessageInfo
import ru.tinkoff.fintech.model.Transaction
import ru.tinkoff.fintech.model.TransactionInfo
import ru.tinkoff.fintech.service.cashback.CashbackCalculator
import ru.tinkoff.fintech.service.notification.NotificationService
import java.time.LocalDate

@Component
class TransactionServiceImpl(

    @Value("\${processing.sign}")
    private val serviceSign: String,

    private val cardServiceClient: CardServiceClient,
    private val clientService: ClientService,
    private val loyaltyServiceClient: LoyaltyServiceClient,
    private val notificationService: NotificationService,
    private val cashbackCalculator: CashbackCalculator,
    private val loyaltyPaymentRepository: LoyaltyPaymentRepository

) : TransactionService {
    override fun handle(transaction: Transaction) {

        with(transaction) {
            if (mccCode != null) {
                val startOfMonth = LocalDate.from(transaction.time).withDayOfMonth(1).atStartOfDay()
                val card = cardServiceClient.getCard(cardNumber)

                val client = clientService.getClient(card.client)
                val loyaltyProgram = loyaltyServiceClient.getLoyaltyProgram(card.loyaltyProgram)
                val cashbacks =
                    loyaltyPaymentRepository.findByCardIdAndSignAndDateTimeAfter(card.id, serviceSign, startOfMonth)
                val loyaltyPaymentSum = cashbacks.stream().mapToDouble(LoyaltyPaymentEntity::value).sum()
                val transactionInfo = TransactionInfo(
                    loyaltyProgram.name,
                    value,
                    loyaltyPaymentSum,
                    mccCode,
                    client.birthDate,
                    client.firstName,
                    client.lastName,
                    client.middleName
                )
                val cashbackTotal = cashbackCalculator.calculateCashback(transactionInfo)

                loyaltyPaymentRepository.save(
                    LoyaltyPaymentEntity(
                        value = cashbackTotal,
                        cardId = card.id,
                        sign = serviceSign,
                        transactionId = transactionId,
                        dateTime = time
                    )
                )

                val notificationMessageInfo = NotificationMessageInfo(
                    transactionInfo.firstName,
                    card.cardNumber,
                    cashbackTotal,
                    transactionInfo.transactionSum,
                    transactionInfo.loyaltyProgramName,
                    time
                )
                notificationService.sendNotification(client.id, notificationMessageInfo)


            }

        }

    }
}