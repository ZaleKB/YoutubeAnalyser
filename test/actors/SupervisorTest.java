package actors;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.concurrent.TimeoutException;
import models.GoogleAPI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;


/**
 * Unit test class for Supervisor
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class SupervisorTest {

  /**
   * The System.
   */
  static ActorSystem system;

  /**
   * Set up.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @BeforeClass
  public static void setUp() {
    system = ActorSystem.create();
  }

  /**
   * Tear down.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @AfterClass
  public static void tearDown() {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  /**
   * unit test for supervisorActor.
   *
   * @throws TimeoutException     the timeout exception
   * @throws InterruptedException the interrupted exception
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void test() throws TimeoutException, InterruptedException {
    TestKit testprobe = new TestKit(system);
    ActorRef ws = mock(ActorRef.class);
    GoogleAPI api = mock(GoogleAPI.class);
    ObjectNode node = Json.newObject();
    ActorRef supervisor = system.actorOf(Supervisor.getProps(ws, api));
    supervisor.tell(node, ActorRef.noSender());

    ActorRef user = (ActorRef) Await.result(
        akka.pattern.Patterns.ask(supervisor, Props.create(UserActor.class, ws, api), 5000),
        Duration.create(3, "seconds")
    );
    user.tell(node, testprobe.getRef());
    assertFalse(user.isTerminated());

  }


}
