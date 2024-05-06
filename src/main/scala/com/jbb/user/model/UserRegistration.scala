package com.jbb.user.model

import cats.data.Validated
import cats.effect.Concurrent
import com.jbb.user.util.types.{Email, Password}
import com.jbb.user.util.validator.{CommonValidatorError, CommonValidators}
import cats.implicits.*
import io.circe.{Decoder, Encoder}
import org.http4s.EntityEncoder
import org.http4s.*
import org.http4s.circe.*


case class UserRegistration(email: Email, password: Password)

object UserRegistration extends ValidationRule[CommonValidatorError, UserRegistration] {

  given Decoder[UserRegistration] = Decoder.derived[UserRegistration]
  given Encoder[UserRegistration] = Encoder.AsObject.derived[UserRegistration]
  given [F[_]]: EntityEncoder[F, UserRegistration] =
    jsonEncoderOf[F, UserRegistration]
  given [F[_] : Concurrent]: EntityDecoder[F, UserRegistration] = jsonOf

  override def validate(userRegistration: UserRegistration): Validated[CommonValidatorError, UserRegistration] = {
      val emailValidator = CommonValidators.isValidEmail(userRegistration.email)
      val passwordValidator = CommonValidators.isValidPassword(userRegistration.password)
      val result = (emailValidator, passwordValidator).mapN((_, _) => userRegistration)
      result
  }
}

