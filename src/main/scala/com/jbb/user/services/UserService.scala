package com.jbb.user.services

import cats.Applicative
import cats.syntax.all.*
import com.jbb.user.model.UserRegistration
import com.jbb.user.store.UserStore

trait UserService[F[_]]:
  def registerUser(user: UserRegistration): F[UserRegistration]

object UserService:
  def impl[F[_]: Applicative](userStore: UserStore[F]): UserService[F] = new UserService[F]{
    override def registerUser(user: UserRegistration): F[UserRegistration] = user.pure[F]
  }
