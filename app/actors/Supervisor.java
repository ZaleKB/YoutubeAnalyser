package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.GoogleAPIInterface;
import scala.concurrent.duration.Duration;

/**
 * the Supervisor actor which could handle the errors and nake start and stop to the
 * following child actors
 *
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class Supervisor extends AbstractActorWithTimers {

  private final static SupervisorStrategy strategy = new OneForOneStrategy(
      1,
      Duration.create(2, "seconds"),
      DeciderBuilder
          .match(Exception.class, e -> {
            System.out.println("Supervisor: child actor resumed");
            return (SupervisorStrategy.Directive) SupervisorStrategy.resume();
          })
          .build());


    ActorRef user;


  private Supervisor(final ActorRef wsout, GoogleAPIInterface googleAPI) {
    this.user = getContext().actorOf(UserActor.props(wsout, googleAPI));
    System.out.println("Supervisor initial");
  }

    /**
     * start the Supervisor actor and bind with the GoogelAPI
     *
     * @param wsout     the wsout
     * @param googleAPI the google api
     * @return the props
     */
    public static Props getProps(final ActorRef wsout, GoogleAPIInterface googleAPI) {
    System.out.println("Supervisor getProps");
    return Props.create(Supervisor.class, wsout, googleAPI);
  }

    /**
     * return the stragety
     */


  @Override
  public SupervisorStrategy supervisorStrategy() {
    return strategy;
  }

    /**
     * match the incoming message to the specific category and start the time actor
     *
     * @return Receive the build message
     *
     * @author Yuxuan Luan, Rui Li, Junwei Zhang
     */

  @Override
  public Receive createReceive() {
    System.out.println("TimeActor createReceive");
    return receiveBuilder()
        .match(ObjectNode.class, (json) -> user.forward(json, getContext()))
        .match(Props.class, props -> {
          getSender().tell(getContext().actorOf(props), getSelf());
        })
        .build();
  }


}
