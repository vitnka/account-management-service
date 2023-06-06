package controller

import cats.effect.IO
import service.ReservationService
import sttp.tapir.server.ServerEndpoint

trait ReservationController {
  def createReservation: ServerEndpoint[Any, IO]
  def revenueReservation: ServerEndpoint[Any, IO]
  def refundReservation: ServerEndpoint[Any, IO]
  def all: List[ServerEndpoint[Any, IO]]
}

object ReservationController {
  final private class Impl(service: ReservationService) extends ReservationController {

    override val createReservation: ServerEndpoint[Any, IO] =
      endpoints.createReservation.serverLogic { case (ctx, request) =>
        service.createDeposit(request).run(ctx)
      }

    override val revenueReservation: ServerEndpoint[Any, IO] =
      endpoints.revenueReservation.serverLogic { case (ctx, request) =>
        service.revenueReservation(request).run(ctx)
      }

    override val refundReservation: ServerEndpoint[Any, IO] =
      endpoints.refundReservation.serverLogic { case (ctx, orderId) =>
        service.refundReservation(orderId).run(ctx)
      }


    override val all: List[ServerEndpoint[Any, IO]] = List(
      createReservation,
      revenueReservation,
      refundReservation
    )
  }

  def make(service: ReservationService): ReservationController = new Impl(service)
}

