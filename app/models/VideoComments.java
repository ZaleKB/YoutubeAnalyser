package models;

import akka.japi.Pair;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analysing Toplevel video comment up to 100, process happy and sad emoji
 *
 * @author Junwei Zhang
 */
public class VideoComments {

  private List<Comment> comments = new ArrayList<>();
  private List<String> commentText = new ArrayList<>();
  private List<CommentThread> commentThreads = new ArrayList<>();
  private GoogleAPI api = new GoogleAPI();

  private String happy = Emoji.HA;
  private String sad = Emoji.SA;

  /**
   * Sets api.
   *
   * @param api the api
   *
   * @author Junwei Zhang
   */
  public void setApi(GoogleAPI api) {
    this.api = api;
  }

  /**
   * This method accepts a String called videoID, based on Youtube Service CommentStream stream
   * Search for toplevel published comment up to 100 for a single video and add into commentText
   * List of String for one comment, it will includes all its sub-comments and add into commentText
   * List of String
   *
   * @param response CommentThreadListResponse response
   *
   * @return commentText the list of up to 100 comment plain text for one video
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   * @since 1.0
   */
  public List<String> videoCommentThread(CommentThreadListResponse response)
      throws GeneralSecurityException, IOException {

    commentThreads = response.getItems();
    commentThreads.stream()
        .map(c -> {
          comments.add(c.getSnippet().getTopLevelComment());
          commentText.add(c.getSnippet().getTopLevelComment().getSnippet().getTextDisplay());
          return c.getReplies();
        })
        .filter(rep -> rep != null)
        .forEach(rep -> {
          comments.addAll(rep.getComments());
          rep.getComments()
              .stream()
              .forEach(repIn -> {
                commentText.add(repIn.getSnippet().getTextDisplay());
              });
        });
    return commentText;
  }

  /**
   * This method is a pipeline after videoCommentThread method, which will handle the list of
   * commentText Using Pattern designed from class Emoji to match emoticon from the Text and count
   * them after count all pos and neg emoticon from one video comment text calculate the rate of
   * them, based on percentage of emotion return new String Integer pair correspond to each videoID
   * and Emotion signal pair Integer 1 represent positive Integer -1 represent negative Integer 0
   * represent neutral Use {@link #videoCommentThread(CommentThreadListResponse response)} to move a
   * piece
   *
   * @param videoId the videoID for a youtube video
   *
   * @return {@code new Pair<String,Integer>} here the integer indicate sentiment
   * signal(pos,neg,neu)
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   * @see models.Emoji
   * @since 1.0
   */
  public Pair<String, Integer> processComment(String videoId)
      throws GeneralSecurityException, IOException {

      videoCommentThread(this.api.commentResponse(videoId));
      Pattern pos = Pattern.compile(happy);
      Pattern neg = Pattern.compile(sad);
      AtomicInteger count_pos = new AtomicInteger();
      AtomicInteger count_neg = new AtomicInteger();
      commentText.stream()
        .forEach(s -> {
          Matcher pos_match = pos.matcher(s);
          Matcher neg_match = neg.matcher(s);
          while (pos_match.find()) {
            count_pos.getAndIncrement();
          }
          while (neg_match.find()) {
            count_neg.getAndIncrement();
          }
        });
    int positive = count_pos.get();
    int negative = count_neg.get();
    if (positive + negative == 0) {
      return new Pair<>(videoId, 0);
    } else if (positive / (positive + negative) >= 0.7) {
      return new Pair<>(videoId, 1);
    } else if (negative / (positive + negative) >= 0.7) {
      return new Pair<>(videoId, -1);
    } else {
      return new Pair<>(videoId, 0);
    }
  }



}