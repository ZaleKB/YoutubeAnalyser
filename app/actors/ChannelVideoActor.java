package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import models.GoogleAPIInterface;
import models.Record;
import models.Video;

/**
 * create a ChannelVideoActor to list the most 10 recent videos through the specific Channel
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class ChannelVideoActor extends AbstractActor {

  private SortingType sortingType;
  private GoogleAPIInterface googleAPI;
  private String queryStr;
  private ActorRef controllerActor;

  private ChannelVideoActor(GoogleAPIInterface googleAPI) {

    this.googleAPI = googleAPI;
  }

    /**
     * Create the ChannelVideoActor and bind it with the googleAPI
     *
     * @param googleAPI the google api
     * @return the props
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public static Props getProps(GoogleAPIInterface googleAPI) {
        return Props.create(ChannelVideoActor.class, googleAPI);
    }

    /**
     * match the incoming message to the specific category
     *
     * @return Receive the build message
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */

    /**
     * match the incoming message to the specific category
     *
     * @return Receive the build message
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(ChannelMsg.class, msg -> {
          // 0 means Ascending and 1 means Descending
          this.queryStr = msg.queryStr;
          if (msg.sortingTypeInt == 0) {
            sortingType = SortingType.ASCENDING;
          } else {
            sortingType = SortingType.DESCENDING;
          }
          controllerActor = getSender();
          CompletionStage<Object> searchResult = searchChannelVideo(msg.channelID);
          akka.pattern.Patterns.pipe(searchResult, getContext().dispatcher()).to(controllerActor);
        })
        .build();
  }

    /**
     * Search channel video through the video ID
     *
     * @param channelID the channel id
     * @return the completion stage
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public CompletionStage<Object> searchChannelVideo(String channelID) {
    CompletableFuture<SearchListResponse> futureVideoResponse =
        googleAPI.fetchVideosResponse(channelID, true);
    CompletableFuture<List<Video>> futureVideoList =
        futureVideoResponse.thenApplyAsync(this::extractVideosObj);
    CompletableFuture<String> futureVideoIDStr =
        futureVideoResponse.thenApply(response -> String.join(",", extractVideosID(response)));
    CompletableFuture<VideoListResponse> futureViewCounts =
        futureVideoIDStr.thenApplyAsync(googleAPI::fetchViewCountsResponse);
    return futureViewCounts.thenCombine(
        futureVideoList, (VideoListResponse response, List<Video> videos) -> {
          Record rec = new Record(channelID, this.updateListVC(response, videos));
          List<Record> result = new ArrayList<>();
          result.add(rec);
          return result;
        }
    );
  }

    /**
     * Extract the list of Videos from the videosResponse
     *
     * @param videosResponse the videos response
     * @return the list
     *
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
    List<Video> finalVideos;
    if (sortingType.equals(SortingType.ASCENDING)) {
      finalVideos = videos.stream()
          .sorted(Comparator.comparing(Video::getVideo_title, (v1, v2) -> {
                Boolean b1 = v1.contains(queryStr);
                Boolean b2 = v2.contains(queryStr);
                return b1.compareTo(b2);
              }
          ))
          .sorted(Comparator.comparing(Video::getLapsedTime).reversed())
          .collect(Collectors.toList());
    } else {
      finalVideos = videos.stream()
          .sorted(Comparator.comparing(Video::getVideo_title, (v1, v2) -> {
                Boolean b1 = v1.contains(queryStr);
                Boolean b2 = v2.contains(queryStr);
                return b1.compareTo(b2);
              }
          ))
          .sorted(Comparator.comparing(Video::getLapsedTime))
          .collect(Collectors.toList());

    }
    return finalVideos;
  }

    /**
     * Extract videos id list from the videoResponse.
     *
     * @param videosResponse the videos response
     * @return the list
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public List<String> extractVideosID(SearchListResponse videosResponse) {
    List<String> videoID = new ArrayList<>();
    videosResponse.getItems().forEach((i) -> videoID.add(i.getId().getVideoId()));
    return videoID;
  }

    /**
     * Update list vc list.
     *
     * @param vcResponse the vc response
     * @param videos     the videos
     * @return the list
     *
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

  private enum SortingType {
      /**
       * Ascending sorting type.
       */
      ASCENDING,
      /**
       * Descending sorting type.
       */
      DESCENDING
  }

    /**
     * The type Channel msg.
     */
    public static class ChannelMsg {

        /**
         * The Channel id.
         */
        public final String channelID;
        /**
         * The Sorting type int.
         */
        public int sortingTypeInt;
        /**
         * The Query str.
         */
        public String queryStr;

        /**
         * Instantiates a new Channel msg.
         *
         * @param channelID   the channel id
         * @param sortingType the sorting type
         * @param queryStr    the query str
         */
        public ChannelMsg(String channelID, int sortingType, String queryStr) {
          this.channelID = channelID;
          this.sortingTypeInt = sortingType;
          this.queryStr = queryStr;
        }
  }


}
