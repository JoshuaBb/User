package com.jbb.user.routes

import cats.implicits.*
import cats.effect.Sync
import com.jbb.user.HelloWorld
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

object HelloWorldRoute:
  def helloWorldRoutes[F[_] : Sync](H: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
