package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo

import java.time.LocalDateTime

import java.time.format.TextStyle

import java.util.*

internal const val LOYALTY_PROGRAM_BLACK = "BLACK"
internal const val LOYALTY_PROGRAM_ALL = "ALL"
internal const val LOYALTY_PROGRAM_BEER = "BEER"
internal const val MAX_CASH_BACK = 3000.0
internal const val MCC_SOFTWARE = 5734
internal const val MCC_BEER = 5921

class CashbackCalculatorImpl : CashbackCalculator {


    override fun calculateCashback(transactionInfo: TransactionInfo): Double {

        programmBlack(transactionInfo)
        additionalCashBack(transactionInfo)
        programmAll(transactionInfo)
        programmBeer(transactionInfo)

        if (transactionInfo.cashbackTotalValue > MAX_CASH_BACK) {
            return MAX_CASH_BACK;
        }
        return transactionInfo.cashbackTotalValue

    }

    private fun programmBlack(transactionInfo: TransactionInfo): Unit {
        if (transactionInfo.loyaltyProgramName.equals(LOYALTY_PROGRAM_BLACK)) {   //● Если программа лояльности "BLACK", начислить 1% кэшбека
            transactionInfo.cashbackTotalValue.plus(
                transactionInfo.transactionSum.plus(
                    transactionInfo.transactionSum.times(
                        1.01
                    )
                )
            )
        }
    }

    private fun programmAll(transactionInfo: TransactionInfo): Unit {
        if (transactionInfo.mccCode!!.equals(MCC_SOFTWARE) && transactionInfo.loyaltyProgramName.equals(
                LOYALTY_PROGRAM_ALL
            ) && chekPalindrom(
                transactionInfo.transactionSum
            )
        ) {
            transactionInfo.cashbackTotalValue.plus(1 + (transactionInfo.firstName.length + transactionInfo.lastName.length) / 1000)
        }
    }

    private fun programmBeer(transactionInfo: TransactionInfo): Unit {
        if (transactionInfo.loyaltyProgramName.equals(LOYALTY_PROGRAM_BEER) && transactionInfo.mccCode!!.equals(MCC_BEER)) {

            if (transactionInfo.firstName.equals("Олег")) {
                if (transactionInfo.lastName.equals("Олегов")) {
                    transactionInfo.cashbackTotalValue.plus(transactionInfo.transactionSum * 1.1)
                } else {
                    transactionInfo.cashbackTotalValue.plus(transactionInfo.transactionSum * 1.07)
                }
                return
            }

            if (LocalDateTime.now().month.getDisplayName(TextStyle.SHORT, Locale("ru")).toLowerCase().first().equals(
                    transactionInfo.firstName.toLowerCase().first()
                )
            ) {
                transactionInfo.cashbackTotalValue.plus(transactionInfo.transactionSum * 1.05)
                return
            }

            if (LocalDateTime.now().plusMonths(1).month.getDisplayName(
                    TextStyle.SHORT,
                    Locale("ru")
                ).toLowerCase().first().equals(transactionInfo.firstName.toLowerCase().first())
                || LocalDateTime.now().minusMonths(1).month.getDisplayName(
                    TextStyle.SHORT,
                    Locale("ru")
                ).toLowerCase().first().equals(transactionInfo.firstName.toLowerCase().first())
            ) {
                transactionInfo.cashbackTotalValue.plus(transactionInfo.transactionSum * 1.3)
                return
            }

            transactionInfo.cashbackTotalValue.plus(transactionInfo.transactionSum * 1.2)

        }
    }

    private fun additionalCashBack(transactionInfo: TransactionInfo): Unit {
        if (transactionInfo.cashbackTotalValue.rem(666).equals(0)) {
            transactionInfo.cashbackTotalValue.plus(6.66)
        }
    }

    private fun chekPalindrom(double: Double): Boolean {
        val st = double.toString().filter { char -> !char.equals('.') }
        var t = 0
        val l = st.length - 1
        for (s in 0..l / 2) {
            if (!s.equals(st.get(l))) {
                t++
            }
        }
        return t <= 1
    }
}

//    val loyaltyProgramName: String,
//    val transactionSum: Double,
//    val cashbackTotalValue: Double,
//    val mccCode: Int? = null,
//    val clientBirthDate: LocalDate? = null,
//    val firstName: String,
//    val lastName: String,
//    val middleName: String? = null

//Метод должен реализовывать следующие правила*:


//● Если программа лояльности "BLACK", начислить 1% кэшбекаBEER"BLACK", начислить 1% кэшбека и mcc код = 5921 и
//a. Ваше имя "BLACK", начислить 1% кэшбекаОлег"BLACK", начислить 1% кэшбека (или без учета регистра) и фамилия "BLACK", начислить 1% кэшбекаОлегов"BLACK", начислить 1% кэшбека (или без учета регистра) то 10% кешбека
//b. Ваше имя "BLACK", начислить 1% кэшбекаОлег"BLACK", начислить 1% кэшбека (или без учета регистра) то начислить 7% кешбека
//c. Первая буква текущего месяца (или на русском, без учета регистра) равна первой букве firstName в
//транзакции (или на русском, без учета регистра) то 5% кешбека
//b. Первая буква прошлого или следующего месяца (или на русском, без учета регистра) равна первой
//букве firstName в транзакции (или на русском, без учета регистра), то 3% кешбека
//d. иначе 2% кешбека.
//* * Для всех вышеперечисленных условий установить максимальный размер суммарного вознаграждения за
//текущий период равным 3000. Вознаграждения по предыдущим транзакциям за текущий период хранятся в
//переменной transactionInfo.cashbackTotalValue.