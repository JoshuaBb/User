package com.jbb.user

import cats.effect.{IO, IOApp}
import pureconfig.ConfigSource

object Main extends IOApp.Simple:
  val run: IO[Nothing] = ConfigSource.default.load[Config] match {
    case Right(value) =>  UserServer.run[IO](value.app)
    case Left(error) =>
      IO.raiseError(new RuntimeException("Failed to initialize config"))
  }
