package com.jbb.user.util.validator

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.jbb.user.util.types.{Email, Password}


object CommonValidators {

  def isValidEmail(email: Email): Validated[CommonValidatorError, Email] = {
    // Copied email regex
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    if (email.matches(emailRegex)) {
      Valid(email)
    } else {
      Invalid(InvalidEmailError(email))
    }
  }

  def isValidPassword(password: Password): Validated[CommonValidatorError, Password] = {
    // Copied password regex, but should mean at least 12 digit password that has one lower case, one upper case, a number, and a special character.
    // I think I need to validate the special character
    val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{12,}$"
    if(password.matches(passwordRegex)) Valid(password)
    else Invalid(InvalidPasswordError())
  }

}
