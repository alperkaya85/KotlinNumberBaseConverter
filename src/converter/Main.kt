package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Optional
import kotlin.math.pow

val hexMap = mapOf(
    "A" to 10,
    "B" to 11,
    "C" to 12,
    "D" to 13,
    "E" to 14,
    "F" to 15,
    "G" to 16,
    "H" to 17,
    "I" to 18,
    "J" to 19,
    "K" to 20,
    "L" to 21,
    "M" to 22,
    "N" to 23,
    "O" to 24,
    "P" to 25,
    "Q" to 26,
    "R" to 27,
    "S" to 28,
    "T" to 29,
    "U" to 30,
    "V" to 31,
    "W" to 32,
    "X" to 33,
    "Y" to 34,
    "Z" to 35
)
val remainders = mapOf(
    10 to "A",
    11 to "B",
    12 to "C",
    13 to "D",
    14 to "E",
    15 to "F",
    16 to "G",
    17 to "H",
    18 to "I",
    19 to "J",
    20 to "K",
    21 to "L",
    22 to "M",
    23 to "N",
    24 to "O",
    25 to "P",
    26 to "Q",
    27 to "R",
    28 to "S",
    29 to "T",
    30 to "U",
    31 to "V",
    32 to "W",
    33 to "X",
    34 to "Y",
    35 to "Z"
)

fun main() {
    print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
    var where = readln()
    var base: MutableList<Int>
    var sourceBase: Int
    var targetBase: Int
    var number: String
    var calculations = hashMapOf<String, String>()
    while (!where.lowercase().contains("exit")) {
        base = where.split(" ").mapTo(mutableListOf<Int>()) { it.toInt() }

        sourceBase = base[0]
        targetBase = base[1]

        print("Enter number in base ${sourceBase} to convert to base $targetBase (To go back type /back) ")
        number = readln()

        while (number.lowercase() != "/back") {
            var result: String
            val key = "$number-$sourceBase-$targetBase"
            if (calculations.contains(key)) {
                result = calculations.get(key)!!
            } else if (sourceBase == 10) {
                result = convertDecimalToOther(number.toBigDecimal(), targetBase)

                calculations.put(key, result)
            } else if (targetBase == 10) {
                result = convertOtherToDecimal(number, sourceBase).toString()

                calculations.put(key, result)
            } else {
                result = convertOtherToDecimal(number, sourceBase).toString()
                result = convertDecimalToOther(result.toBigDecimal(), targetBase)

                calculations.put(key, result)
            }

            println("Conversion result: " + result)
            println()
            print("Enter number in base ${sourceBase} to convert to base $targetBase (To go back type /back) ")
            number = readln()
        }
        println()
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        where = readln()

    }

}

fun convertDecimalToOther(num: BigDecimal, targetBase: Int): String {
    var result = mutableListOf<Char>()
    var remainder: BigInteger
    var remainderInt: Int
    var number: BigInteger
    var fraction: Optional<BigDecimal>
    if (num.toString().contains(".")) {
        var arr = num.toString().split(".")
        number = arr[0].toBigInteger()
        fraction = Optional.of(BigDecimal("0.${arr[1]}"))
    } else {
        number = num.toBigInteger()
        fraction = Optional.empty()
    }

    do {
        remainder = number % targetBase.toBigInteger()
        remainderInt = remainder.toInt()
        if (remainderInt >= 10) {
            result.add(0, remainders.get(remainderInt)!!.toCharArray()[0])
        } else {
            result.add(0, remainderInt.digitToChar())
        }
        number /= targetBase.toBigInteger()
    } while (number != BigInteger.ZERO)

    var resultStr = result.joinToString(separator = "")
    var resultStrObj: StringBuilder

    if (fraction.isPresent) {
        resultStrObj = StringBuilder(resultStr)
        resultStrObj.append(getDecimalToOtherFraction(fraction.get(), targetBase))
        return resultStrObj.toString()
    } else {
        return resultStr
    }

    return result.joinToString(separator = "")
}

fun getDecimalToOtherFraction(frac: BigDecimal, targetBase: Int): String {

    val resultStrObj = StringBuilder(".")
    var step = 0
    var fraction = frac
    do {
        step++
        fraction = fraction!!.multiply(targetBase.toBigDecimal())
        if ((fraction % targetBase.toBigDecimal()).compareTo(BigDecimal.TEN) >= 0) {
            resultStrObj.append(remainders.get((fraction % targetBase.toBigDecimal()).toInt()))
        } else {
            resultStrObj.append((fraction % targetBase.toBigDecimal()).toInt())
        }
        if (fraction.compareTo((targetBase - 1).toBigDecimal()) >= 0) {
            fraction = fraction % targetBase.toBigDecimal()
        }

        if (step == 5) {
            break
        }
    } while (fraction!!.compareTo(BigDecimal.ZERO) != 0)
    val trailing: Int
    if (resultStrObj.substring(1).length < 5) {
        trailing = 5 - resultStrObj.substring(1).length
        for (i in 1..trailing) {
            resultStrObj.append("0")
        }
    }

    return resultStrObj.toString()
}

fun convertOtherToDecimal(number: String, sourceBase: Int): String {
    var p = 0
    var resultStr = BigDecimal.ZERO
    var resultInt = BigInteger.ZERO
    var digit: Int?
    var numberStr: String
    var fraction: Optional<String>
    if (number.contains(".")) {
        var arr = number.split(".")
        numberStr = arr[0]
        fraction = Optional.of(arr[1])
    } else {
        numberStr = number
        fraction = Optional.empty<String>()
    }
    for (i in numberStr.toCharArray().size - 1 downTo 0) {
        if (numberStr.toCharArray()[i].isLetter()) {
            digit = hexMap[numberStr.toCharArray()[i].uppercase()]
        } else {
            digit = numberStr.toCharArray()[i].digitToInt()
        }
        resultInt += digit!!.toBigInteger() * sourceBase.toBigInteger().pow(p.toInt())
        p++
    }

    p = 0
    if (fraction.isPresent) {
        resultStr = resultStr.add(resultInt.toBigDecimal())
        return resultStr.add(getOtherToDecimalFraction(fraction.get(), sourceBase)).toString()
    } else {
        return resultInt.toString()
    }
}

fun getOtherToDecimalFraction(fraction: String, sourceBase: Int): BigDecimal {
    var p = 0
    var num = BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP)
    for (digit in fraction) {
        p++

        if (digit.isLetter()) {
            num = num.add(
                hexMap[digit.uppercase()]!!.toBigDecimal()
                    .multiply((1.0 / sourceBase.toDouble().pow(p.toDouble())).toBigDecimal())
            )
        } else {
            num = num.add(
                (((1.0 / sourceBase.toDouble()).pow(p)) *
                        digit.digitToInt().toDouble()

                        ).toBigDecimal()
            )
        }
    }
    return num.toString().toBigDecimal()
}