package ru.tinkoff.fintech.service.notification

class CardNumberMaskerImpl : CardNumberMasker {

    override fun mask(cardNumber: String, maskChar: Char, start: Int, end: Int): String {
        if (cardNumber.isEmpty()) {
            return cardNumber
        }

        val maskSize = if (cardNumber.length <= end - 1) cardNumber.length else end

        return cardNumber.replaceRange(start, maskSize, buildString {
            for (s in 1..maskSize - start) {
                append(maskChar)
            }
        })
    }
}
