package controllers;

import actors.ChannelVideoActor;
import actors.StatisticActor;
import actors.Supervisor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import models.GoogleAPIInterface;
import models.Record;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.compat.java8.FutureConverters;

/**
 * The home controller acted with the actor system
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class ActorHomeController extends Controller {


  final ActorRef statisticActor;

  ActorRef supervisor;

  ActorRef channelActor;
  @Inject
  private ActorSystem actorSystem;
  @Inject
  private Materializer materializer;
  @com.google.inject.Inject
  private GoogleAPIInterface googleAPI;


  /**
   * start the ActorHomeController and bind with the GoogleAPI
   *
   * @param actorSystem the actor system
   * @param googleAPI   the google api
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  @Inject
  public ActorHomeController(ActorSystem actorSystem, GoogleAPIInterface googleAPI) {
    System.out.println("HomeController");
    this.googleAPI = googleAPI;
    statisticActor = actorSystem.actorOf(StatisticActor.props(googleAPI));
  }

  /**
   * redirect to the Home page with the result
   *
   * @param request the request
   * @return the result
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public Result index(Http.Request request) {
    System.out.println("HomeController index");
    return ok(views.html.index.render(request));
  }

  /**
   * redirect to the static page with the result
   *
   * @param req the req
   * @return the completion stage
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public CompletionStage<Result> profile(Http.Request req) {
    String query = req.queryString("v").orElse("");
    return FutureConverters.toJava(
        akka.pattern.Patterns
            .ask(statisticActor, new StatisticActor.StaticQuery(query),
                10000))
        .thenApply(response -> {
          StatisticActor.StaticResponse response1 = (StatisticActor.StaticResponse) response;
          return ok(views.html.profile.render((List<Entry<String, Integer>>) response1.getList()));
        });


  }


  /**
   * redirect to the channelVideo page with the search result
   *
   * @param request the request
   * @return the completion stage
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public CompletionStage<Result> channelVideo(Http.Request request) {
    System.out.println("HomeController index");
    String channelID = request.queryString("channelID").orElse("");
    String sortingTypeStr = request.queryString("sortingType").orElse("");
    String queryStr = request.queryString("queryStr").orElse("");
    int sortingTypeInt = Integer.parseInt(sortingTypeStr);
    channelActor = actorSystem.actorOf(ChannelVideoActor.getProps(googleAPI));
    return FutureConverters.toJava(
        akka.pattern.Patterns
            .ask(channelActor,
                new ChannelVideoActor.ChannelMsg(channelID, sortingTypeInt, queryStr),
                10000))
        .thenApply(response -> ok(views.html.channelVideo.render((List<Record>) response)));
  }

  /**
   * Start the websocket
   *
   * @return the web socket
   *
   * @author Yuxuan Luan, Rui Li, Junwei Zhang
   */
  public WebSocket ws() {
    System.out.println("---------web socket----------");
    return WebSocket.Json.accept(request ->
        ActorFlow.actorRef(ar -> Supervisor.getProps(ar, googleAPI), actorSystem, materializer));
  }
}
