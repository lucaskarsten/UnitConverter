package converter

enum class UnitType {
    LENGTH, WEIGHT, TEMPERATURE, UNKNOWN
}

enum class Unit(val type: UnitType, val singular: String, val plural: String) {
    METER(UnitType.LENGTH, "meter", "meters"),
    KILOMETER(UnitType.LENGTH, "kilometer", "kilometers"),
    CENTIMETER(UnitType.LENGTH, "centimeter", "centimeters"),
    MILLIMETER(UnitType.LENGTH, "millimeter", "millimeters"),
    MILE(UnitType.LENGTH, "mile", "miles"),
    YARD(UnitType.LENGTH, "yard", "yards"),
    FOOT(UnitType.LENGTH, "foot", "feet"),
    INCH(UnitType.LENGTH, "inch", "inches"),
    GRAM(UnitType.WEIGHT, "gram", "grams"),
    KILOGRAM(UnitType.WEIGHT, "kilogram", "kilograms"),
    MILLIGRAM(UnitType.WEIGHT, "milligram", "milligrams"),
    POUND(UnitType.WEIGHT, "pound", "pounds"),
    OUNCE(UnitType.WEIGHT, "ounce", "ounces"),
    CELSIUS(UnitType.TEMPERATURE, "degree Celsius", "degrees Celsius"),
    FAHRENHEIT(UnitType.TEMPERATURE, "degree Fahrenheit", "degrees Fahrenheit"),
    KELVIN(UnitType.TEMPERATURE, "kelvin", "kelvins"),
    UNKNOWN(UnitType.UNKNOWN, "???", "???");

    companion object {
        fun fromString(unit: String): Unit {
            return when (unit.lowercase()) {
                "m", "meter", "meters" -> METER
                "km", "kilometer", "kilometers" -> KILOMETER
                "cm", "centimeter", "centimeters" -> CENTIMETER
                "mm", "millimeter", "millimeters" -> MILLIMETER
                "mi", "mile", "miles" -> MILE
                "yd", "yard", "yards" -> YARD
                "ft", "foot", "feet" -> FOOT
                "in", "inch", "inches" -> INCH
                "g", "gram", "grams" -> GRAM
                "kg", "kilogram", "kilograms" -> KILOGRAM
                "mg", "milligram", "milligrams" -> MILLIGRAM
                "lb", "pound", "pounds" -> POUND
                "oz", "ounce", "ounces" -> OUNCE
                "degree celsius", "degrees celsius", "celsius", "dc", "c" -> CELSIUS
                "degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df", "f" -> FAHRENHEIT
                "kelvin", "kelvins", "k" -> KELVIN
                else -> UNKNOWN
            }
        }
    }
}

fun convertValue(value: Double, fromUnit: Unit, toUnit: Unit): Double {
    return when (fromUnit.type) {
        UnitType.LENGTH -> convertLength(value, fromUnit, toUnit)
        UnitType.WEIGHT -> convertWeight(value, fromUnit, toUnit)
        UnitType.TEMPERATURE -> convertTemperature(value, fromUnit, toUnit)
        else -> value
    }
}

fun convertLength(value: Double, from: Unit, to: Unit): Double {
    val meters = when (from) {
        Unit.METER -> value
        Unit.KILOMETER -> value * 1000
        Unit.CENTIMETER -> value / 100
        Unit.MILLIMETER -> value / 1000
        Unit.MILE -> value * 1609.35
        Unit.YARD -> value * 0.9144
        Unit.FOOT -> value * 0.3048
        Unit.INCH -> value * 0.0254
        else -> 0.0
    }

    return when (to) {
        Unit.METER -> meters
        Unit.KILOMETER -> meters / 1000
        Unit.CENTIMETER -> meters * 100
        Unit.MILLIMETER -> meters * 1000
        Unit.MILE -> meters / 1609.35
        Unit.YARD -> meters / 0.9144
        Unit.FOOT -> meters / 0.3048
        Unit.INCH -> meters / 0.0254
        else -> meters
    }
}

fun convertWeight(value: Double, from: Unit, to: Unit): Double {
    val grams = when (from) {
        Unit.GRAM -> value
        Unit.KILOGRAM -> value * 1000
        Unit.MILLIGRAM -> value / 1000
        Unit.POUND -> value * 453.592
        Unit.OUNCE -> value * 28.3495
        else -> 0.0
    }

    return when (to) {
        Unit.GRAM -> grams
        Unit.KILOGRAM -> grams / 1000
        Unit.MILLIGRAM -> grams * 1000
        Unit.POUND -> grams / 453.592
        Unit.OUNCE -> grams / 28.3495
        else -> grams
    }
}

fun convertTemperature(value: Double, from: Unit, to: Unit): Double {
    return when (from to to) {
        Unit.CELSIUS to Unit.FAHRENHEIT -> value * 9/5 + 32
        Unit.FAHRENHEIT to Unit.CELSIUS -> (value - 32) * 5/9
        Unit.CELSIUS to Unit.KELVIN -> value + 273.15
        Unit.KELVIN to Unit.CELSIUS -> value - 273.15
        Unit.FAHRENHEIT to Unit.KELVIN -> (value + 459.67) * 5/9
        Unit.KELVIN to Unit.FAHRENHEIT -> value * 9/5 - 459.67
        else -> value
    }
}

// Main.kt

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val input = readLine()?.trim() ?: continue
        if (input.equals("exit", ignoreCase = true)) {
            break
        }

        val parts = input.split(" ")
        if (parts.size < 4) {
            println("Parse error.")
            continue
        }

        val numberInput = parts[0]
        val number = numberInput.toDoubleOrNull()
        if (number == null) {
            println("Parse error.")
            continue
        }

        // Identifying the conversion word and split the input around it
        val toIndex = parts.indexOfFirst {
            it.equals("to", ignoreCase = true) ||
                    it.equals("in", ignoreCase = true) ||
                    it.equals("convertto", ignoreCase = true)
        }
        if (toIndex == -1 || toIndex >= parts.size - 1) {
            println("Parse error.")
            continue
        }

        val sourceUnitInput = parts.subList(1, toIndex).joinToString(" ").lowercase()
        val targetUnitInput = parts.subList(toIndex + 1, parts.size).joinToString(" ").lowercase()

        val sourceUnit = Unit.fromString(sourceUnitInput)
        val targetUnit = Unit.fromString(targetUnitInput)

        if (sourceUnit == Unit.UNKNOWN || targetUnit == Unit.UNKNOWN) {
            println("Conversion from ${sourceUnit.plural} to ${targetUnit.plural} is impossible")
            continue
        }

        if (sourceUnit.type != targetUnit.type) {
            println("Conversion from ${sourceUnit.plural} to ${targetUnit.plural} is impossible")
            continue
        }

        if (sourceUnit.type == UnitType.LENGTH && number < 0) {
            println("Length shouldn't be negative.")
            continue
        } else if (sourceUnit.type == UnitType.WEIGHT && number < 0) {
            println("Weight shouldn't be negative.")
            continue
        }

        val convertedValue = convertValue(number, sourceUnit, targetUnit)
        val sourceUnitName = if (number == 1.0) sourceUnit.singular else sourceUnit.plural
        val targetUnitName = if (convertedValue == 1.0) targetUnit.singular else targetUnit.plural
        println("$number $sourceUnitName is $convertedValue $targetUnitName\n")
    }
}