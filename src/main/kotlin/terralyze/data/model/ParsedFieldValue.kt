package terralyze.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ParsedFieldValue<out T> {
    @Serializable
    data object Absent : ParsedFieldValue<Nothing>

    @Serializable
    data class Present<T>(
        val value: T
    ) : ParsedFieldValue<T>
}

fun <T> ParsedFieldValue<T>.getOrNull(): T? =
    when (this) {
        ParsedFieldValue.Absent -> null
        is ParsedFieldValue.Present -> value
    }