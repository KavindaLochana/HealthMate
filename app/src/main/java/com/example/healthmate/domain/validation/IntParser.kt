package com.example.healthmate.domain.validation

sealed interface ParseResult {
    data class Value(val value: Int) : ParseResult
    data class Failure(val error: InputError) : ParseResult
}

// parsing rules

object IntParser {
    private val integerPattern = Regex("-?\\d+")

    fun parse(raw: String, range: IntRange): ParseResult {
        val text = raw.trim()
        if (text.isEmpty()) return ParseResult.Failure(InputError.Required)
        if (!integerPattern.matches(text)) return ParseResult.Failure(InputError.NotANumber)

        // A digit string that does not fit in an Int is a number, just far out of range.
        val value = text.toIntOrNull()
        if (value == null || value !in range) {
            return ParseResult.Failure(InputError.OutOfRange(range.first, range.last))
        }
        return ParseResult.Value(value)
    }
}