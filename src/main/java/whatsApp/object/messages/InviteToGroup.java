package whatsApp.object.messages;

import akka.actor.ActorRef;
import whatsApp.object.Message;

public class InviteToGroup extends Message {
    private final String groupName;
    private final String targetUser;
    private final String sourceUser;
    private final ActorRef targetActor;
    private final Boolean targetResponse;
    private final Boolean askForInvite;

    public InviteToGroup(String groupName, String targetUser) {
        this.groupName=groupName;
        this.targetUser=targetUser;
        this.sourceUser=null;
        this.targetActor=null;
        this.targetResponse=false;
        this.askForInvite=false;

    }
    public InviteToGroup(Message msg, String sourceUser) {
        this.groupName=((InviteToGroup)msg).getGroupName();
        this.targetUser=((InviteToGroup)msg).getTargetUser();
        this.sourceUser=sourceUser;
        this.targetActor=((InviteToGroup)msg).getTargetActor();
        this.targetResponse=((InviteToGroup)msg).getTargetResponse();
        this.askForInvite=((InviteToGroup)msg).getAskForInvite();
    }
    public InviteToGroup(String groupName, String targetUser,String sourceUser,Boolean targetResponse) {
        this.groupName=groupName;
        this.targetUser=targetUser;
        this.sourceUser=sourceUser;
        this.targetActor=null;
        this.targetResponse=targetResponse;
        this.askForInvite=false;
    }
    public InviteToGroup(Message msg, ActorRef targetActor) {
        this.groupName=((InviteToGroup)msg).getGroupName();
        this.targetUser=((InviteToGroup)msg).getTargetUser();
        this.sourceUser=((InviteToGroup)msg).getSourceUser();
        this.targetActor=targetActor;
        this.targetResponse=((InviteToGroup)msg).getTargetResponse();
        this.askForInvite=((InviteToGroup)msg).getAskForInvite();
    }
    public InviteToGroup(Message msg, Boolean askForInvite) {
        this.groupName=((InviteToGroup)msg).getGroupName();
        this.targetUser=((InviteToGroup)msg).getTargetUser();
        this.sourceUser=((InviteToGroup)msg).getSourceUser();
        this.targetActor=((InviteToGroup)msg).getTargetActor();;
        this.targetResponse=((InviteToGroup)msg).getTargetResponse();
        this.askForInvite=askForInvite;
    }

    public InviteToGroup(Boolean targetResponse) {
        this.groupName=null;
        this.targetUser=null;
        this.sourceUser=null;
        this.targetActor=null;
        this.targetResponse=targetResponse;
        this.askForInvite=false;
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTargetUser() { return targetUser; }

    public ActorRef getTargetActor() { return targetActor; }

    public boolean getTargetResponse() { return targetResponse; }

    public boolean getAskForInvite() { return askForInvite; }

}
