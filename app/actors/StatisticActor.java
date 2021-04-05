package actors;

import static java.util.stream.Collectors.toMap;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Pair;
import com.google.api.services.youtube.model.SearchListResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import models.GoogleAPIInterface;

/**
 * Create StatisticActor to analyze the statistic of the first 100 titles
 *
 * @author Yuxuan Luan
 */
public class StatisticActor extends AbstractLoggingActor {

  private GoogleAPIInterface googleAPI;

  /**
   * Instantiates a new Statistic actor.
   *
   * @param googleAPI the google api
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public StatisticActor(GoogleAPIInterface googleAPI) {
    this.googleAPI = googleAPI;
  }


  /**
   * Create the StatisticActor bind the googleAPI
   *
   * @param googleAPI the google api
   * @return the props
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public static Props props(GoogleAPIInterface googleAPI) {
    return Props.create(StatisticActor.class, googleAPI);
  }

  /**
   * match the incoming message and send to the destination
   *
   * @return Receive the build message
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(StaticQuery.class, s -> {
          ActorRef sender = sender();
          qSim(s.query).thenAccept(list -> sender.tell(new StaticResponse(list), self()));
        })
        .build();
  }

  /**
   * Searching and calculating the similarity levels of the unique words from the 100 latest video
   * titles based on the search query by asynchronous method
   *
   * @param query the query
   * @return the completable future
   * @throws GeneralSecurityException the general security exception
   * @throws IOException              the io exception
   * @throws ExecutionException       the execution exception
   * @throws InterruptedException     the interrupted exception
   * @author Yuxuan Luan
   */
  public CompletableFuture<List<Map.Entry<String, Integer>>> qSim(String query)
      throws GeneralSecurityException, IOException, ExecutionException, InterruptedException {

    CompletableFuture<List<String>> titles1 = googleAPI.firstHalfResponse1(query)
        .thenApply(this::extractTitles);
    CompletableFuture<List<String>> titles2 = googleAPI.firstHalfResponse2(query)
        .thenApply(this::extractTitles);
    CompletableFuture<List<String>> finalTitle = titles1.thenCombine(titles2, (a, b) -> {
      a.addAll(b);
      return a;
    });
    CompletableFuture<List<Map.Entry<String, Integer>>> result = finalTitle
        .thenApply(this::queryFetch);

    return result;

  }

  /**
   * Extract the response from GoogleAPI and and find the title names from each response and gather
   * them into a list of String
   *
   * @param queryRes the query res
   * @return the list
   * @author Yuxuan Luan
   */
  public List<String> extractTitles(SearchListResponse queryRes) {
    List<String> titles = new ArrayList<>();
    queryRes.getItems().forEach((i) -> titles.add(i.getSnippet().getTitle()));
    return titles;
  }

  /**
   * Splitting the list and counting each unique word show in the list and count the occurrence of
   * each one by using Stream method.
   *
   * @param totalList the total list
   * @return the list
   * @author Yuxuan Luan
   */
  public List<Map.Entry<String, Integer>> queryFetch(List<String> totalList) {
    Map<String, Integer> result = new HashMap<>();
    result =
        totalList.stream()
            .flatMap(line -> Arrays.stream(line.split("\\s+")))
            .map(word -> word.replaceAll("[^a-zA-Z]", " ").toLowerCase())
            .flatMap(line -> Arrays.stream(line.split("\\s+")))
            .filter(word -> word.length() != 0)
            .map(word -> new Pair<String, Integer>(word, 1))
            .collect(toMap(e -> e.first(), e -> e.second(), (v1, v2) -> v1 + v2));
    List<Map.Entry<String, Integer>> fresult = new ArrayList<>();
    Set<Map.Entry<String, Integer>> entrySet = result.entrySet();
    ArrayList<Map.Entry<String, Integer>> listOfEntry =
        new ArrayList<Map.Entry<String, Integer>>(entrySet);
    fresult =
        listOfEntry.stream()
            .sorted(Comparator.comparing(i -> -i.getValue()))
            .collect(Collectors.toList());

    return fresult;
  }

  /**
   * The type Static query to accept the input query.
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public static final class StaticQuery {

    /**
     * The Query.
     */
    public final String query;

    /**
     * Instantiates a new Static query.
     *
     * @param query the query
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public StaticQuery(String query) {
      this.query = query;
    }
  }

  /**
   * The type Static response.
   */
  public static final class StaticResponse {

    public final List<Map.Entry<String, Integer>> list;


    /**
     * Instantiates a new Static response.
     *
     * @param list the list
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public StaticResponse(List<Map.Entry<String, Integer>> list) {
      this.list = list;
    }

    /**
     * Gets the result list.
     *
     * @return the list
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */
    public List<Map.Entry<String, Integer>> getList() {
      return list;
    }
  }
}