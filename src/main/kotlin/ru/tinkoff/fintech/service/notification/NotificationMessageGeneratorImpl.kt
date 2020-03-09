package ru.tinkoff.fintech.service.notification

import ru.tinkoff.fintech.model.NotificationMessageInfo

class NotificationMessageGeneratorImpl(
    private val cardNumberMasker: CardNumberMasker
) : NotificationMessageGenerator {

    override fun generateMessage(notificationMessageInfo: NotificationMessageInfo): String {
        return with(notificationMessageInfo) {
            """
            |Уважаемый, ${name}!
            |Спешим Вам сообщить, что на карту ${cardNumberMasker.mask(notificationMessageInfo.cardNumber)}
            |начислен cashback в размере ${cashback}
            |за категорию ${category}.
            |Спасибо за покупку ${transactionDate}
            """.trimMargin()
        }
    }
}