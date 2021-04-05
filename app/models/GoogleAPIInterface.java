package models;


import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
//import javafx.util.Pair;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The interface Google api interface.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public interface GoogleAPIInterface {

  /**
   * Fetch videos response completable future wait for two API to inherit
   *
   * @param query       the query
   * @param isChannelID the is channel id
   * @return the completable future
   */
  CompletableFuture<SearchListResponse> fetchVideosResponse(String query,
      boolean isChannelID);

  /**
   * Fetch view counts response video list response wait for two API to inherit.
   *
   * @param videoIDString the video id string
   * @return the video list response
   */
  VideoListResponse fetchViewCountsResponse(String videoIDString);

  /**
   * Fetch first 50 titles response completable future wait for two API to inherit
   *
   * @param query the query
   * @return the completable future
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   */
  CompletableFuture<SearchListResponse> firstHalfResponse1(String query)
      throws GeneralSecurityException, IOException;

  /**
   * Fetch second 50 titles response completable future. wait for two API to inherit
   *
   * @param query the query
   * @return the completable future
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   */
  CompletableFuture<SearchListResponse> firstHalfResponse2(String query)
      throws GeneralSecurityException, IOException;

  /**
   * Comment response comment thread list response wait for two API to inherit
   *
   * @param videoID the video id
   * @return the comment thread list response
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   */
  CommentThreadListResponse commentResponse(String videoID)
      throws GeneralSecurityException, IOException;

//  CommentThreadListResponse commentForTest(List<Pair<String, String>> content)
//    throws GeneralSecurityException, IOException;

}
