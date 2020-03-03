package ru.tinkoff.fintech.service.cashback

import ru.tinkoff.fintech.model.TransactionInfo
import java.math.RoundingMode
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
        with(transactionInfo) {
            if (cashbackTotalValue >= MAX_CASH_BACK) {
                return 0.0
            }
            var cashback = 0.0

            cashback += programmBlack(this)
            cashback += additionalCashBack(this)
            cashback += programmAll(this)
            cashback += programmBeer(this)

            if (cashback + cashbackTotalValue > MAX_CASH_BACK) {
                return MAX_CASH_BACK - cashbackTotalValue
            }
            return cashback
        }
    }

    private fun programmBlack(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            if (loyaltyProgramName.equals(LOYALTY_PROGRAM_BLACK)) {
                return transactionSum / 100 * 1
            }
            return 0.0
        }
    }

    private fun programmAll(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            if (mccCode?.equals(MCC_SOFTWARE) ?: false
                && loyaltyProgramName.equals(LOYALTY_PROGRAM_ALL)
                && chekPalindrom(transactionSum)
            ) {
                val a = firstName.length
                val b = lastName.length
                val lcm = lcm(a, b)
                return (lcm / 1000.0 / 100 * (transactionSum)).toBigDecimal()
                    .setScale(1, RoundingMode.HALF_EVEN).toDouble()
            }
        }
        return 0.0
    }



    private fun programmBeer(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            if (loyaltyProgramName.equals(LOYALTY_PROGRAM_BEER) && mccCode!!.equals(
                    MCC_BEER
                )
            ) {

                if (firstName.toLowerCase().equals("олег")) {
                    if (lastName.toLowerCase().equals("олегов")) {
                        return transactionSum / 100 * 10
                    } else {
                        return transactionSum / 100 * 7
                    }
                }

                if (LocalDateTime.now().month.getDisplayName(
                        TextStyle.SHORT,
                        Locale("ru")
                    ).toLowerCase().first().equals(
                        firstName.toLowerCase().first()
                    )
                ) {
                    return transactionSum / 100 * 5
                }

                if (LocalDateTime.now().plusMonths(1).month.getDisplayName(
                        TextStyle.SHORT,
                        Locale("ru")
                    ).toLowerCase().first().equals(firstName.toLowerCase().first())
                    || LocalDateTime.now().minusMonths(1).month.getDisplayName(
                        TextStyle.SHORT,
                        Locale("ru")
                    ).toLowerCase().first().equals(firstName.toLowerCase().first())
                ) {
                    return transactionSum / 100 * 3

                }

                return transactionSum / 100 * 2
            }
        }
        return 0.0
    }

    private fun additionalCashBack(transactionInfo: TransactionInfo): Double {
        if (transactionInfo.transactionSum.rem(666).equals(0.0)) {
            return 6.66
        }
        return 0.0
    }

    private fun chekPalindrom(double: Double): Boolean {
        val st = double.toString().filter { char -> !char.equals('.') }
        var t = 0
        val l = st.length - 1
        for (s in 0..l / 2) {
            if (!st[s].equals(st[l])) {
                t++
            }
        }
        return t <= 1
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b === 0) a else gcd(b, a % b)
    }

    private fun lcm(a: Int, b: Int): Int {
        return a / gcd(a, b) * b
    }
}
