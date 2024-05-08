package com.jbb.user

import cats.effect.{Async, Resource}
import com.jbb.user.routes.UserServiceRoute
import com.jbb.user.services.UserService
import com.jbb.user.store.UserStore
import com.jbb.user.util.db.DoobieDb
import doobie.util.transactor.Transactor
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits.*


object UserServer:

  def run[F[_]: Async: Network](appConfig: AppConfig): F[Nothing] = {
    // TODO: Mess with dependency injection to clean this up.

    // given AppLogger[F] = Slf4jLogger.getLogger[F]
    val serverConfig = appConfig.server
    val db = appConfig.db

    for {
      dbTransactor <- Resource.Pure[F, Transactor[F]](DoobieDb.impl[F](
        driver = db.driver,
        url = db.url,
        user = db.user,
        password = db.password,
      ).transactor)
      // Stores 
      userStore = UserStore.impl[F](dbTransactor)
      // Services 
      userService: UserService[F] = UserService.impl[F](userStore)
      // Routes 
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
