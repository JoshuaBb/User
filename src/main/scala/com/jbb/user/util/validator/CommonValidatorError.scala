package com.jbb.user.util.validator

import cats.Semigroup
import cats.data.NonEmptyList
import com.jbb.user.util.types.Email
import cats.implicits._

/**
 * CommonValidatorError acts as a SemiGroup that can collect multiple errors and chain them together as a NonEmptyList
 */
sealed trait CommonValidatorError {
  def displayMessage: String
}

object CommonValidatorError:
  given Semigroup[CommonValidatorError] = (x: CommonValidatorError, y: CommonValidatorError) => (x, y) match {
    case (CollectedError(errorsX), CollectedError(errorsY)) => CollectedError(errorsX concatNel errorsY)
    case (CollectedError(errorsX), error) => CollectedError(errorsX :+ error)
    case (error, CollectedError(errorsX)) => CollectedError(errorsX :+ error)
    case (errorX, errorY) => CollectedError(NonEmptyList.of(errorX, errorY))
  }

case class CollectedError(errors: NonEmptyList[CommonValidatorError]) extends CommonValidatorError:
  override def displayMessage: Email = errors.map(_.displayMessage).toList.mkString(",")

case class InvalidEmailError(email: Email) extends CommonValidatorError {
  override def displayMessage: Email = s"$email is an invalid email"
}
// Never display the password in the logs
case class InvalidPasswordError() extends CommonValidatorError {
  override def displayMessage: Email = s"User sent an invalid password"
}
