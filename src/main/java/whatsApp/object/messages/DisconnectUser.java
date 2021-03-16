package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class DisconnectUser extends Message {
    final private String userName;
    final private ActorRef userActor;


    public DisconnectUser(ActorRef userActor){
        this.userName=null;
        this.userActor = userActor;
    }

    public DisconnectUser(String userName, ActorRef userActor){
        this.userName = userName;
        this.userActor = userActor;
    }

    public String getUserName() {
        return userName;
    }

    public ActorRef getUserActor() {
        return userActor;
    }
}
