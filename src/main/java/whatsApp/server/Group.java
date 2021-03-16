package whatsApp.server;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.remote.Ack;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Router;
import whatsApp.object.CreateGroup;
import whatsApp.object.Message;
import whatsApp.object.messages.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Group extends AbstractActor  {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    Router router;
    private String groupName;
    private String admin;
    private List<String> coAdmins;
    private List<String> users;
    private List<String> mutedUsers;

    public Group(String groupName, String admin) {
        this.groupName = groupName;
        this.admin = admin;
        this.coAdmins = new ArrayList<>();
        this.users = new ArrayList<>();
        this.mutedUsers = new ArrayList<>();
        this.router=new Router(new BroadcastRoutingLogic());
    }


    static public Props props(String groupName, String admin) {
        return Props.create(Group.class, () -> new Group(groupName, admin));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateGroup.class, msg->handler(msg, Manager.MessageHandle.CreateGroup))
                .match(DisconnectUser.class, msg ->handler(msg, Manager.MessageHandle.DisconnectUser))
                .match(LeaveGroup.class, msg ->handler(msg, Manager.MessageHandle.LeaveGroup))
                .match(SendTextGroup.class, msg ->handler(msg, Manager.MessageHandle.SendTextGroup))
                .match(SendFileGroup.class, msg ->handler(msg, Manager.MessageHandle.SendFileGroup))
                .match(MuteUserGroup.class, msg ->handler(msg, Manager.MessageHandle.MuteUserGroup))
                .match(UnmutedUserGroup.class, msg ->handler(msg, Manager.MessageHandle.UnmutedUserGroup))
                .match(RemoveUserGroup.class, msg ->handler(msg, Manager.MessageHandle.RemoveUserGroup))
                .match(AddCoAdminGroup.class, msg ->handler(msg, Manager.MessageHandle.AddCoAdminGroup))
                .match(RemoveCoAdminGroup.class, msg ->handler(msg, Manager.MessageHandle.RemoveCoAdminGroup))
                .match(InviteToGroup.class, msg ->handler(msg, Manager.MessageHandle.InviteToGroup))
                .build();
    }




    private void handler (Message msg, Manager.MessageHandle Case){
        switch (Case){
            case DisconnectUser: {
                String userToDelete = ((DisconnectUser) msg).getUserName();
                if (userToDelete.equals(this.admin)) {
                    router.route(new ACK(String.format("%s admin has closed %s!", this.groupName, this.groupName)), getSelf());
                    getContext().parent().tell(new GroupDeleted(this.groupName), getSelf());
                } else {
                    this.router=router.removeRoutee(((DisconnectUser)msg).getUserActor());
                    coAdmins.remove(userToDelete);
                    users.remove(userToDelete);
                    mutedUsers.remove(userToDelete);
                }
            }
            break;
            case CreateGroup:{
                CreateGroup msgC = (CreateGroup)msg;
                addUserToGroup(msgC.getAdmin(),getSender());
                this.router.route(new ACK(String.format("%s created successfully!",msgC.getGroupName())),getSelf());
            }
            break;
            case LeaveGroup:{
                String sourceUser = ((LeaveGroup) msg).getSourceUser();
                ActorRef sourceActor = ((LeaveGroup) msg).getSourceActor();

                if (!users.remove(sourceUser) & !mutedUsers.remove(sourceUser))
                    getSender().tell(new FAIL(String.format("%s is not in %s!", sourceUser,this.groupName)),getSelf());
                else
                    this.router.route(new ACK(String.format("%s has left %s!", sourceUser,this.groupName)),getSelf());

                if (coAdmins.remove(sourceUser))
                    this.router.route(new ACK(String.format("%s has removed from co-admin list in %s!", sourceUser,this.groupName)),getSelf());

                if (this.admin.equals(sourceUser)) {
                    router.route(new ACK(String.format("%s admin has closed %s!", this.groupName, this.groupName)), getSelf());
                    getContext().parent().tell(new GroupDeleted(this.groupName), getSelf());
                }
                this.router = router.removeRoutee(sourceActor);
            }
            break;
            case SendTextGroup:{
                SendTextGroup msgG = ((SendTextGroup) msg);
                if (!users.contains(((SendTextGroup) msg).getSourceUser())){
                    if (mutedUsers.contains(msgG.getSourceUser()))
                        getSender().tell(new FAIL(String.format("you are muted in %s!",this.groupName)),getSelf());
                    else
                        getSender().tell(new FAIL(String.format("%s is not in %s!", msgG.getSourceUser(),this.groupName)),getSelf());
                }
                else
                    this.router.route(new ACK(String.format("%s[%s][%s]%s", msgG.getTime(), this.groupName,msgG.getSourceUser(),msgG.getMessage())),getSelf());
            }
            break;

            case SendFileGroup:{
                if (!users.contains(((SendFileGroup) msg).getSourceUser())){
                    if (mutedUsers.contains(((SendFileGroup) msg).getSourceUser()))
                        getSender().tell(new FAIL(String.format("you are muted in %s!",this.groupName)),getSelf());
                    else
                        getSender().tell(new FAIL(String.format("you are not part of %s!",this.groupName)),getSelf());
                }
                else
                    this.router.route(msg,getSelf());
                }
                break;
            case MuteUserGroup:{
                String sourceUser = ((MuteUserGroup) msg).getSourceUser();
                String targetUser = ((MuteUserGroup) msg).getTarget();
                String time = ((MuteUserGroup) msg).getTime();
                ActorRef targetActor= ((MuteUserGroup) msg).getTargetActor();

                if (admin.equals(sourceUser) || coAdmins.contains(sourceUser)){
                    if (!this.users.contains(targetUser) && !this.mutedUsers.contains(targetUser))
                        getSender().tell(new FAIL(String.format("%s is not in %s!", targetUser,this.groupName)),getSelf());
                    else {
                        this.users.remove(targetUser);
                        this.mutedUsers.add(targetUser);
                        targetActor.tell(new ACK(String.format("%s you have been muted for %s in %s by %s!",targetUser,time,this.groupName,sourceUser)),getSelf());
                        this.getContext().getSystem().scheduler().scheduleOnce(Duration.ofSeconds(Integer.parseInt(time)),
                                getSelf(), new UnmutedUserGroup((MuteUserGroup)(msg)),
                                this.getContext().getSystem().dispatcher(), getSender());
                    }
                } else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!",this.groupName)),getSelf());
            }
            break;

            case UnmutedUserGroup:{
                String sourceUser = ((UnmutedUserGroup) msg).getSourceUser();
                String targetUser = ((UnmutedUserGroup) msg).getTarget();
                ActorRef targetActor=((UnmutedUserGroup) msg).getTargetActor();

                if (admin.equals(sourceUser) | coAdmins.contains(sourceUser)){
                    if (!this.mutedUsers.contains(targetUser))
                        getSender().tell(new FAIL(String.format("%s is not muted in %s!", targetUser,this.groupName)),getSelf());
                    else {
                        this.users.add(targetUser);
                        this.mutedUsers.remove(targetUser);

                        if (((UnmutedUserGroup) msg).isAuto())
                            targetActor.tell(new ACK("you have been unmuted! muting time is up"),getSelf());
                        else
                            targetActor.tell(new ACK(String.format("%s you have been unmuted in %s by %s!",targetUser,this.groupName,sourceUser)),getSelf());

                    }
                } else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!",this.groupName)),getSelf());
            }
            break;

            case RemoveUserGroup:{
                String sourceUser = ((RemoveUserGroup) msg).getSourceUser();
                String targetUser = ((RemoveUserGroup) msg).getTargetUser();
                ActorRef targetActor = ((RemoveUserGroup) msg).getTargetActor();
                if (admin.equals(sourceUser) || coAdmins.contains(sourceUser)){
                    if (!users.remove(targetUser) & !mutedUsers.remove(targetUser) & !coAdmins.remove(targetUser))
                        getSender().tell(new FAIL(String.format("%s is not in %s!", targetUser,this.groupName)),getSelf());
                    else{
                        this.router=router.removeRoutee(targetActor);
                        targetActor.tell(new ACK(String.format("you have been removed from %s by %s!",groupName,sourceUser)),getSelf()); }
                } else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!",this.groupName)),getSelf());
            }
            break;

            case AddCoAdminGroup: {
                String sourceUser = ((AddCoAdminGroup) msg).getSourceUser();
                String targetUser = ((AddCoAdminGroup) msg).getTargetUser();
                ActorRef targetActor = ((AddCoAdminGroup) msg).getTargetActor();

                if (admin.equals(sourceUser) || coAdmins.contains(sourceUser)) {
                    if(coAdmins.contains(targetUser))
                        getSender().tell(new FAIL(String.format("%s is already co-admin in %s ",targetUser,groupName)), getSelf());

                    else if (users.contains(targetUser) || mutedUsers.contains(targetUser)) {
                        coAdmins.add(targetUser);
                        targetActor.tell(new ACK(String.format("you have been promoted to co-admin in %s", groupName)), getSelf());
                    }
                    else
                        getSender().tell(new FAIL(String.format("%s is not in %s",targetUser,groupName)), getSelf());

                }else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!", this.groupName)), getSelf());
            }
            break;

            case RemoveCoAdminGroup:{
                String sourceUser = ((RemoveCoAdminGroup) msg).getSourceUser();
                String targetUser = ((RemoveCoAdminGroup) msg).getTargetUser();
                ActorRef targetActor = ((RemoveCoAdminGroup) msg).getTargetActor();
                if (admin.equals(sourceUser) || coAdmins.contains(sourceUser)){
                    if (!coAdmins.contains(targetUser)) {
                        getSender().tell(new FAIL(String.format("%s is not co-admin in %s!", targetUser, groupName)), getSelf());
                    }else{
                        coAdmins.remove(targetUser);
                        targetActor.tell(new ACK(String.format("you have been demoted to user in %s !",groupName)),getSelf()); }
                } else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!",groupName)),getSelf());
            }
            break;
            case InviteToGroup: {
                String sourceUser = ((InviteToGroup) msg).getSourceUser();
                String targetUser = ((InviteToGroup) msg).getTargetUser();
                ActorRef targetActor = ((InviteToGroup) msg).getTargetActor();
                Boolean targetResponse = ((InviteToGroup) msg).getTargetResponse();
                if (admin.equals(sourceUser) || coAdmins.contains(sourceUser)) {
                    if (!users.contains(targetUser)) {
                        if (targetResponse) {
                            addUserToGroup(targetUser, targetActor);
                            targetActor.tell(new ACK(String.format("Welcome to %s!", groupName)), getSelf());
                        } else
                            targetActor.tell(new InviteToGroup(msg, true), getSelf());
                    }
                    else
                        getSender().tell(new FAIL(String.format("%s is already in %s",targetUser, groupName)), getSelf());

                }else
                    getSender().tell(new FAIL(String.format("you are neither an admin nor a co-admin of %s!", this.groupName)), getSelf());
            }
            break;
            }



    }


    private void addUserToGroup(String userName, ActorRef actorRef){
        this.users.add(userName);
        this.router = this.router.addRoutee(new ActorRefRoutee(actorRef));
    }
}
