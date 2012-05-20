
import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import akka.testkit.TestKit
import akka.testkit.ImplicitSender
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef

@RunWith(classOf[JUnitRunner])
class Test extends TestKit(ActorSystem())
  with ImplicitSender
  with Specification {

  "ComputeActor" should {
    "multiply integers with two" in {
      val system = ActorSystem()
      val computeActor = system.actorOf(Props(new ComputeActor))
      computeActor ! 2
      expectMsg(4)
      computeActor ! 8
      expectMsg(64)
      done
    }
  }

  "ComputeActor" should {
    "compute square" in {
      val computeActor = TestActorRef(Props[ComputeActor])(system)
      val underlying: ComputeActor = computeActor.underlyingActor
      underlying.computeSquare(2) must beEqualTo(4)
      underlying.computeLength("hello") must beEqualTo(5)
      done
    }
  }
}