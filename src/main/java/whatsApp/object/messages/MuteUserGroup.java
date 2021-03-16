package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class MuteUserGroup extends Message {
    private final String groupName;
    private final String target;
    private final String sourceUser;
    private final String time;
    private final ActorRef targetActor;


    public MuteUserGroup(String groupName, String target, String time) {
        this.groupName = groupName;
        this.target = target;
        this.time = time;
        sourceUser = null;
        this.targetActor=null;
    }
    public MuteUserGroup(Message msg, String source) {
        this.groupName = ((MuteUserGroup)msg).groupName;
        this.target = ((MuteUserGroup)msg).target;
        this.time = ((MuteUserGroup)msg).time;
        this.sourceUser = source;
        this.targetActor=null;
    }
    public MuteUserGroup(Message msg, ActorRef targetActor) {
        this.groupName = ((MuteUserGroup)msg).groupName;
        this.target = ((MuteUserGroup)msg).target;
        this.time = ((MuteUserGroup)msg).time;
        this.sourceUser =((MuteUserGroup)msg).sourceUser;
        this.targetActor=targetActor;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public String getTarget() {
        return target;
    }

    public String getTime() {
        return time;
    }

    public ActorRef getTargetActor() {
        return targetActor;
    }

}
