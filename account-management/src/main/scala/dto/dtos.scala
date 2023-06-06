package dto

import derevo.circe.{decoder, encoder}
import derevo.derive
import domain.OperationType.OperationType
import domain._
import sttp.tapir.Schema

@derive(encoder, decoder)
final case class Operation(id: OperationId, account_id: AccountId, productId: ProductId,
                           orderId: OrderId, amount: Amount, createdDate: CreatedDate)

@derive(encoder, decoder)
final case class Reservation(id: OperationId, account_id: AccountId, amount: Amount,
                             createdDate: CreatedDate, productId: ProductId, orderId: OrderId,
                             description: String, operationType: OperationType)
@derive(encoder, decoder)
final case class OperationResponse(account_id: AccountId, amount: Amount, time: CreatedDate)

@derive(encoder, decoder)
final case class DepositRequest(amount: Amount, accountId: AccountId)

@derive(encoder, decoder)
final case class ReservationRequest(amount: Amount, accountId: AccountId, productId: ProductId, orderId: OrderId)

@derive(encoder, decoder)
final case class DefaultResponse(message: String)

@derive(encoder, decoder)
final case class Product(productId: ProductId, name: String)

@derive(encoder, decoder)
final case class Account(account_id: AccountId, balance: Int, createdDate: CreatedDate)


object Operation {
  implicit val schema: Schema[Operation] = Schema.derived
}

object Reservation {
  implicit val schema: Schema[Reservation] = Schema.derived
}

object Product {
  implicit val schema: Schema[Product] = Schema.derived
}

object Account {
  implicit val schema: Schema[Account] = Schema.derived
}
