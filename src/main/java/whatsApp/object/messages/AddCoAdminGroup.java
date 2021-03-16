package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class AddCoAdminGroup extends Message {
    private final String groupName;
    private final String targetUser;
    private final String sourceUser;
    private final ActorRef targetActor;

    public AddCoAdminGroup(String groupName, String targetUser) {
        this.groupName=groupName;
        this.targetUser=targetUser;
        this.sourceUser=null;
        this.targetActor=null;
    }
    public AddCoAdminGroup(Message msg, String sourceUser) {
        this.groupName=((AddCoAdminGroup) msg).getGroupName();
        this.targetUser=((AddCoAdminGroup) msg).getTargetUser();
        this.sourceUser=sourceUser;
        this.targetActor=null;
    }
    public AddCoAdminGroup(Message msg, ActorRef targetActor) {
        this.groupName=((AddCoAdminGroup) msg).getGroupName();
        this.targetUser=((AddCoAdminGroup) msg).getTargetUser();
        this.sourceUser=((AddCoAdminGroup) msg).getSourceUser();
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
