package com.jbb.user

import cats.effect.Async
import com.jbb.user.routes.UserServiceRoute
import com.jbb.user.services.UserService
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits.*


object UserServer:

  def run[F[_]: Async: Network](appConfig: AppConfig): F[Nothing] = {
    // given AppLogger[F] = Slf4jLogger.getLogger[F]
    val serverConfig = appConfig.server
    for {
      client <- EmberClientBuilder.default[F].build
      userService = UserService.impl[F]
      httpApp = (UserServiceRoute.routes[F](userService)).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      _ <-
        EmberServerBuilder.default[F]
          .withHost(serverConfig.host)
          .withPort(serverConfig.port)
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
