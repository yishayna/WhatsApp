package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class GetUserActor extends Message {
    final private String userName;
    private ActorRef actorRef;

    public GetUserActor(String userName) {
        this.userName = userName;
        this.actorRef = null;
    }

    public GetUserActor(Message msg, ActorRef actorRef) {
        GetUserActor msgG = (GetUserActor)msg;
        this.userName = msgG.userName;
        this.actorRef = actorRef;
    }

    public String getUserName() {
        return userName;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
