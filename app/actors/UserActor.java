package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Pair;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import models.GoogleAPIInterface;
import models.Record;
import models.Video;
import models.VideoComments;
import play.libs.Json;
import scala.concurrent.duration.Duration;

/**
 * Actor Class for the User behaviors such as searching videos, update videos and frequently
 * server-push.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class UserActor extends AbstractActorWithTimers {

  private final ActorRef ws;
  private GoogleAPIInterface googleAPI;
  private HashMap<String, Pair<String, List<String>>> history;

  /**
   * Instantiates a new User actor.
   *
   * @param ws        the ws
   * @param googleAPI the google api
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public UserActor(final ActorRef ws, GoogleAPIInterface googleAPI) {
    this.ws = ws;
    this.googleAPI = googleAPI;
    this.history = new HashMap<>();
  }

  /**
   * Singleton function to get the only ActorRef
   *
   * @param wsout     the wsout
   * @param googleAPI the google api
   *
   * @return the props
   */
  public static Props props(final ActorRef wsout, GoogleAPIInterface googleAPI) {
    return Props.create(UserActor.class, wsout, googleAPI);
  }

  /**
   * override preStart function for userActor
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Override
  public void preStart() throws Exception {
    super.preStart();
    System.out.println("useractor prestart");
    getTimers()
        .startTimerAtFixedRate("Timer", new Tick(),
            Duration.create(30, TimeUnit.SECONDS));
  }

  /**
   * override createReceive function for userActor, we defined the behaviors for user actor here.
   * Object Node class msg for searching new query string Tick class msg for updating all the
   * previews searching queries
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Override
  public Receive createReceive() {

    return receiveBuilder()
        .match(ObjectNode.class, (json) -> {
          this.searchNew(json.get("q").asText());
        })
        .match(Tick.class, msg -> {
          updateAll();
        })
        .build();
  }

  /**
   * Extract videos obj list.
   *
   * @param videosResponse the videos response
   *
   * @return the list
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<Video> extractVideosObj(SearchListResponse videosResponse) {
    List<Video> videos = new ArrayList<>();
    videosResponse.getItems().forEach((i) -> videos.add(
        new Video(
            i.getSnippet().getTitle(),
            i.getSnippet().getChannelTitle(),
            i.getSnippet().getPublishedAt(),
            i.getId().getVideoId(),
            i.getSnippet().getChannelId())
    ));
    return videos;
  }

  /**
   * Extract videos id list.
   *
   * @param videosResponse the videos response
   *
   * @return the list
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<String> extractVideosID(SearchListResponse videosResponse) {
    List<String> videoID = new ArrayList<>();
    videosResponse.getItems().forEach((i) -> videoID.add(i.getId().getVideoId()));
    return videoID;
  }

  /**
   * Fetch sentiment list.
   *
   * @param videoID the video id
   *
   * @return the list
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<Pair<String, Integer>> fetchSentiment(List<String> videoID) {
    List<CompletableFuture<Pair<String, Integer>>> sentimentFutures = videoID.stream()
        .map(id -> CompletableFuture.supplyAsync(() -> {
          VideoComments videoComments = new VideoComments();
          try {
            return videoComments.processComment(id);
          } catch (GeneralSecurityException | IOException e) {
//            e.printStackTrace();
            return new Pair<>(id, 0);
          }
        }))
        .collect(Collectors.toList());
    return sentimentFutures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());
  }


  /**
   * Update sentiment list.
   *
   * @param sentiment the sentiment
   * @param videos    the videos
   *
   * @return the list
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<Video> updateSentiment(List<Pair<String, Integer>> sentiment, List<Video> videos) {
    sentiment
        .forEach(
            (pair) -> videos.stream()
                .filter(j -> j.getVideoID().equals(pair.first()))
                .forEach(video -> video.setSentiment(pair.second()))
        );
    return videos;
  }

  /**
   * Update list vc list.
   *
   * @param vcResponse the vc response
   * @param videos     the videos
   *
   * @return the list
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<Video> updateListVC(VideoListResponse vcResponse, List<Video> videos) {
    vcResponse
        .getItems()
        .forEach(
            (i) -> videos.stream()
                .filter(j -> j.getVideoID().equals(i.getId()))
                .forEach(k -> k.setView_count(i.getStatistics().getViewCount().longValue()))
        );
    return videos;
  }


  /**
   * function for searching new query and return the response to the websocket
   *
   * @param query query String
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  private void searchNew(String query) {
    String queryID = LocalTime.now().toString();
    ArrayList<String> iniList = new ArrayList<>();

    videoSearch(query).thenAccept(rec -> {
      rec.getVideos().forEach(v -> iniList.add(v.getVideoID()));
      final ObjectNode response = Json.newObject();
      response.put("youtube", rec.toString().replace("!addQueryIDHere!", queryID)
          .replaceAll("!addQuqueryStrHere!", query));
      response.put("type", "newSearch");
      response.put("queryID", queryID);
//      System.out.println("newSearch" + response);
      ws.tell(response, self());
    });
    history.put(queryID, new Pair<>(query, iniList));
  }


  /**
   * function for update all the history queries and return the new coming data to the websocket
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  private void updateAll() {
    System.out.println("checking update after 30s:");

    // replace history with new records
    history.replaceAll((queryID, queryPair) -> {
      System.out.println("queryList[" + queryPair.first() + "][before]:" + queryPair.second());
      // get historic videoIDs from the cache
      ArrayList<String> currHistoryList = (ArrayList<String>) queryPair.second();
      // re-search queryStr then filter out the duplicated records
      videoSearch(queryPair.first()).thenAccept(rec -> {
        ArrayList<Video> updateVideoList = (ArrayList<Video>) rec.getVideos().stream()
            .filter(video -> !currHistoryList.contains(video.getVideoID()))
            .collect(Collectors.toList());

        // generate html string
        String updateVideoString = updateVideoList.stream().map(Video::toString)
            .collect(Collectors.joining());

        // add new items to history
        currHistoryList
            .addAll(updateVideoList.stream().map(Video::getVideoID).collect(Collectors.toList()));

        System.out.println("queryList[" + queryPair.first() + "][after]:" + currHistoryList);

        // tag json object as update flow
        final ObjectNode response = Json.newObject();
        response.put("youtube", updateVideoString);
        response.put("type", "update");
        response.put("queryID", queryID);
//        System.out.println(response);
        ws.tell(response, self());
      });
      // generate new pair which is the new history record for current query block
      return new Pair<>(queryPair.first(), currHistoryList);
    });

  }

  /**
   * pipeline function for searching videos using google API
   *
   * @param query query String
   *
   * @return the Record
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  private CompletableFuture<Record> videoSearch(String query) {

    CompletableFuture<SearchListResponse> futureVideoResponse =
        googleAPI.fetchVideosResponse(query, false);

    CompletableFuture<List<Video>> futureVideoList =
        futureVideoResponse.thenApplyAsync(this::extractVideosObj);

    CompletableFuture<List<String>> futureVideoIDStr =
        futureVideoResponse.thenApply(this::extractVideosID);

    CompletableFuture<VideoListResponse> futureViewCounts =
        futureVideoIDStr.thenApplyAsync(videoIDList -> {
          String videoIDString = String.join(",", videoIDList);
          return googleAPI.fetchViewCountsResponse(videoIDString);
        });

    CompletableFuture<List<Pair<String, Integer>>> futureSentiments =
        futureVideoIDStr.thenApplyAsync(this::fetchSentiment);

    CompletableFuture<List<Video>> futureFinalVideoList = futureSentiments.thenCombine(
        futureVideoList,
        (pair, videos) -> videos = updateSentiment(pair, videos));

    return futureViewCounts.thenCombine(
        futureFinalVideoList, (VideoListResponse response, List<Video> videos) -> new Record(query,
            this.updateListVC(response, videos))
    );


  }

  /**
   * msg class Tick for server push functions
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public static final class Tick {

  }
}
