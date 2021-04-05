package models;

import com.google.api.client.util.DateTime;
import org.joda.time.Duration;

/**
 * This class generate a new type called Video which contains several attributes to identify the
 * Video.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class Video {

  private String video_title;
  private String owner;
  private Long view_count;
  private DateTime published_time;
  private String videoID;
  private String channelID;
  private int sentiment;

  /**
   * Default constructor
   */
  public Video() {
  }

  /**
   * Constructor with all variables
   *
   * @param video_title    the video title
   * @param owner          the owner
   * @param published_time the published time
   * @param videoID        the video id
   * @param channelID      the channel id
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public Video(
      String video_title, String owner, DateTime published_time, String videoID, String channelID) {
    this.video_title = video_title;
    this.owner = owner;
    this.published_time = published_time;
    this.videoID = videoID;
    this.channelID = channelID;
    this.sentiment = 0;
  }

  @Override
  public String toString() {
    return
        "<tr>" +
            "<td class=\"SortCLASS\"></td>" +
            "<td>" + video_title + "</td>" +
            "<td>" + owner +
            "<a href = \"channelVideo?channelID=" + channelID
            + "&sortingType=0&queryStr=!addQuqueryStrHere!" + "\" target=\"_blank\">[A]</a>" +
            "<a href = \"channelVideo?channelID=" + channelID
            + "&sortingType=1&queryStr=!addQuqueryStrHere!" + "\" target=\"_blank\">[D]</a>"
            + "</td>" +
            "<td>" + view_count + "</td>" +
            "<td>" + getLapsedTime() + "</td>" +
            "<td>" + getSentimentHtml() + "</td></tr>";

  }

  /**
   * This method returns the lapsed time since the video has been published
   *
   * @return lapsed time
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public Long getLapsedTime() {
    Duration d = new Duration(org.joda.time.DateTime.parse(this.published_time.toString()),
        org.joda.time.DateTime.now());
    return d.getStandardMinutes();
  }

  /**
   * This method gets the channelID from the selected video
   *
   * @return channelID channel id
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getChannelID() {
    return channelID;
  }

  /**
   * This method gets the videoID from the selected video
   *
   * @return videoID video id
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getVideoID() {
    return videoID;
  }

  /**
   * Sets video id.
   *
   * @param videoID the video id
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public void setVideoID(String videoID) {
    this.videoID = videoID;
  }

  /**
   * This method get the video_title from the selected video
   *
   * @return video_title video title
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getVideo_title() {
    return video_title;
  }

  /**
   * This method gets the owner from the selected video
   *
   * @return owner owner
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Sets owner.
   *
   * @param owner the owner
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * This method returns the total view counts from the selected video
   *
   * @return view_count view count
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public Long getView_count() {
    return view_count;
  }


  /**
   * Sets view count.
   *
   * @param view_count the view count
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public void setView_count(Long view_count) {
    this.view_count = view_count;
  }


  /**
   * This method get the sentiment from the selected video
   *
   * @return sentiment sentiment
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public int getSentiment() {
    return sentiment;
  }

  /**
   * This method set the sentiment from the selected video
   *
   * @param sentiment stands for emotion
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public void setSentiment(int sentiment) {
    this.sentiment = sentiment;
  }

  /**
   * Return the emoji for comment sentiment
   *
   * @return the sentiment html
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getSentimentHtml() {
    if (getSentiment() == 0) {
      return "\uD83D\uDC49";
    } else if (getSentiment() == 1) {
      return "\uD83D\uDC4D";
    } else {
      return "\uD83D\uDC4E";
    }
  }

}
