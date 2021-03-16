package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class RemoveUserGroup extends Message {

    private final String groupName;
    private final String targerUser;
    private final String sourceUser;
    private final ActorRef targetActor;


    public RemoveUserGroup(String groupName , String targerUser) {
        this.groupName=groupName;
        this.targerUser=targerUser;
        this.sourceUser=null;
        this.targetActor=null;
    }
    public RemoveUserGroup(Message msg , String sourceUser) {
        this.sourceUser=sourceUser;
        this.targerUser=((RemoveUserGroup) msg).getTargetUser();
        this.groupName=((RemoveUserGroup) msg).getGroupName();
        this.targetActor=null;
    }
    public RemoveUserGroup(Message msg , ActorRef targetActor) {
        this.sourceUser=((RemoveUserGroup) msg).getSourceUser();
        this.targerUser=((RemoveUserGroup) msg).getTargetUser();
        this.groupName=((RemoveUserGroup) msg).getGroupName();
        this.targetActor=targetActor;
    }
    public String getSourceUser() {
        return sourceUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTargetUser() {
        return targerUser;
    }

    public ActorRef getTargetActor() {
        return targetActor;
    }


}
