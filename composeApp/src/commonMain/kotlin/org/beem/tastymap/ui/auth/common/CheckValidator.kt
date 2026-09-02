package org.beem.tastymap.ui.auth.common

import org.jetbrains.compose.resources.StringResource
import tastymap.composeapp.generated.resources.Res
import tastymap.composeapp.generated.resources.validation_email_empty
import tastymap.composeapp.generated.resources.validation_email_invalid
import tastymap.composeapp.generated.resources.validation_name_empty
import tastymap.composeapp.generated.resources.validation_name_invalid
import tastymap.composeapp.generated.resources.validation_name_length
import tastymap.composeapp.generated.resources.validation_password_digit
import tastymap.composeapp.generated.resources.validation_password_lowercase
import tastymap.composeapp.generated.resources.validation_password_min_length
import tastymap.composeapp.generated.resources.validation_password_special_char
import tastymap.composeapp.generated.resources.validation_password_uppercase
import tastymap.composeapp.generated.resources.validation_surname_empty
import tastymap.composeapp.generated.resources.validation_surname_invalid
import tastymap.composeapp.generated.resources.validation_surname_length
import tastymap.composeapp.generated.resources.validation_username_empty
import tastymap.composeapp.generated.resources.validation_username_invalid
import tastymap.composeapp.generated.resources.validation_username_length

object CheckValidator {
    private val USERNAME_PATTERN = Regex("^[a-zA-Z0-9._]+$")
    private val NAME_PATTERN = Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ ]+$")
    private val SURNAME_PATTERN = Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ \\-']+$")
    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(Res.string.validation_email_empty)
            !EMAIL_PATTERN.matches(email) -> ValidationResult.Invalid(Res.string.validation_email_invalid)
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(password: String): ValidationResult {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.contains(Regex("[@#\$!%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"))

        return when {
            password.length < 8 -> ValidationResult.Invalid(Res.string.validation_password_min_length)
            !hasUppercase -> ValidationResult.Invalid(Res.string.validation_password_uppercase)
            !hasLowercase -> ValidationResult.Invalid(Res.string.validation_password_lowercase)
            !hasDigit -> ValidationResult.Invalid(Res.string.validation_password_digit)
            !hasSpecialChar -> ValidationResult.Invalid(Res.string.validation_password_special_char)
            else -> ValidationResult.Valid
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid(Res.string.validation_name_empty)
            name.length !in 2..50 -> ValidationResult.Invalid(Res.string.validation_name_length)
            !NAME_PATTERN.matches(name) -> ValidationResult.Invalid(Res.string.validation_name_invalid)
            else -> ValidationResult.Valid
        }
    }

    fun validateSurname(surname: String): ValidationResult {
        return when {
            surname.isBlank() -> ValidationResult.Invalid(Res.string.validation_surname_empty)
            surname.length !in 2..50 -> ValidationResult.Invalid(Res.string.validation_surname_length)
            !SURNAME_PATTERN.matches(surname) -> ValidationResult.Invalid(Res.string.validation_surname_invalid)
            else -> ValidationResult.Valid
        }
    }

    fun validateUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult.Invalid(Res.string.validation_username_empty)
            username.length !in 3..20 -> ValidationResult.Invalid(Res.string.validation_username_length)
            !USERNAME_PATTERN.matches(username) -> ValidationResult.Invalid(Res.string.validation_username_invalid)
            else -> ValidationResult.Valid
        }
    }

    fun validateRequiredField(value: String, errorRes: StringResource): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Invalid(errorRes)
        } else {
            ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val messageRes: StringResource) : ValidationResult()
}