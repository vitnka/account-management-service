package domain

import cats.syntax.option._
import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.Schema

object error {
  sealed abstract class ServiceError(
                                  val message: String,
                                  val cause: Option[Throwable] = None
                                )
  object ServiceError {
    implicit val encoder: Encoder[ServiceError] = new Encoder[ServiceError] {
      override def apply(a: ServiceError): Json = Json.obj(
        ("message", Json.fromString(a.message))
      )
    }

    implicit val decoder: Decoder[ServiceError] = new Decoder[ServiceError] {
      override def apply(c: HCursor): Decoder.Result[ServiceError] =
        c.downField("message").as[String].map(MockError(_))
    }

    implicit val schema: Schema[ServiceError] = Schema.string[ServiceError]
  }

  case class DepositAlreadyExists()
    extends ServiceError("Deposit with same amount value exists")
  case class ReservationAlreadyExist(id: ReservationId)
    extends ServiceError(s"Reservation with id ${id.value} not found")
  case class InternalError(cause0: Throwable)
    extends ServiceError("Internal error", cause0.some)
  case class MockError(override val message: String) extends ServiceError(message)
}
