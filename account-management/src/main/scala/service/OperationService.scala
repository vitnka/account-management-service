package service

import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxEitherId, toBifunctorOps}
import domain.error.{InternalError, ServiceError}
import domain.{AccountId, IOWithRequestContext}
import doobie.Transactor
import doobie.implicits._
import dto.{DepositRequest, Operation, OperationResponse}
import repository.OperationRepository


trait OperationService {
  def getOperationHistory(accountId: AccountId) : IOWithRequestContext[Either[InternalError, List[Operation]]]
  def createDeposit(request: DepositRequest): IOWithRequestContext[Either[ServiceError, OperationResponse]]

}

object OperationService {

  private final class OperationServiceImpl(operationRepository: OperationRepository,  transactor: Transactor[IOWithRequestContext]) extends OperationService {
    override def getOperationHistory(accountId: AccountId): IOWithRequestContext[Either[InternalError, List[Operation]]] = {
      operationRepository.listAll.transact(transactor).attempt.map(_.leftMap(InternalError))
    }

    override def createDeposit(request: DepositRequest): IOWithRequestContext[Either[ServiceError, OperationResponse]] = {
      operationRepository.create(request).transact(transactor).attempt.map {
        case Left(th) => InternalError(th).asLeft[OperationResponse]
        case Right(Left(error)) => error.asLeft[OperationResponse]
        case Right(Right(operation)) => operation.asRight[ServiceError]
      }
    }
  }

  def make(operationRepository: OperationRepository,  transactor: Transactor[IOWithRequestContext]): OperationService =
    new OperationServiceImpl(operationRepository, transactor)
}