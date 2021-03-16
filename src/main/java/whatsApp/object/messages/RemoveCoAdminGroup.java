package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class RemoveCoAdminGroup extends Message {
    private final String groupName;
    private final String targetUser;
    private final String sourceUser;
    private final ActorRef targetActor;

    public RemoveCoAdminGroup(String groupName, String targetUser) {
        this.groupName=groupName;
        this.targetUser=targetUser;
        this.sourceUser=null;
        this.targetActor=null;
    }
    public RemoveCoAdminGroup(Message msg, String sourceUser) {
        this.groupName=((RemoveCoAdminGroup) msg).getGroupName();
        this.targetUser=((RemoveCoAdminGroup) msg).getTargetUser();
        this.sourceUser=sourceUser;
        this.targetActor=null;
    }
    public RemoveCoAdminGroup(Message msg, ActorRef targetActor) {
        this.groupName=((RemoveCoAdminGroup) msg).getGroupName();
        this.targetUser=((RemoveCoAdminGroup) msg).getTargetUser();
        this.sourceUser=((RemoveCoAdminGroup) msg).getSourceUser();
        this.targetActor=targetActor;
    }
    public String getSourceUser() {
        return sourceUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public ActorRef getTargetActor() {
        return targetActor;
    }

}