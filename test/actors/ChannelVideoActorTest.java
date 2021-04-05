package actors;

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import models.GoogleAPIInterface;
import models.GoogleAPITestMock;
import models.Record;
import models.Video;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

/**
 * Unit test class for ChannelVideoActor
 *
 * @author Rui Li
 */
public class ChannelVideoActorTest {

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

  /**
   * Sets up.
   *
   * @throws Exception the exception
   * @author Rui Li
   */
  @BeforeClass
  public static void setUp() throws Exception {
    system = ActorSystem.create();
    testApp = new GuiceApplicationBuilder()
        .overrides(bind(GoogleAPIInterface.class).to(GoogleAPITestMock.class))
        .build();
    testAPI = testApp.injector().instanceOf(GoogleAPIInterface.class);
  }

  /**
   * Tear down.
   *
   * @throws Exception the exception
   * @author Rui Li
   */
  @AfterClass
  public static void tearDown() throws Exception {
    TestKit.shutdownActorSystem(system);
    system = null;
  }

  /**
   * unit test for searching channel videos
   *
   * @throws ExecutionException   the execution exception
   * @throws InterruptedException the interrupted exception
   * @throws IOException          the io exception
   * @author Rui Li
   */
  @Test
  public void searchChannelVideo() throws ExecutionException, InterruptedException, IOException {
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
    final TestKit probe = new TestKit(system);
    final ActorRef channelVideoActor = system.actorOf(ChannelVideoActor.getProps(testAPI));

    String channelID = "UCKo-NbWOxnxBnU41b-AoKeA";
    int sortingTypeInt = 1;
    String queryStr = "abc";
    channelVideoActor.tell(new ChannelVideoActor.ChannelMsg(channelID, sortingTypeInt, queryStr),
        probe.getRef());
    ArrayList<Record> result = probe.expectMsgClass(ArrayList.class);

    assertEquals(testVideo.toString(), result.get(0).getVideos().get(0).toString());

    sortingTypeInt = 0;
    channelVideoActor.tell(new ChannelVideoActor.ChannelMsg(channelID, sortingTypeInt, queryStr),
        probe.getRef());
    ArrayList<Record> result2 = probe.expectMsgClass(ArrayList.class);

    assertEquals(testVideo.toString(), result2.get(0).getVideos().get(1).toString());
  }

}