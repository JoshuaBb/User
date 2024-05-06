package com.jbb.user.services

import cats.Applicative
import cats.syntax.all.*
import com.jbb.user.model.UserRegistration
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.*

trait UserService[F[_]]:
  def registerUser(user: UserRegistration): F[UserRegistration]

object HelloWorld:
  final case class Greeting(greeting: String) extends AnyVal
  object Greeting:
    given Encoder[Greeting] = (a: Greeting) => Json.obj(
      ("message", Json.fromString(a.greeting)),
    )

    given [F[_]]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[F, Greeting]

  def impl[F[_]: Applicative]: UserService[F] = new UserService[F]{
    override def registerUser(user: UserRegistration): F[UserRegistration] = user.pure[F]
  }
