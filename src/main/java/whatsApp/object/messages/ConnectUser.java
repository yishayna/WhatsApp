package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class ConnectUser extends Message {
    final private String userName;
    final private ActorRef userActor;

    public ConnectUser(String userName, ActorRef userActor){
        this.userName = userName;
        this.userActor = userActor;
    }

    public ConnectUser(Message msg, ActorRef userActor){
        ConnectUser msgC = (ConnectUser) msg;
        this.userName = msgC.userName;
        this.userActor = userActor;
    }


    public String getUserName() {
        return userName;
    }

    public ActorRef getUserActor() {
        return userActor;
    }
}
