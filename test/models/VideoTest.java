package models;

import static org.junit.Assert.assertEquals;

import com.google.api.client.util.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for Video Class.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class VideoTest {

  /**
   * Sets up a new Video Object
   */
  Video myVideo;
  /**
   * The Time.
   */
  DateTime time = new DateTime(System.currentTimeMillis());

  /**
   * Sets up.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Before
  public void setUp() {
    myVideo = new Video();
  }

  /**
   * Tear down.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @After
  public void tearDown() {
    myVideo = null;
  }

  /**
   * Gets lapsed time.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getLapsedTime() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    Long a = 0L;
    Long b = myVideo.getLapsedTime();
    assertEquals(a, b);

  }

  /**
   * Gets channel id.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getChannelID() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    assertEquals(myVideo.getChannelID(), "123-asdf");
  }

  /**
   * Gets video id.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getVideoID() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    assertEquals(myVideo.getVideoID(), "123QWE");
  }

  /**
   * Gets video title.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getVideo_title() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    assertEquals(myVideo.getVideo_title(), "Hello");
  }

  /**
   * Gets owner.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getOwner() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    assertEquals(myVideo.getOwner(), "world");
  }

  /**
   * Sets owner.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void setOwner() {
    myVideo = new Video("Hello", "world", time, "123QWE", "123-asdf");
    myVideo.setOwner("Owen");
    assertEquals(myVideo.getOwner(), "Owen");
  }

  /**
   * Gets view count.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getView_count() {
    long a = 10L;
    myVideo = new Video();
    myVideo.setView_count(a);
    long b = myVideo.getView_count();
    assertEquals(a, b);


  }

  /**
   * Sets view count.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void setView_count() {
    long a = 10L;
    myVideo = new Video();
    myVideo.setView_count(a);
    long b = myVideo.getView_count();
    assertEquals(a, b);
  }

  /**
   * Gets sentiment.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getSentiment() {
    int a = 1;
    myVideo = new Video();
    myVideo.setSentiment(a);
    assertEquals(a, myVideo.getSentiment());
  }

  /**
   * Sets sentiment.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void setSentiment() {
    int a = 1;
    myVideo = new Video();
    myVideo.setSentiment(a);
    assertEquals(a, myVideo.getSentiment());

  }

  /**
   * Sets ID.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void setVideoID() {
    String a = "123";
    myVideo = new Video();
    myVideo.setVideoID(a);
    assertEquals(a, myVideo.getVideoID());

  }

  /**
   * Gets sentiment html.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getSentimentHtml0() {
    int a = 0;
    myVideo = new Video();
    myVideo.setSentiment(a);
    String test0 = "\uD83D\uDC49";
    assertEquals(test0, myVideo.getSentimentHtml());
  }

  /**
   * Gets sentiment html 1.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getSentimentHtml1() {
    int a = 1;
    myVideo = new Video();
    myVideo.setSentiment(a);
    String test0 = "\uD83D\uDC4D";
    assertEquals(test0, myVideo.getSentimentHtml());
  }

  /**
   * Gets sentiment html 2.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getSentimentHtml2() {
    int a = 2;
    myVideo = new Video();
    myVideo.setSentiment(a);
    String test0 = "\uD83D\uDC4E";
    assertEquals(test0, myVideo.getSentimentHtml());
  }
}