package models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.CommentThreadReplies;
import com.google.api.services.youtube.model.CommentThreadSnippet;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The VideoComments class Test
 *
 * @author Junwei Zhang
 */
public class VideoCommentsTest {

  /**
   * The constant mockGoogleApi.
   */
  public static GoogleAPI mockGoogleApi;
  /**
   * The Video comments.
   */
  VideoComments videoComments = new VideoComments();

  private List<Pair<String, String>> positive = Arrays
      .asList(new Pair<>("what day you are uploading ? every episode?",
              "Feel the Pain we all did \uD83D\uDE02")
          , new Pair<>("Is Kobe the greatest?", "Yes he is")
          , new Pair<>("People she loves\uD83D\uDE0E\uD83D\uDE0E", "Good")
      );
  private List<Pair<String, String>> negative = Arrays
      .asList(new Pair<>("Are you sad?", "\uD83D\uDE41"),
          new Pair<>("cheer up", "Yeah!"),
          new Pair<>("Wake Up!", "\uD83D\uDE2A\uD83D\uDE41\uD83D\uDE15"));

  private List<Pair<String, String>> neutral = Arrays
      .asList(new Pair<>("Are you sad?", "\uD83D\uDE41"),
          new Pair<>("cheer up", "Yeah!\uD83D\uDE0E"),
          new Pair<>("Wake Up!", "\uD83D\uDE2A"));

  private List<Pair<String, String>> no_emo = Arrays
      .asList(new Pair<>("Are you sad?", "No"),
          new Pair<>("cheer up", "Yeah!"),
          new Pair<>("Wake Up!", "Sure"));


  /**
   * Init. construct mock Api
   *
   * @author Junwei Zhang
   */
  @Before
  public void init() {
    Emoji e = new Emoji();
    mockGoogleApi = mock(GoogleAPI.class);
    videoComments.setApi(mockGoogleApi);
  }

  /**
   * Tear down.
   *
   * @author Junwei Zhang
   */
  @After
  public void tearDown() {
    videoComments.setApi(new GoogleAPI());
  }

  private CommentThreadListResponse mockcommentApi(List<Pair<String, String>> content) {
    //List<Comment> comments = new ArrayList<>();
    List<String> text = new ArrayList<>();
    List<CommentThread> cthreads = new ArrayList<>();
    List<Comment> totalComments = new ArrayList<>();

    content.forEach(c -> {
      CommentThread ct = new CommentThread();
      Comment com = new Comment();
      com.setSnippet(new CommentSnippet());
      com.getSnippet().setTextDisplay(c.getKey());
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
      recom.get(0).getSnippet().setTextDisplay(content.get(i).getValue());
      cthreads.get(i).setReplies(new CommentThreadReplies().setComments(recom));
      for (Comment c : cthreads.get(i).getReplies().getComments()) {
        text.add(c.getSnippet().getTextDisplay());
      }

    }
    CommentThreadListResponse comResponse = new CommentThreadListResponse();
    comResponse.setItems(cthreads);
    return comResponse;
  }


  /**
   * Video comment pos.
   *
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   */
  @Test
  public void videoCommentPos() throws GeneralSecurityException, IOException {
    when(mockGoogleApi.commentResponse("kobe")).thenReturn(mockcommentApi(positive));
    List<String> Text = new ArrayList<>();
    Text.add("what day you are uploading ? every episode?");
    Text.add("Feel the Pain we all did \uD83D\uDE02");
    Text.add("Is Kobe the greatest?");
    Text.add("Yes he is");
    Text.add("People she loves\uD83D\uDE0E\uD83D\uDE0E");
    Text.add("Good");
    List<String> commentText = videoComments.videoCommentThread(mockcommentApi(positive));
    assertEquals(commentText, Text);

  }

  /**
   * test sad emotion when negative emoji lagger than 70%
   *
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   */
  @Test
  public void commentNeg() throws GeneralSecurityException, IOException {
    when(mockGoogleApi.commentResponse("sad")).thenReturn(mockcommentApi(negative));
    assertEquals(new akka.japi.Pair<>("sad", -1), videoComments.processComment("sad"));
  }

  /**
   * test neural emotion when none of happy or sad emoji larger than 70%
   *
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   */
  @Test
  public void commentNeu() throws GeneralSecurityException, IOException {
    when(mockGoogleApi.commentResponse("neu")).thenReturn(mockcommentApi(neutral));
    assertEquals(new akka.japi.Pair<>("neu", 0), videoComments.processComment("neu"));
  }

  /**
   * test empty emoji comment list
   *
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   */
  @Test
  public void commentNone() throws GeneralSecurityException, IOException {
    when(mockGoogleApi.commentResponse("non")).thenReturn(mockcommentApi(no_emo));
    assertEquals(new akka.japi.Pair<>("non", 0), videoComments.processComment("non"));
  }

  /**
   * test positive emoji when happy larger than 70%
   *
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @author Junwei Zhang
   */
  @Test
  public void commentPositive() throws GeneralSecurityException, IOException {
    when(mockGoogleApi.commentResponse("kobe")).thenReturn(mockcommentApi(positive));
    //<String> Text = new ArrayList<>();
    //List<String> commentText = videoComments.videoCommentThread(mockcommentApi(positive));

    assertEquals(new akka.japi.Pair<>("kobe", 1), videoComments.processComment("kobe"));

  }
}
