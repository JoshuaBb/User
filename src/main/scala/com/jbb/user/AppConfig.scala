package com.jbb.user

import com.comcast.ip4s.{Host, Port}
import pureconfig.*
import cats.implicits.*
import pureconfig.error.{CannotConvert, ConfigReaderFailures, ConvertFailure}

case class Config(app: AppConfig)
case class AppConfig(server: ServerConfig)
case class ServerConfig(host: Host, port: Port)


// Not sure that there is a better way to do this, but would like to load the configs
// before even starting the application
object Config:
  given ConfigReader[Config] = ConfigReader.forProduct1("app")(Config.apply)

object AppConfig:
  given ConfigReader[AppConfig] = ConfigReader.forProduct1("server")(AppConfig.apply)


object ServerConfig:
  given ConfigReader[Port] with {
    def from(cur: ConfigCursor): Either[ConfigReaderFailures, Port] =
      cur.asInt.flatMap { portNumber =>
        Port.fromInt(portNumber) match {
          case None =>
            Left(ConfigReaderFailures(
              head = ConvertFailure(CannotConvert(portNumber.toString, "Port", s"number is not between ${Port.MinValue} and ${Port.MaxValue}"), cur)
            ))
          case Some(port) =>
            Right(port)
        }

      }
  }

  // Can be improved to take hostName or ipAddress
  given ConfigReader[Host] with {
    def from(cur: ConfigCursor): Either[ConfigReaderFailures, Host] =
      cur.asString.flatMap { hostName =>
        Host.fromString(hostName) match {
          case None =>
            Left(ConfigReaderFailures(
              head = ConvertFailure(CannotConvert(hostName, "HostName", s"Host Name value doesn't meet regex pattern"), cur)
            ))
          case Some(host) =>
            Right(host)
        }
      }
  }


  given ConfigReader[ServerConfig] = ConfigReader.forProduct2("host", "port")(ServerConfig.apply)
