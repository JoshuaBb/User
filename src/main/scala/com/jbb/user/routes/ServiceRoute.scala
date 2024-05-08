package com.jbb.user.routes

import cats.{Applicative, MonadThrow}
import cats.data.{Kleisli, Validated}
import cats.data.Validated.{Invalid, Valid}
import cats.syntax.all.*
import org.http4s.*
import org.http4s.Status
import org.http4s.Status.Ok

/**
 * ServiceRoute acts as a utility trait for handling HTTP request logic
 */
trait ServiceRoute {

  // Playing with Kleisli. Probably not using Kleisli at all one look cleaner.
  private def decodeRequest[F[_], Req](implicit F: MonadThrow[F], decoder: EntityDecoder[F, Req]): Kleisli[F, Request[F], Req] =
    Kleisli[F, Request[F], Req](_.as[Req])

  private def handleSuccess[F[_] : Applicative, Res](response: Res)(implicit encoder: EntityEncoder[F, Res]): F[Response[F]] =
    Response[F](status = Status.Ok, body = encoder.toEntity(response).body).pure[F]

  private def handleError[F[_] : Applicative]: F[Response[F]] =
    Response[F](status = Status.BadRequest).pure[F]


  /**
   * Utility method for chaining together request decoding -> request validation -> service logic -> response encoding
   * If a request has additional validation or needs to pass addition parameters currying is fine.
   *
   * @param request: The HTTP Request that gets sent
   * @param validateF: After parsing the request body validation will be made to determine if it is a valid request body
   * @param serviceLogicF: After determining it is a valid request body then it will
   * @param F: Typically just Cats IO
   * @param decoder: Decodes the request[Req]
   * @param encoder: Encodes the response[Res]
   * @tparam F
   * @tparam Req
   * @tparam Res
   * @tparam Error
   * @return
   */
  def handleRequestWithValidation[F[_] : Applicative, Req, Res, Error](request: Request[F],
                                                                       validateF: Req => Validated[Error, Req],
                                                                       serviceLogicF: Req => F[Res]
                                                                      )(implicit F: MonadThrow[F], decoder: EntityDecoder[F, Req], encoder: EntityEncoder[F, Res])
  : F[Response[F]] = {
    decodeRequest[F, Req]
      .map(validateF(_))
      .flatMapF {
        case Valid(req) => serviceLogicF(req).flatMap(handleSuccess)
        case Invalid(_) => handleError
      }.run(request)
  }

  def handleRequest[F[_] : Applicative, Req, Res](request: Request[F],
                                                  serviceLogicF: Req => F[Res]
                                                 )(implicit F: MonadThrow[F], decoder: EntityDecoder[F, Req], encoder: EntityEncoder[F, Res]): F[Response[F]] =
    decodeRequest[F, Req]
      .flatMapF { req => serviceLogicF(req).flatMap(handleSuccess) }
      .run(request)
}
