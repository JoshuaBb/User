package com.jbb.user

import cats.effect.Async
import com.jbb.user.routes.HelloWorldRoute
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger

object UserServer:

  def run[F[_]: Async: Network](appConfig: AppConfig): F[Nothing] = {
    // given AppLogger[F] = Slf4jLogger.getLogger[F]
    val serverConfig = appConfig.server
    for {
      client <- EmberClientBuilder.default[F].build
      helloWorldAlg = HelloWorld.impl[F]
      httpApp = (
        HelloWorldRoute.helloWorldRoutes[F](helloWorldAlg)
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      _ <-
        EmberServerBuilder.default[F]
          .withHost(serverConfig.host)
          .withPort(serverConfig.port)
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
