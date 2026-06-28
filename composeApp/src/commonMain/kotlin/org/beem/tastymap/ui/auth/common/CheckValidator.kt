package org.beem.tastymap.ui.auth.common

object CheckValidator {
    private val USERNAME_PATTERN = Regex("^[a-zA-Z0-9._]+$")
    private val NAME_PATTERN = Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ ]+$")
    private val SURNAME_PATTERN = Regex("^[a-zA-ZçÇğĞıİöÖşŞüÜ \\-']+$")
    private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email boş olamaz")
            !EMAIL_PATTERN.matches(email) -> ValidationResult.Invalid("Geçerli bir email girin")
            else -> ValidationResult.Valid
        }
    }
    fun validatePassword(password: String): ValidationResult {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.contains(Regex("[@#\$!%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"))

        return when {
            password.length < 8 -> ValidationResult.Invalid("Parola en az 8 karakter olmalı")
            !hasUppercase -> ValidationResult.Invalid("En az bir büyük harf içermeli")
            !hasLowercase -> ValidationResult.Invalid("En az bir küçük harf içermeli")
            !hasDigit -> ValidationResult.Invalid("En az bir rakam içermeli")
            !hasSpecialChar -> ValidationResult.Invalid("En az bir özel karakter içermeli (@, #, !, vb.)")
            else -> ValidationResult.Valid
        }
    }
    fun validateName(name:String): ValidationResult{
        return when{
            name.isBlank() -> ValidationResult.Invalid("Ad boş olamaz")
            name.length !in 2..50 -> ValidationResult.Invalid("Ad 2–50 karakter arasında olmalıdır")
            !NAME_PATTERN.matches(name) -> ValidationResult.Invalid("Ad sadece harf ve boşluk içerebilir")
            else -> ValidationResult.Valid
        }
    }
    fun validateSurname(surname:String) : ValidationResult{
        return when{
            surname.isBlank() -> ValidationResult.Invalid("Soyad boş olamaz")
            surname.length !in 2..50 -> ValidationResult.Invalid("Ad 2–50 karakter arasında olmalıdır")
            !SURNAME_PATTERN.matches(surname) -> ValidationResult.Invalid("Soyad sadece harf içerebilir")
            else -> ValidationResult.Valid
        }
    }
    fun validateUsername(username: String): ValidationResult {
        return when {
            username.isBlank() -> ValidationResult.Invalid("Kullanıcı adı boş olamaz")
            username.length !in 3..20 -> ValidationResult.Invalid("Kullanıcı adı 3–20 karakter arasında olmalıdır")
            !USERNAME_PATTERN.matches(username) -> ValidationResult.Invalid("Kullanıcı adı sadece harf, rakam, nokta ve alt çizgi içerebilir")
            else -> ValidationResult.Valid
        }
    }

    fun validateRequiredField(value: String, errorMessage: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Invalid(errorMessage)
        } else {
            ValidationResult.Valid
        }
    }

}
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}