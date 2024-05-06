package com.jbb.user.routes

import cats.{Applicative, MonadThrow}
import cats.data.{Kleisli, Validated}
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all.*
import org.http4s.*
import org.http4s.Status
import org.http4s.Status.Ok

trait ServiceRoute {


  // Playing with Kleisli. Probably not using Kleisli at all one look cleaner.
  // Maybe if I added more than one utility method it would start looking a bit more cleaner and reusable
  private def decodeKleisli[F[_], Req](implicit F: MonadThrow[F], decoder: EntityDecoder[F, Req]): Kleisli[F, Request[F], Req] = Kleisli((request: Request[F]) => request.as[Req])

  private def validationSuccessKleisli[F[_], Req, Res](handleF: Req => F[Res])(implicit F: MonadThrow[F], encoder: EntityEncoder[F, Res]): Kleisli[F, Req, Response[F]] = Kleisli[F, Req, Res](handleF).map { res =>
    Response[F](
      status = Status.Ok,
      body = encoder.toEntity(res).body
    )
  }

  // Not doing too much with the errors yet
  private def validationErrorKleisli[F[_] : Applicative, Error]: Kleisli[F, Error, Response[F]] = Kleisli[F, Error, Response[F]] { _ => Response[F](status = Status.BadRequest).pure[F] }

  def handleBadRequestWithBody[F[_] : Applicative, Req, Res, Error](request: Request[F])
                                                                   (validateF: Req => Validated[Error, Req])
                                                                   (handleF: Req => F[Res])
                                                                   (implicit F: MonadThrow[F], decoder: EntityDecoder[F, Req], encoder: EntityEncoder[F, Res])
  : F[Response[F]] = {

    val validateKleisli: Kleisli[F, Request[F], Validated[Error, Req]] =
      decodeKleisli[F, Req].map(validateF(_))

    val handle: Kleisli[F, Req, Response[F]] =
      validationSuccessKleisli[F, Req, Res](handleF(_))

    validateKleisli.flatMapF {
      case Valid(value) => handle.run(value)
      case Invalid(error) => validationErrorKleisli.run(error)
    }.run(request)
  }
}
