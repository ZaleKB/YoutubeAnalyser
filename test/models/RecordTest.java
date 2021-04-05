package models;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Record.class.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class RecordTest {

  private Record r;

  /**
   * Sets up.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Before
  public void setUp() {
    List<Video> videos = new ArrayList<>();
    videos.add(new Video());
    r = new Record("q", videos);
  }

  /**
   * Tear down.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @After
  public void tearDown() {
    r = null;
  }

  /**
   * Unit test for Testr getQueryStr.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getQueryStr() {
    assertEquals("q", r.getQueryStr());
  }

  /**
   * Gets videos.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void getVideos() {
    assertEquals(1, r.getVideos().size());
  }

  /**
   * Sets videos.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Test
  public void setVideos() {
    List<Video> videos = new ArrayList<>();
    videos.add(new Video());
    r.setVideos(videos);
    assertEquals(videos, r.getVideos());
  }
}