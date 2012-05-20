import scala.actors._
import Actor._
import akka.actor._
import akka.pattern._
import akka.util.duration._
import akka.dispatch
import akka.dispatch.Await
import akka.util.Timeout
import akka.dispatch.Future
import akka.dispatch.OnFailure

abstract class Work {
  def perform(): Any
}

class SumSequence(start: Int, stop: Int) extends Work {
  def perform(): Int = {
    require(start >= 0)
    (start to stop).sum 
  }
}

class ComputeActor extends akka.actor.Actor {

  def computeSquare(i: Int) = i * i
  def computeLength(s: String) = s.length()

  def receive = {
    case i: Int => sender ! computeSquare(i)
    case s: String => sender ! computeLength(s); 
    case w: Work => {
      try {
    	  sender ! w.perform()        
      }
      catch {
        case ex: IllegalArgumentException => // Let user rely on timeout
      }
    }
  }
}

object Main extends App {
  // Create Actor
  implicit val system = ActorSystem()
  val computeActor = system.actorOf(Props(new ComputeActor))
  
  // Blocking future
  val future = computeActor.ask(2)(2000 millis)
  val result = Await.result(future, 4 seconds) // Blocks
  println("Future 1 result: " + result)
  
  // Non-blocking future
  implicit val timeout: Timeout = 2 seconds
  val future2 = computeActor ? "Hello" // "?" alias for ask(), timeout via implicit 
  future2.onSuccess { // non-blocking
    case result => println("Future 2 result: " + result)
  }

  // Sequence of futures 
  // Define a sequence of two futures, and transform sequence within a Akka Future structure
  val computeFutures = Seq((computeActor ? new SumSequence(0, 5)).mapTo[Int], (computeActor ? new SumSequence(0, 10)).mapTo[Int])
  val futureComputations = Future.sequence(computeFutures)
  futureComputations.onSuccess { case result => println("Sum of computations: " + result.sum)}
  futureComputations.onFailure { case _: AskTimeoutException => println("One or more tasks failed") }
  
  // Let onSuccess() or onFailure() trigger before shutdown
  Await.result(futureComputations, 1 seconds)
  system.shutdown()
}