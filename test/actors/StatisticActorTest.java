package actors;

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.GoogleAPIInterface;
import models.GoogleAPITestMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

/**
 * Unit test class for StatisticActor
 *
 * @author Yuxuan Luan
 */
public class StatisticActorTest {

  /**
   * The Test app.
   */
  static Application testApp;
  /**
   * The Mock google api.
   */
  static GoogleAPIInterface mockGoogleAPI;

  private static ActorSystem system;

  /**
   * Sets up.
   *
   * @author Yuxuan Luan
   */
  @BeforeClass
  public static void setUp() {
    system = ActorSystem.create();
    testApp = new GuiceApplicationBuilder()
        .overrides(bind(GoogleAPIInterface.class).to(GoogleAPITestMock.class))
        .build();
    mockGoogleAPI = testApp.injector().instanceOf(GoogleAPIInterface.class);
  }

  /**
   * Tear down.
   *
   * @throws Exception the exception
   * @author Yuxuan Luan
   */
  @AfterClass
  public static void tearDown() throws Exception {
    TestKit.shutdownActorSystem(system);
    system = null;
  }


  /**
   * unit test for statistic functions
   *
   * @author Yuxuan Luan
   */
  @Test
  public void testqSim() {
    final TestKit probe = new TestKit(system);
    final ActorRef statisticActor = system.actorOf(StatisticActor.props(mockGoogleAPI));
    statisticActor.tell(new StatisticActor.StaticQuery("Java"), probe.getRef());
    StatisticActor.StaticResponse result = probe
        .expectMsgClass(StatisticActor.StaticResponse.class);

    List<Map.Entry<String, Integer>> expected = new ArrayList<>();
    expected.add(new AbstractMap.SimpleEntry<String, Integer>("java", 6));
    expected.add(new AbstractMap.SimpleEntry<String, Integer>("intro", 2));
    expected.add(new AbstractMap.SimpleEntry<String, Integer>("to", 1));
    expected.add(new AbstractMap.SimpleEntry<String, Integer>("hello", 1));
    expected.add(new AbstractMap.SimpleEntry<String, Integer>("basic", 1));

    assertEquals(result.getList(), expected);

  }
}
