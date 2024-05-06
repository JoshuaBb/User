package com.jbb.user.routes

import cats.effect.kernel.Concurrent
import com.jbb.user.model.UserRegistration
import com.jbb.user.services.UserService
import com.jbb.user.util.validator.CommonValidatorError
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

object UserServiceRoute extends ServiceRoute:
  def routes[F[_] : Concurrent](service: UserService[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case req@POST -> Root / "register" / "user" =>
        handleRequestWithValidation[F, UserRegistration, UserRegistration, CommonValidatorError](req)(UserRegistration.validate)(service.registerUser)
    }
