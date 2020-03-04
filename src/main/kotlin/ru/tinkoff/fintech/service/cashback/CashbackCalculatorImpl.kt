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
            var cashback: Double
            cashback = when (loyaltyProgramName) {
                "BLACK" -> programmBlack(this)
                "ALL" -> programmAll(this)
                "BEER" -> programmBeer(this)
                else -> return 0.0
            }
            cashback += additionalCashback(this)
            return if (cashback + cashbackTotalValue > MAX_CASH_BACK) {
                MAX_CASH_BACK - cashbackTotalValue
            } else {
                cashback
            }
        }
    }

    private fun programmBlack(transactionInfo: TransactionInfo): Double {
        return transactionInfo.transactionSum / 100 * 1
    }

    private fun additionalCashback(transactionInfo: TransactionInfo): Double {
        if (transactionInfo.transactionSum.rem(666).equals(0.0)) {
            return 6.66
        }
        return 0.0
    }


    private fun programmAll(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            return if (mccCode?.equals(MCC_SOFTWARE) == true
                && isPalindrom(transactionSum)
            ) {
                val lcm = lcm(firstName.length, lastName.length)
                (lcm / 1000.0 / 100 * (transactionSum)).toBigDecimal()
                    .setScale(1, RoundingMode.HALF_EVEN).toDouble()
            } else {
                0.0
            }
        }
    }

    private fun programmBeer(transactionInfo: TransactionInfo): Double {
        with(transactionInfo) {
            if (loyaltyProgramName == LOYALTY_PROGRAM_BEER && mccCode!! == MCC_BEER) {

                if (firstName.toLowerCase() == "олег") {
                    return if (lastName.toLowerCase() == "олегов") {
                        transactionSum / 100 * 10
                    } else {
                        transactionSum / 100 * 7
                    }
                }

                return if (LocalDateTime.now().month.getDisplayName(
                        TextStyle.SHORT,
                        Locale("ru")
                    ).toLowerCase().first() == firstName.toLowerCase().first()
                ) {
                    transactionSum / 100 * 5
                } else if (compareFirstSignOnRoundMonthAndName(firstName)) {
                    transactionSum / 100 * 3
                } else transactionSum / 100 * 2

            } else {
                return 0.0
            }
        }
    }

    private fun compareFirstSignOnRoundMonthAndName(firstName: String): Boolean {
        return (LocalDateTime.now().plusMonths(1).month.getDisplayName(
            TextStyle.SHORT,
            Locale("ru")
        ).toLowerCase().first() == firstName.toLowerCase().first()
                || LocalDateTime.now().minusMonths(1).month.getDisplayName(
            TextStyle.SHORT,
            Locale("ru")
        ).toLowerCase().first() == firstName.toLowerCase().first()
                )
    }

    private fun isPalindrom(double: Double): Boolean {
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
        return if (b == 0) a else gcd(b, a % b)
    }

    private fun lcm(a: Int, b: Int): Int {
        return a / gcd(a, b) * b
    }
}
