package service

import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxEitherId}
import domain.error.{InternalError, ServiceError}
import domain.{IOWithRequestContext, ReservationId}
import doobie.Transactor
import doobie.implicits._
import dto.{DefaultResponse, ReservationRequest}
import repository.ReservationRepository


trait ReservationService {
  def refundReservation(orderId: domain.OrderId): IOWithRequestContext[Either[ServiceError, DefaultResponse]]

  def revenueReservation(request: ReservationRequest): IOWithRequestContext[Either[ServiceError, DefaultResponse]]

  def createDeposit(request: ReservationRequest): IOWithRequestContext[Either[ServiceError, DefaultResponse]]

}

object ReservationService {

  private final class ReservationServiceImpl(reservationRepository: ReservationRepository, transactor: Transactor[IOWithRequestContext]) extends ReservationService {
    override def createDeposit(request: ReservationRequest): IOWithRequestContext[Either[ServiceError, DefaultResponse]] = {
        reservationRepository.create(request).transact(transactor).attempt.map {
          case Left(th) => InternalError(th).asLeft[DefaultResponse]
          case Right(Left(error)) => error.asLeft[DefaultResponse]
          case Right(Right(operation)) => operation.asRight[ServiceError]
        }
    }

    override def refundReservation(orderId: domain.OrderId): IOWithRequestContext[Either[ServiceError, DefaultResponse]] = ???

    override def revenueReservation(request: ReservationRequest): IOWithRequestContext[Either[ServiceError, DefaultResponse]] = ???
  }

  def make(reservationRepository: ReservationRepository, transactor: Transactor[IOWithRequestContext]): ReservationService =
    new ReservationServiceImpl(reservationRepository, transactor)
}
