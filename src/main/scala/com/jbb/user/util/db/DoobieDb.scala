package com.jbb.user.util.db

import cats.effect.kernel.Async
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor

trait DoobieDb[F[_]]:
  def transactor: Transactor[F]

/***
 * Initializes a transactor for the Doobie Database
 */
object DoobieDb:
  def impl[F[_]: Async](
                         driver: String = "org.postgres.Driver",
                         url: String = "jdbc:postgresql:world",
                         user: String = "postgres",
                         password: String = "password",
                         logHandler :Option[LogHandler[F]] = None
                       )
  : DoobieDb[F] = new DoobieDb[F]:
    override val transactor: Transactor[F] = Transactor.fromDriverManager[F](
      driver = driver,
      url = url,
      user = user,
      password = password, 
      logHandler = logHandler
    )


