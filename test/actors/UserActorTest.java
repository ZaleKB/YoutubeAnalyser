package actors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;

import actors.UserActor.Tick;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import models.GoogleAPIInterface;
import models.GoogleAPITestMock;
import models.Video;
import models.VideoComments;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;

/**
 * Unit test class for UserActor
 *
 * @author Yuxuan Luan, Rui Li
 */
public class UserActorTest {

  /**
   * The System.
   */
  static ActorSystem system;
  /**
   * The Test app.
   */
  static Application testApp;
  /**
   * The Test api.
   */
  static GoogleAPIInterface testAPI;
  /**
   * The Video search result node.
   */
  static JsonNode videoSearchResultNode;
  /**
   * The Test video.
   */
  static Video testVideo;
  /**
   * The Video list response mock.
   */
  static VideoListResponse videoListResponseMock;
  /**
   * The Video list result node.
   */
  static JsonNode videoListResultNode;
  /**
   * The Video search result mock.
   */
  static SearchListResponse videoSearchResultMock;
  private static VideoComments videoComments;

  /**
   * Sets up.
   *
   * @throws Exception the exception
   * @author Yuxuan Luan, Rui Li
   */
  @BeforeClass
  public static void setUp() throws Exception {
    videoComments = mock(VideoComments.class);
    system = ActorSystem.create();
    testApp = new GuiceApplicationBuilder()
        .overrides(bind(GoogleAPIInterface.class).to(GoogleAPITestMock.class))
        .build();
    testAPI = testApp.injector().instanceOf(GoogleAPIInterface.class);

    videoSearchResultMock = new SearchListResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    File file = new File("test/models/videoSearchResponseTest.json");
    videoSearchResultNode = objectMapper.readTree(file);
    List<SearchResult> SearchResults = new ArrayList<>();
    videoSearchResultNode.get("items").forEach(item -> {
      SearchResult searchResult = new SearchResult();
      ResourceId resourceId = new ResourceId();
      resourceId.setVideoId(item.get("id").get("videoId").asText());
      searchResult.setId(resourceId);
      SearchResultSnippet resultSnippet = new SearchResultSnippet();
      resultSnippet.setPublishedAt(new DateTime(item.get("snippet").get("publishedAt").asText()));
      resultSnippet.setChannelId(item.get("snippet").get("channelId").asText());
      resultSnippet.setTitle(item.get("snippet").get("title").asText());
      resultSnippet.setChannelTitle(item.get("snippet").get("channelTitle").asText());
      searchResult.setSnippet(resultSnippet);
      SearchResults.add(searchResult);
      testVideo = new Video(
          item.get("snippet").get("title").asText(),
          item.get("snippet").get("channelTitle").asText(),
          new DateTime(item.get("snippet").get("publishedAt").asText()),
          item.get("id").get("videoId").asText(),
          item.get("snippet").get("channelId").asText()
      );
    });

    videoSearchResultMock.setItems(SearchResults);

    videoListResponseMock = new VideoListResponse();
    List<com.google.api.services.youtube.model.Video> countsResults = new ArrayList<>();
    File file2 = new File("test/models/videoCountsResponseTest.json");
    videoListResultNode = objectMapper.readTree(file2);
    videoListResultNode.get("items").forEach(item -> {
      com.google.api.services.youtube.model.Video video = new com.google.api.services.youtube.model.Video();
      VideoStatistics statistics = new VideoStatistics();
      statistics.setViewCount(BigInteger.valueOf(item.get("statistics").get("viewCount").asLong()));
      video.setStatistics(statistics);
      video.setId(item.get("id").asText());
      testVideo.setView_count(item.get("statistics").get("viewCount").asLong());
      countsResults.add(video);
    });

    videoListResponseMock.setItems(countsResults);

    Pair<String, Integer> testPair = new Pair<>("UCKo-NbWOxnxBnU41b-AoKeA", 0);
    when(videoComments.processComment("UCKo-NbWOxnxBnU41b-AoKeA")).thenReturn(testPair);

  }

  /**
   * Tear down.
   *
   * @throws Exception the exception
   */
  @AfterClass
  public static void tearDown() throws Exception {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  /**
   * Test search video.
   */
  @Test
  public void testSearchVideo() {

    final TestKit probe = new TestKit(system);
    final ActorRef userActor = system.actorOf(UserActor.props(probe.getRef(), testAPI));
    ObjectNode node = Json.newObject();
    node.put("q", "surfing");
    userActor.tell(node, probe.getRef());
    ObjectNode response = probe.expectMsgClass(ObjectNode.class);
    assertEquals(response.get("type").textValue(), "newSearch");

    userActor.tell(new Tick(), probe.getRef());
    ObjectNode response2 = probe.expectMsgClass(ObjectNode.class);
    assertEquals(response2.get("type").textValue(), "update");

  }

//  @Test
//  public void testUpdateVideo() {
//
//    final TestKit probe = new TestKit(system);
//    final ActorRef userActor = system.actorOf(UserActor.props(probe.getRef(), testAPI));
//    userActor.tell(new Tick(), probe.getRef());
//    ObjectNode response = probe.expectMsgClass(ObjectNode.class);
//    assertEquals(response.get("type").textValue(), "update");
//
//  }

}
