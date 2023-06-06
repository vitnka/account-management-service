package repository

import cats.implicits.catsSyntaxEitherId
import domain.error.ReservationAlreadyExist
import domain.{OperationType, ReservationId}
import doobie.implicits.toSqlInterpolator
import doobie.{ConnectionIO, Update0}
import dto.{DefaultResponse, ReservationRequest}

trait ReservationRepository {
  def refundReservation(reservationId: ReservationId):ConnectionIO[Either[ReservationAlreadyExist, DefaultResponse]]

  def create(create: ReservationRequest): ConnectionIO[Either[ReservationAlreadyExist, DefaultResponse]]

}

object ReservationRepository {
  object sqls {
    def createSql(reservation: ReservationRequest): Update0 =
      sql"insert into reservations (account_id, amount) values (${reservation.accountId.value}, ${reservation.amount.value})".update
  }

  private final class Impl extends ReservationRepository {
    import sqls._

    override def create(reservation: ReservationRequest): ConnectionIO[Either[ReservationAlreadyExist, DefaultResponse]] = {
      createSql(reservation)
            .withUniqueGeneratedKeys[ReservationId]("id")
            .map(id => DefaultResponse(s"Successfully created with id = ${id}").asRight[ReservationAlreadyExist])
      }

    override def refundReservation(reservationId: ReservationId): ConnectionIO[Either[ReservationAlreadyExist, DefaultResponse]] = ???
  }

  def make(): ReservationRepository = new Impl
}

