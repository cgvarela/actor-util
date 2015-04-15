package im.actor.util.testing

import java.net.InetAddress

import akka.actor._
import akka.testkit._
import com.typesafe.config._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ FlatSpecLike, BeforeAndAfterAll, Matchers, Suite }
import org.specs2.SpecificationLike
import org.specs2.execute.Success
import org.specs2.specification.core.Fragments

object ActorSpecification {
  private[this] def defaultSystemName = "actor-server-test"

  def createSystem(systemName: String = defaultSystemName): ActorSystem = {
    createSystem(systemName, createConfig(systemName, ConfigFactory.empty()))
  }

  def createSystem(config: Config): ActorSystem = {
    createSystem(defaultSystemName, createConfig(defaultSystemName, config))
  }

  def createSystem(systemName: String, config: Config): ActorSystem = {
    ActorSystem(systemName, config)
  }

  def createConfig(systemName: String, initialConfig: Config): Config = {
    val maxPort = 65535
    val minPort = 1025
    val port = util.Random.nextInt(maxPort - minPort + 1) + minPort

    val host = InetAddress.getLocalHost.getHostAddress

    initialConfig
      .withFallback(
        ConfigFactory.parseString( s"""
          akka.remote.netty.tcp.port = $port
          akka.remote.netty.tcp.hostname = "$host"
          akka.cluster.seed-nodes = [ "akka.tcp://$systemName@$host:$port" ]
        """))
      .withFallback(ConfigFactory.load().getConfig("actor-server"))
  }
}

abstract class ActorSpecification(system: ActorSystem = {ActorSpecification.createSystem()})
  extends TestKit(system) with SpecificationLike {
  implicit def anyToSuccess[T](a: T): org.specs2.execute.Result = Success()

  override def map(fragments: => Fragments) =
    fragments ^ step(shutdownSystem())

  private def shutdownSystem(): Unit = TestKit.shutdownActorSystem(system)
}

abstract class ActorSuite(system: ActorSystem = {ActorSpecification.createSystem()})
  extends TestKit(system)
  with Suite
  with FlatSpecLike
  with BeforeAndAfterAll
  with Matchers
  with ScalaFutures {
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}

abstract class ActorFlatSuite(system: ActorSystem = {ActorSpecification.createSystem()}) extends ActorSuite(system)
