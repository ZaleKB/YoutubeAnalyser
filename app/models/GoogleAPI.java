package models;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;

/**
 * This is API interface calling class that handles requests from controllers. All the API calling
 * methods are defined and implemented here. API calling model is separated from other local date
 * processing logic.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class GoogleAPI implements GoogleAPIInterface {

  private static final String DEVELOPER_KEY_RUI = "AIzaSyBrv4rValDfMcdDiPZcgkv_NLQGKN1xJb4";
  private static final String DEVELOPER_KEY_LUAN = "AIzaSyDXy2jwBLmXhuA1NdCV2OiTl1XIA83YjDo";
  private static final String DEVELOPER_KEY_Junwei = "AIzaSyAHep9htA9cKu-ZsLnt13UAiB0ec-7vyjo";
  private static final String DEVELOPER_KEY_KUN = "AIzaSyBDWuAthU_jt1BvniAvokWIG_JWeZfwi_o";
  private static final String APPLICATION_NAME = "Youtube";
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  //  private static final GoogleAPI googleAPI = new GoogleAPI();
  private static String DEVELOPER_KEY;
  private static YouTube ytService;

  /**
   * Instantiates a new Google api.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public GoogleAPI() {
    try {
      ytService = getService();
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    DEVELOPER_KEY = DEVELOPER_KEY_Junwei;
  }


  /**
   * Connect to the remote Youtube API service and then use the return to do the following steps
   *
   * @return google.api.services.youtube
   * @throws GeneralSecurityException throw GeneralSecurityException
   * @throws IOException              throw IOException
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  private YouTube getService() throws GeneralSecurityException, IOException {
    final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }


  /**
   * Fetch videos response completable future.
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
    return CompletableFuture.supplyAsync(() -> {
      try {
        ytService = getService();
        YouTube.Search.List req1 =
            ytService.search().list("snippet").setKey(DEVELOPER_KEY)
                .setType("video").setOrder("date").setMaxResults(10L);
        if (isChannelID) {
          req1 = req1.setChannelId(query);
        } else {
          req1 = req1.setQ(query);
        }
        return req1.execute();
      } catch (IOException | GeneralSecurityException e) {
        e.printStackTrace();
        return new SearchListResponse();
      }
    });
  }

  /**
   * Fetch view counts response video list response.
   *
   * @param videoIDString the video id string
   *
   * @return the video list response
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Override
  public VideoListResponse fetchViewCountsResponse(String videoIDString) {
    try {
      ytService = getService();
      YouTube.Videos.List req2 = ytService
          .videos()
          .list("statistics")
          .setKey(DEVELOPER_KEY)
          .setId(videoIDString);
      return req2.execute();
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
      return new VideoListResponse();
    }

  }

  /**
   * Fetch first 50 titles response completable future.
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
    ytService = getService();
    return CompletableFuture.supplyAsync(() -> {
      try {
        return ytService
            .search()
            .list("snippet")
            .setKey(DEVELOPER_KEY)
            .setType("video")
            .setQ(query)
            .setOrder("date")
            .setMaxResults(50L).execute();

      } catch (IOException e) {
        e.printStackTrace();
      }
      return new SearchListResponse();
    });
  }

  /**
   * Fetch second 50 titles response completable future.
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
    ytService = getService();
    return CompletableFuture.supplyAsync(() -> {
      try {
        return ytService
            .search()
            .list("snippet")
            .setKey(DEVELOPER_KEY)
            .setType("video")
            .setQ(query)
            .setOrder("date")
            .setPageToken("CDIQAA")
            .setMaxResults(50L).execute();

      } catch (IOException e) {
        e.printStackTrace();
        return new SearchListResponse();
      }

    });
  }

  /**
   * Comment response comment thread list response.
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
    YouTube ctService = getService();

    YouTube.CommentThreads.List req =
        ctService.commentThreads()
            .list("snippet, replies")
            .setKey(DEVELOPER_KEY)
            .setVideoId(videoID)
            .setMaxResults(100L)
            .setModerationStatus("published")
            .setTextFormat("plainText");
    return req.execute();
  }

//  @Override
//  public CommentThreadListResponse commentForTest(List<Pair<String, String>> content) throws GeneralSecurityException, IOException {
//    return null;
//  }
}
