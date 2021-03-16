package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class UnmutedUserGroup extends Message {
    private final String groupName;
    private final String target;
    private final String sourceUser;
    private final boolean isAuto;
    private final ActorRef targetActor;

    public UnmutedUserGroup(String groupName, String target){
        this.groupName = groupName;
        this.target = target;
        this.isAuto = false;
        sourceUser = null;
        this.targetActor=null;
    }

    public UnmutedUserGroup(Message msg, String sourceUser){
        this.groupName = ((UnmutedUserGroup)msg).groupName;
        this.target = ((UnmutedUserGroup)msg).target ;
        this.isAuto = false;
        this.sourceUser = sourceUser;
        this.targetActor=null;
    }
    public UnmutedUserGroup(Message msg, ActorRef targetActor){
        this.groupName = ((UnmutedUserGroup)msg).groupName;
        this.target = ((UnmutedUserGroup)msg).target ;
        this.isAuto = false;
        this.sourceUser = ((UnmutedUserGroup)msg).sourceUser ;
        this.targetActor=targetActor;
    }

    public UnmutedUserGroup(MuteUserGroup msg){
        this.groupName = msg.getGroupName();
        this.target =msg.getTarget() ;
        sourceUser = msg.getSourceUser();
        this.isAuto = true;
        this.targetActor=msg.getTargetActor();
    }

    public String getTarget() {
        return target;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public ActorRef getTargetActor() {
        return targetActor;
    }

}
