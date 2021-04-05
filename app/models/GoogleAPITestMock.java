package models;

import akka.japi.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadReplies;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is API is a mock GoogleAPI which is used at the test part
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class GoogleAPITestMock implements GoogleAPIInterface {

  private SearchListResponse videoSearchResultMock;
  private JsonNode videoSearchResultNode;
  private VideoComments videoComments;
  private VideoListResponse videoListResponseMock;
  private JsonNode videoListResultNode;
  //private VideoSearch mockVideoSearch;
  private Video testVideo;


  /**
   * Instantiates a new Google api test mock.
   *
   * @throws IOException the io exception
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public GoogleAPITestMock() throws IOException {
    videoSearchResultMock = new SearchListResponse();
//      googleAPI = mock(GoogleAPI.class);
//      videoComments = mock(VideoComments.class);
//      videoSearch = new VideoSearch();
//    mockVideoSearch = new VideoSearch();
//      mockVideoSearch.setApi(googleAPI);

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
      statistics
          .setViewCount(BigInteger.valueOf(item.get("statistics").get("viewCount").asLong()));
      video.setStatistics(statistics);
      video.setId(item.get("id").asText());
      testVideo.setView_count(item.get("statistics").get("viewCount").asLong());
      countsResults.add(video);
    });

    videoListResponseMock.setItems(countsResults);

    File file3 = new File("test/models/videoCommentResponseTest.json");
    videoListResultNode = objectMapper.readTree(file3);
  }

  /**
   * Mock fetch videos response completable future.
   *
   * @param query       the query
   * @param isChannelID the is channel id
   *
   * @return the completable future
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */

  @Override
  public CompletableFuture<SearchListResponse> fetchVideosResponse(String query,
      boolean isChannelID) {
    if (isChannelID) {
      return CompletableFuture.supplyAsync(() -> videoSearchResultMock);
    } else {
      return CompletableFuture.supplyAsync(() -> videoSearchResultMock);
    }
  }

  /**
   * Mock fetch view counts response video list response.
   *
   * @param videoIDString the video id string
   *
   * @return the video list response
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */

  @Override
  public VideoListResponse fetchViewCountsResponse(String videoIDString) {
    return videoListResponseMock;
  }

  /**
   * Mock fetch first 50 titles response completable future.
   *
   * @param query the query needed to be searched
   *
   * @return fqTitle    the list of total 100 video titles
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              inherit the exception from the previous service by getting
   *                                  Youtube API connection
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */

  @Override
  public CompletableFuture<SearchListResponse> firstHalfResponse1(String query)
      throws GeneralSecurityException, IOException {
    List<String> mockTitle1 = Arrays.asList(
        "Hello Java",
        "Java Java",
        "Java Intro"
    );
    List<SearchResult> result1 = new ArrayList<>();
    mockTitle1.forEach(title -> {
      SearchResult item = new SearchResult();
      item.setSnippet(new SearchResultSnippet());
      item.getSnippet().setTitle(title);
      result1.add(item);
    });
    SearchListResponse response1 = new SearchListResponse();
    response1.setItems(result1);
    return CompletableFuture.supplyAsync(() -> response1);
  }

  /**
   * Mock to first half response 2 completable future.
   *
   * @param query the query
   *
   * @return the completable future
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */

  @Override
  public CompletableFuture<SearchListResponse> firstHalfResponse2(String query)
      throws GeneralSecurityException, IOException {
    List<String> mockTitle1 = Arrays.asList(
        "Intro to Java",
        "Java Basic"
    );
    List<SearchResult> result2 = new ArrayList<>();
    mockTitle1.forEach(title -> {
      SearchResult item = new SearchResult();
      item.setSnippet(new SearchResultSnippet());
      item.getSnippet().setTitle(title);
      result2.add(item);
    });
    SearchListResponse response2 = new SearchListResponse();
    response2.setItems(result2);
    return CompletableFuture.supplyAsync(() -> response2);
  }

  /**
   * Mock to comment response comment thread list response.
   *
   * @param videoID the video id
   *
   * @return the comment thread list response
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */

  @Override
  public CommentThreadListResponse commentResponse(String videoID)
      throws GeneralSecurityException, IOException {

    List<Pair<String, String>> positive = Arrays
        .asList(new Pair<>("what day you are uploading ? every episode?",
                "Feel the Pain we all did \uD83D\uDE02")
            , new Pair<>("Is Kobe the greatest?", "Yes he is")
            , new Pair<>("People she loves\uD83D\uDE0E\uD83D\uDE0E", "Good")
        );

    List<Pair<String, String>> negative = Arrays
        .asList(new Pair<>("Are you sad?", "\uD83D\uDE41"),
            new Pair<>("cheer up", "Yeah!"),
            new Pair<>("Wake Up!", "\uD83D\uDE2A\uD83D\uDE41\uD83D\uDE15"));

    List<Pair<String, String>> neutral = Arrays
        .asList(new Pair<>("Are you sad?", "\uD83D\uDE41"),
            new Pair<>("cheer up", "Yeah!\uD83D\uDE0E"),
            new Pair<>("Wake Up!", "\uD83D\uDE2A"));

    List<Pair<String, String>> no_emo = Arrays
        .asList(new Pair<>("Are you sad?", "No"),
            new Pair<>("cheer up", "Yeah!"),
            new Pair<>("Wake Up!", "Sure"));

    List<String> text = new ArrayList<>();
    List<CommentThread> cthreads = new ArrayList<>();
    List<Comment> totalComments = new ArrayList<>();

    List<Pair<String, String>> test = null;

    switch (videoID) {
      case "kobe":
        test = positive;
        break;
      case "sad":
        test = negative;
        break;
      case "neu":
        test = neutral;
        break;
      case "non":
        test = no_emo;
        break;
    }

    test.forEach(c -> {
      CommentThread ct = new CommentThread();
      Comment com = new Comment();
      com.setSnippet(new CommentSnippet());
      com.getSnippet().setTextDisplay(c.first());
      ct.setSnippet(new CommentThreadSnippet());
      ct.getSnippet().setTopLevelComment(com);

      text.add(com.getSnippet().getTextDisplay());
      totalComments.add(com);
      //comments.add(com);
      cthreads.add(ct);
    });
    for (int i = 0; i < cthreads.size(); i++) {
      List<Comment> recom = new ArrayList<>();
      recom.add(new Comment());
      recom.get(0).setSnippet(new CommentSnippet());
      recom.get(0).getSnippet().setTextDisplay(test.get(i).second());
      cthreads.get(i).setReplies(new CommentThreadReplies().setComments(recom));
      for (Comment c : cthreads.get(i).getReplies().getComments()) {
        text.add(c.getSnippet().getTextDisplay());
      }

    }
    CommentThreadListResponse comResponse = new CommentThreadListResponse();
    comResponse.setItems(cthreads);
    return comResponse;
  }

//  @Override
//  public CommentThreadListResponse commentForTest(List<Pair<String, String>> videoID)
//      throws GeneralSecurityException, IOException {
//    //List<Comment> comments = new ArrayList<>();
//
//    List<String> text = new ArrayList<>();
//    List<CommentThread> cthreads = new ArrayList<>();
//    List<Comment> totalComments = new ArrayList<>();
//
//    videoID.forEach(c -> {
//      CommentThread ct = new CommentThread();
//      Comment com = new Comment();
//      com.setSnippet(new CommentSnippet());
//      com.getSnippet().setTextDisplay(c.getKey());
//      ct.setSnippet(new CommentThreadSnippet());
//      ct.getSnippet().setTopLevelComment(com);
//
//      text.add(com.getSnippet().getTextDisplay());
//      totalComments.add(com);
//      //comments.add(com);
//      cthreads.add(ct);
//    });
//    for (int i = 0; i < cthreads.size(); i++) {
//      List<Comment> recom = new ArrayList<>();
//      recom.add(new Comment());
//      recom.get(0).setSnippet(new CommentSnippet());
//      recom.get(0).getSnippet().setTextDisplay(videoID.get(i).getValue());
//      cthreads.get(i).setReplies(new CommentThreadReplies().setComments(recom));
//      for (Comment c : cthreads.get(i).getReplies().getComments()) {
//        text.add(c.getSnippet().getTextDisplay());
//      }
//
//    }
//    CommentThreadListResponse comResponse = new CommentThreadListResponse();
//    comResponse.setItems(cthreads);
//    return comResponse;
//  }
}
