package com.jbb.user.services

import cats.Applicative
import cats.syntax.all.*
import com.jbb.user.model.UserRegistration

trait UserService[F[_]]:
  def registerUser(user: UserRegistration): F[UserRegistration]

object UserService:
  def impl[F[_]: Applicative]: UserService[F] = new UserService[F]{
    override def registerUser(user: UserRegistration): F[UserRegistration] = user.pure[F]
  }
