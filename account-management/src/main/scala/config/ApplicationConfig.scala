package config

import pureconfig.generic.semiauto._
import pureconfig.ConfigReader
import cats.effect.IO
import pureconfig.ConfigSource

final case class ApplicationConfig(db: DbConfig, server: ServerConfig)
object ApplicationConfig {
  implicit val reader: ConfigReader[ApplicationConfig] = deriveReader

  def load: IO[ApplicationConfig] =
    IO.delay(ConfigSource.default.loadOrThrow[ApplicationConfig])
}

final case class DbConfig(
                           url: String,
                           driver: String,
                           user: String,
                           password: String
                         )
object DbConfig {
  implicit val reader: ConfigReader[DbConfig] = deriveReader
}

final case class ServerConfig(host: String, port: Int)
object ServerConfig {
  implicit val reader: ConfigReader[ServerConfig] = deriveReader
}
