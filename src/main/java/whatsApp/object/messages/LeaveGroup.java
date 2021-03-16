package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class LeaveGroup extends Message {
    private final String groupName;
    private  String sourceUser;
    private ActorRef sourceActor;


    public LeaveGroup(String groupName) {
        this.groupName = groupName;
    }

    public LeaveGroup(Message msg, String sourceUser) {
        LeaveGroup msgL = (LeaveGroup)msg;
        this.groupName = msgL.groupName;
        this.sourceUser = sourceUser;
        this.sourceActor=null;
    }
    public LeaveGroup(Message msg, ActorRef sourceActor) {
        LeaveGroup msgL = (LeaveGroup)msg;
        this.groupName = msgL.groupName;
        this.sourceUser = msgL.sourceUser;
        this.sourceActor=sourceActor;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public ActorRef getSourceActor() {
        return sourceActor;
    }

}
