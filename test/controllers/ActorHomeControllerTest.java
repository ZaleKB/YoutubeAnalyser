package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.inject.Bindings.bind;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import models.GoogleAPIInterface;
import models.GoogleAPITestMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.netty.ws.NettyWebSocket;
import play.test.Helpers;
import play.test.TestServer;
import play.test.WithApplication;

/**
 * Unit test class for ActorHomeController
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class ActorHomeControllerTest extends WithApplication {

  /**
   * The System.
   */
  static ActorSystem system;
  /**
   * The Mock google api.
   */
  static GoogleAPIInterface mockGoogleAPI;
  /**
   * The Test app.
   */
  static Application testApp;

  /**
   * Set up.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
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
   * Teardown.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @AfterClass
  public static void teardown() {
    Helpers.start(testApp);
    TestKit.shutdownActorSystem(system);
    system = null;
  }


  @Override
  protected Application provideApplication() {
    return new GuiceApplicationBuilder().build();
  }

  /**
   * Test ws.
   */
  @Test
  public void testWs() {
    TestServer server = testServer(9001);
    running(server, () -> {
      try {
        AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
            .setMaxRequestRetry(0).build();
        AsyncHttpClient client = new DefaultAsyncHttpClient(config);
        WebSocketClient webSocketClient = new WebSocketClient(client);

        try {
          String serverURL = "ws://localhost:9001/ws";
          ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
          WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener(
              (message) -> {
                try {
                  queue.put(message);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              });
          CompletableFuture<NettyWebSocket> completionStage = webSocketClient
              .call(serverURL, serverURL, listener);
          NettyWebSocket searchResult = completionStage.get();
          assertTrue(searchResult != null);
        } finally {
          client.close();
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    });
  }

  /**
   * Test index.
   */
  @Test
  public void testIndex() {
    running(fakeApplication(), () -> {
      Call action = routes.ActorHomeController.index();
      Http.RequestBuilder fakeRequest = Helpers.fakeRequest(action);
      Result result = Helpers.route(app, fakeRequest);
      assertEquals(200, result.status());
    });
  }

  /**
   * Test profile.
   */
  @Test
  public void testProfile() {
    running(fakeApplication(), () -> {
      Call action = routes.ActorHomeController.profile();
      Http.RequestBuilder fakeRequest = Helpers.fakeRequest(action);
      Result result = Helpers.route(app, fakeRequest);
      assertEquals(200, result.status());
    });
  }

  /**
   * Test channel video.
   */
  @Test
  public void testChannelVideo() {
    running(fakeApplication(), () -> {
      Call action = routes.ActorHomeController.channelVideo();
      Http.RequestBuilder fakeRequest = Helpers.fakeRequest(action);
      fakeRequest
          .uri("/channelVideo?channelID=UCQTTe8puVKqurziI6Do-H-Q&sortingType=0&queryStr=linux");
      Result result = Helpers.route(app, fakeRequest);
      assertEquals(200, result.status());
    });
  }


}
