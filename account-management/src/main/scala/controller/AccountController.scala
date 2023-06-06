package controller

import cats.effect.IO
import service.{OperationService}
import sttp.tapir.server.ServerEndpoint

trait AccountController {
  def createDeposit: ServerEndpoint[Any, IO]
  def getOperationHistory: ServerEndpoint[Any, IO]
  def all: List[ServerEndpoint[Any, IO]]
}


object AccountController {
  final private class Impl(operationService: OperationService) extends AccountController {

    override val createDeposit: ServerEndpoint[Any, IO] =
      endpoints.createDeposit.serverLogic { case (ctx, request) =>
        operationService.createDeposit(request).run(ctx)
      }

    override val getOperationHistory: ServerEndpoint[Any, IO] =
      endpoints.getOperationHistory.serverLogic { case (ctx, accountId) =>
        operationService.getOperationHistory(accountId).run(ctx)
      }

    override val all: List[ServerEndpoint[Any, IO]] = List(
      createDeposit,
      getOperationHistory
    )
  }

  def make(operationService: OperationService): AccountController = new Impl(operationService)
}

