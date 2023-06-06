import cats.data.ReaderT
import cats.effect.IO
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.util.Read
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, Schema}
import tofu.logging.derivation._

import java.time.Instant

package object domain {

  @derive(loggable, encoder, decoder)
  @newtype
  case class OperationId(value: Int)
  object OperationId {
    implicit val doobieRead: Read[OperationId] = Read[Int].map(OperationId(_))
    implicit val schema: Schema[OperationId] =
      Schema.schemaForInt.map(l => Some(OperationId(l)))(_.value)
    implicit val codec: Codec[String, OperationId, TextPlain] =
      Codec.int.map(OperationId(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class ReservationId(value: Int)

  object ReservationId {
    implicit val doobieRead: Read[ReservationId] = Read[Int].map(ReservationId(_))
    implicit val schema: Schema[ReservationId] =
      Schema.schemaForInt.map(l => Some(ReservationId(l)))(_.value)
    implicit val codec: Codec[String, ReservationId, TextPlain] =
      Codec.int.map(ReservationId(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class AccountId(value: Int)

  object AccountId {
    implicit val doobieRead: Read[AccountId] = Read[Int].map(AccountId(_))
    implicit val schema: Schema[AccountId] =
      Schema.schemaForInt.map(l => Some(AccountId(l)))(_.value)
    implicit val codec: Codec[String, AccountId, TextPlain] =
      Codec.int.map(AccountId(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class ProductId(value: Int)

  object ProductId {
    implicit val doobieRead: Read[ProductId] = Read[Int].map(ProductId(_))
    implicit val schema: Schema[ProductId] =
      Schema.schemaForInt.map(l => Some(ProductId(l)))(_.value)
    implicit val codec: Codec[String, ProductId, TextPlain] =
      Codec.int.map(ProductId(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class OrderId(value: Int)

  object OrderId {
    implicit val doobieRead: Read[OrderId] = Read[Int].map(OrderId(_))
    implicit val schema: Schema[OrderId] =
      Schema.schemaForInt.map(l => Some(OrderId(l)))(_.value)
    implicit val codec: Codec[String, OrderId, TextPlain] =
      Codec.int.map(OrderId(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class Amount(value: Int)

  object Amount {
    implicit val doobieRead: Read[Amount] = Read[Int].map(Amount(_))
    implicit val schema: Schema[Amount] =
      Schema.schemaForInt.map(l => Some(Amount(l)))(_.value)
    implicit val codec: Codec[String, Amount, TextPlain] =
      Codec.int.map(Amount(_))(_.value)
  }


  @derive(loggable, encoder, decoder)
  @newtype
  case class CreatedDate(value: Instant)

  object CreatedDate {
    implicit val doobieRead: Read[CreatedDate] =
      Read[Long].map(ts => CreatedDate(Instant.ofEpochMilli(ts)))
    implicit val schema: Schema[CreatedDate] = Schema.schemaForString.map(n =>
      Some(CreatedDate(Instant.parse(n)))
    )(_.value.toString)
  }


  object OperationType extends Enumeration {

    type OperationType = Value

    val refund, reservation, transfer_from, transfer_to, revenue, deposit = Value

    implicit val decoder: Decoder[OperationType.Value] = Decoder.decodeEnumeration(OperationType)
    implicit val encoder: Encoder[OperationType.Value] = Encoder.encodeEnumeration(OperationType)
    implicit val schema: Schema[OperationType.Value] = Schema.string[OperationType.Value]

    implicit val doobieRead: Read[OperationType.Value] = Read[Int].map(OperationType(_))
  }

  type IOWithRequestContext[A] = ReaderT[IO, RequestContext, A]
}


