package models;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Local Data model class to store results for a video search.
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class Record {

  private final String queryStr;
  private List<Video> videos;

  /**
   * Default constructor.
   *
   * @param queryStr the query str
   * @param videos   the videos
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public Record(String queryStr, List<Video> videos) {
    this.queryStr = queryStr;
    this.videos = videos;
  }

  @Override
  public String toString() {

    String output = "<div class=\"container\" style=\"text-align: left\" >\n"
        + "    <table class=\"table table-striped\" >\n"
        + "      <caption style=\"text-align:left\"> <b>Search Term:</b><a href=\"profile?v="
        + queryStr + "\" target=\"_blank\"> " + queryStr + " </a></caption>"
        + "<thead>\n"
        + "        <tr>\n"
        + "          <th>No.</th>"
        + "          <th scope=\"col\">Title</th>\n"
        + "          <th scope=\"col\">Channel</th>\n"
        + "          <th scope=\"col\">View Counts</th>\n"
        + "          <th scope=\"col\">LapsedTime(min)</th>\n"
        + "          <th scope=\"col\">Comments Sentiment</th>\n"
        + "        </tr>\n"
        + "      <thead>"
        + "<tbody id = \"!addQueryIDHere!\">";
    String videoStr = videos.stream().map(Video::toString).collect(Collectors.joining());
    output += videoStr;
    output += "      </tbody>\n"
        + "    </table>";
    return output;
  }

  /**
   * Return the searching string
   *
   * @return the query str
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public String getQueryStr() {
    return queryStr;
  }

  /**
   * Return Video list of the record
   *
   * @return the videos
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public List<Video> getVideos() {
    return videos;
  }

  /**
   * update videos List for the record
   *
   * @param videos videos a list of videos which belongs to Video type
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public void setVideos(List<Video> videos) {
    this.videos = videos;
  }
}
