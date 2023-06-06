import cats.data.ReaderT
import cats.effect.kernel.Resource
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s._
import config.ApplicationConfig
import controller.{AccountController, ReservationController}
import domain.{IOWithRequestContext, RequestContext}
import doobie.util.transactor.Transactor
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router
import repository.{OperationRepository, ReservationRepository}
import service.{OperationService, ReservationService}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tofu.logging.Logging

object Server extends IOApp {

  private val mainLogs =
    Logging.Make.plain[IO].byName("Main")

  override def run(args: List[String]): IO[ExitCode] =
    (for {
      _ <- Resource.eval(mainLogs.info("Starting account-management service..."))
      config <- Resource.eval(ApplicationConfig.load)
      transactor = Transactor
        .fromDriverManager[IO](
          config.db.driver,
          config.db.url,
          config.db.user,
          config.db.password
        ).mapK[IOWithRequestContext](ReaderT.liftK[IO, RequestContext])

      // repository
      operationRepository = OperationRepository.make()
      reservationRepository = ReservationRepository.make()

      // Services
      operationService = OperationService.make(operationRepository, transactor)
      reservationService = ReservationService.make(reservationRepository, transactor)

      //Controllers
      reservationController = ReservationController.make(reservationService)
      accountController = AccountController.make(operationService)

      routes = Http4sServerInterpreter[IO]().toRoutes(accountController.all <+> reservationController.all)
      httpApp = Router("/api/v1/" -> routes).orNotFound

      _ <- EmberServerBuilder
        .default[IO]
        .withHost(
          Ipv4Address.fromString(config.server.host).getOrElse(ipv4"0.0.0.0")
        )
        .withPort(Port.fromInt(config.server.port).getOrElse(port"80"))
        .withHttpApp(httpApp)
        .build
    } yield ()).use(_ => IO.never).as(ExitCode.Success)
}