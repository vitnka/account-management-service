package repository

import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import domain.OperationType.OperationType
import domain._
import domain.error.DepositAlreadyExists
import doobie._
import doobie.implicits._
import dto.{DepositRequest, Operation, OperationResponse}

import java.time.Instant

trait OperationRepository {
  def getOperationHistory(accountId: AccountId): ConnectionIO[List[Operation]]
  def listAll: ConnectionIO[List[Operation]]
  def create(create: DepositRequest): ConnectionIO[Either[DepositAlreadyExists, OperationResponse]]
  def allRevenueOperationsGroupedByProduct(productId: ProductId, operation: OperationType): ConnectionIO[List[Operation]]
}
object OperationRepository {
  object sqls {
    def listAllSql: Query0[Operation] = sql"select * from operations".query[Operation]
    def createSql(deposit: DepositRequest): Update0 = sql"insert into operations (account_id, amount, operation_type) values (${deposit.accountId.value}, ${deposit.amount.value}, ${OperationType.deposit.toString} )".update
    def allRevenueOperationsSql(productId: ProductId, operation: OperationType): Query0[Operation] =
      sql"select products.name, sum(amount) from operations inner join products on operations.product_id = ${productId.value} where operation_type = ${operation.toString} group by products.name".query[Operation]
    def findByAmountAndAccountId(accountId: AccountId, amount: Amount): Query0[Operation] =
      sql"select * from operations where account_id=${accountId.value} and amount=${amount.value}"
        .query[Operation]

    def allOperationByAccountIdSql(accountId: AccountId): Query0[Operation] =
      sql"select * from operations where account_id=${accountId.value}".query[Operation]
  }

  private final class Impl extends OperationRepository {
    import sqls._

    override def listAll: ConnectionIO[List[Operation]] =
      listAllSql.to[List]

    override def allRevenueOperationsGroupedByProduct(productId: ProductId, operation: OperationType): ConnectionIO[List[Operation]] = {
      allRevenueOperationsSql(productId, operation).to[List]
    }

    override def create(deposit: DepositRequest): ConnectionIO[Either[DepositAlreadyExists, OperationResponse]] =
      findByAmountAndAccountId(deposit.accountId, deposit.amount).option.flatMap {
        case None =>
          createSql(deposit)
            .withUniqueGeneratedKeys[OperationId]("id")
            .map(id =>
              OperationResponse(deposit.accountId, deposit.amount, CreatedDate.apply(Instant.now())).asRight[DepositAlreadyExists]
            )
        case Some(_) => DepositAlreadyExists().asLeft[OperationResponse].pure[ConnectionIO]
      }

    override def getOperationHistory(accountId: AccountId): ConnectionIO[List[Operation]] = {
      allOperationByAccountIdSql(accountId).to[List]
    }
  }

  def make(): OperationRepository = new Impl
}
