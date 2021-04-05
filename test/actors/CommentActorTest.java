package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.google.api.services.youtube.model.*;
import akka.japi.Pair;
import models.GoogleAPIInterface;
import models.GoogleAPITestMock;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;

/**
 * Unit test class for StatisticActor
 *
 * @author Junwei Zhang
 */
public class CommentActorTest {

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
     * @author Junwei Zhang
     */
    @BeforeClass
    public static void setUp() {
        system = ActorSystem.create();
        testApp = new GuiceApplicationBuilder()
                .overrides(bind(GoogleAPIInterface.class).to(GoogleAPITestMock.class))
                .build();
        mockGoogleAPI = testApp.injector().instanceOf(GoogleAPIInterface.class);
        //videoComments.setApi(mockGoogleAPI);

    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     * @author Junwei Zhang
     */
    public static void tearDown() throws Exception {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * unit test for positive comments list
     *
     * @author Junwei Zhang
     */
    @Test
    public void testpos() {
        final TestKit probe = new TestKit(system);
        final ActorRef commentActor = system.actorOf(CommentActor.getProps(mockGoogleAPI));
        commentActor.tell(new CommentActor.CommentID("kobe"), probe.getRef());
        CommentActor.CommentResponse result = probe
                .expectMsgClass(CommentActor.CommentResponse.class);


        assertEquals(result.getPair(), new akka.japi.Pair<>("kobe", 1));

    }

    /**
     * unit test for negative comments list
     *
     * @author Junwei Zhang
     */
    @Test
    public void testneg() {
        final TestKit probe = new TestKit(system);
        final ActorRef commentActor = system.actorOf(CommentActor.getProps(mockGoogleAPI));
        commentActor.tell(new CommentActor.CommentID("sad"), probe.getRef());
        CommentActor.CommentResponse result = probe
                .expectMsgClass(CommentActor.CommentResponse.class);


        assertEquals(result.getPair(), new akka.japi.Pair<>("sad", -1));
    }

    /**
     * unit test for neutral comments list
     *
     * @author Junwei Zhang
     */
    @Test
    public void testneu() {
        final TestKit probe = new TestKit(system);
        final ActorRef commentActor = system.actorOf(CommentActor.getProps(mockGoogleAPI));
        commentActor.tell(new CommentActor.CommentID("neu"), probe.getRef());
        CommentActor.CommentResponse result = probe
                .expectMsgClass(CommentActor.CommentResponse.class);


        assertEquals(result.getPair(), new akka.japi.Pair<>("neu", 0));
    }

    /**
     * unit test for comments unavailable list
     *
     * @author Junwei Zhang
     */
    @Test
    public void testnon() {
        final TestKit probe = new TestKit(system);
        final ActorRef commentActor = system.actorOf(CommentActor.getProps(mockGoogleAPI));
        commentActor.tell(new CommentActor.CommentID("non"), probe.getRef());
        CommentActor.CommentResponse result = probe
                .expectMsgClass(CommentActor.CommentResponse.class);

        assertEquals(result.getPair(), new akka.japi.Pair<>("non", 0));
    }

}
