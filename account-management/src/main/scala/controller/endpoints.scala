package controller

import domain._
import domain.error.ServiceError
import dto.{DefaultResponse, DepositRequest, OperationResponse, ReservationRequest}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

object endpoints {
  
  val createDeposit: PublicEndpoint[(RequestContext, DepositRequest), ServiceError, OperationResponse, Any] =
    endpoint.post
      .in("account" / "deposit")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[ServiceError])
      .in(jsonBody[DepositRequest])
      .out(jsonBody[OperationResponse])

  val getOperationHistory: PublicEndpoint[(RequestContext, AccountId), ServiceError, List[OperationResponse], Any] =
    endpoint.get
      .in("account" / "history")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[ServiceError])
      .in(jsonBody[AccountId])
      .out(jsonBody[List[OperationResponse]])

  val createReservation: PublicEndpoint[(RequestContext, ReservationRequest), ServiceError, DefaultResponse, Any] =
    endpoint.post
      .in("reservations" / "create")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[ServiceError])
      .in(jsonBody[ReservationRequest])
      .out(jsonBody[DefaultResponse])

  val revenueReservation: PublicEndpoint[(RequestContext, ReservationRequest), ServiceError, DefaultResponse, Any] =
    endpoint.post
      .in("reservations" / "revenue")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[ServiceError])
      .in(jsonBody[ReservationRequest])
      .out(jsonBody[DefaultResponse])

  val refundReservation: PublicEndpoint[(RequestContext, OrderId), ServiceError, DefaultResponse, Any] =
    endpoint.post
      .in("reservations" / "refund")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[ServiceError])
      .in(jsonBody[OrderId])
      .out(jsonBody[DefaultResponse])
}
