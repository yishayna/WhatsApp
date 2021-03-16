package whatsApp.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import whatsApp.object.CreateGroup;
import whatsApp.object.Message;
import whatsApp.object.messages.*;
import java.util.HashMap;


public class Manager extends AbstractActor {
    enum MessageHandle {
        ConnectUser,
        DisconnectUser,
        GroupDeleted,
        GetUserActor,
        CreateGroup,
        LeaveGroup,
        SendTextGroup,
        SendFileGroup,
        MuteUserGroup,
        UnmutedUserGroup,
        RemoveUserGroup,
        AddCoAdminGroup,
        RemoveCoAdminGroup,
        InviteToGroup
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private HashMap<String, ActorRef> connectedUsersList = new HashMap<String, ActorRef>();
    private HashMap<String, ActorRef> groupsList = new HashMap<String, ActorRef>();

    static public Props props() {
        return Props.create(Manager.class, Manager::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ConnectUser.class, msg -> handler(msg, MessageHandle.ConnectUser))
                .match(DisconnectUser.class, msg -> handler(msg, MessageHandle.DisconnectUser))
                .match(GroupDeleted.class, msg -> handler(msg, MessageHandle.GroupDeleted))
                .match(GetUserActor.class, msg -> handler(msg, MessageHandle.GetUserActor))
                .match(CreateGroup.class, msg -> handler(msg, MessageHandle.CreateGroup))
                .match(LeaveGroup.class, msg -> handler(msg, MessageHandle.LeaveGroup))
                .match(SendTextGroup.class, msg -> handler(msg, MessageHandle.SendTextGroup))
                .match(SendFileGroup.class, msg -> handler(msg, MessageHandle.SendFileGroup))
                .match(MuteUserGroup.class, msg -> handler(msg, MessageHandle.MuteUserGroup))
                .match(UnmutedUserGroup.class, msg -> handler(msg, MessageHandle.UnmutedUserGroup))
                .match(RemoveUserGroup.class, msg -> handler(msg, MessageHandle.RemoveUserGroup))
                .match(AddCoAdminGroup.class, msg -> handler(msg, MessageHandle.AddCoAdminGroup))
                .match(RemoveCoAdminGroup.class, msg -> handler(msg, MessageHandle.RemoveCoAdminGroup))
                .match(InviteToGroup.class, msg -> handler(msg, MessageHandle.InviteToGroup))
                .build();
    }


    private void handler(Message msg, MessageHandle Case) {
        switch (Case) {
            case ConnectUser: {
                String userName = ((ConnectUser) msg).getUserName();
                ActorRef actorRef = ((ConnectUser) msg).getUserActor();
                if (connectedUsersList.putIfAbsent(userName, actorRef) != null)
                    getSender().tell(new FAIL(String.format("%s already exists!", userName)), getSelf());
                else
                    getSender().tell(new ACK(String.format("%s connected successfully!", userName), userName), getSelf());
            }
            break;
            case DisconnectUser: {
                String userName = ((DisconnectUser) msg).getUserName();
                if (connectedUsersList.remove(userName) != null) {
                    groupsList.values().forEach(ref -> ref.forward(msg, getContext()));
                    getSender().tell(new ACK(String.format("%s has been disconnected successfully!", userName)), getSelf());
                    getSender().tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
                } else
                    getSender().tell(new FAIL(String.format("%s does not exists!", userName)), getSelf());
            }
            break;
            case GroupDeleted: {
                ActorRef groupActor = this.groupsList.get(((GroupDeleted) msg).getGroupName());
                this.groupsList.remove(((GroupDeleted) msg).getGroupName());
                groupActor.tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
            }
            break;
            case GetUserActor: {
                String targetName = ((GetUserActor) msg).getUserName();
                ActorRef actorRef = this.connectedUsersList.get(targetName);
                if (actorRef == null)
                    getSender().tell(new FAIL(String.format("%s does not exists!", targetName)), getSelf());
                else {
                    getSender().tell(new GetUserActor(msg, actorRef), getSelf());
                }
            }
            break;
            case CreateGroup: {
                String groupName = ((CreateGroup) msg).getGroupName();
                String admin = ((CreateGroup) msg).getAdmin();
                if (!groupsList.containsKey(groupName)) {
                    ActorRef groupActor = getContext().actorOf(Group.props(groupName, admin), groupName);
                    groupsList.put(groupName, groupActor);
                    groupActor.forward(msg, getContext());
                } else
                    getSender().tell(new FAIL(String.format("%s already exists!", groupName)), getSelf());
            }
            break;
            case LeaveGroup: {
                String groupName = ((LeaveGroup) msg).getGroupName();
                String sourceUser = ((LeaveGroup) msg).getSourceUser();
                if (groupsList.containsKey(groupName))
                    groupsList.get(groupName).forward(new LeaveGroup(msg, connectedUsersList.get(sourceUser)), getContext());
                else
                    getSender().tell(new FAIL(String.format("%s does not exists!", groupName)), getSelf());
            }
            break;
            case SendTextGroup: {
                String groupName = ((SendTextGroup) msg).getGroupName();
                if (groupsList.containsKey(groupName))
                    groupsList.get(groupName).forward((msg), getContext());
                else
                    getSender().tell(new FAIL(String.format("%s does not exists!", groupName)), getSelf());
            }
            break;
            case SendFileGroup: {
                String groupName = ((SendFileGroup) msg).getGroupName();
                if (groupsList.containsKey(groupName))
                    groupsList.get(groupName).forward(msg, getContext());
                else
                    getSender().tell(new FAIL(String.format("%s does not exists!", groupName)), getSelf());
            }
            break;
            case MuteUserGroup: {
                String groupName = ((MuteUserGroup) msg).getGroupName();
                String targetName = ((MuteUserGroup) msg).getTarget();

                if (checkGroupAndTarget(groupName, targetName))
                    groupsList.get(groupName).forward(new MuteUserGroup(msg, this.connectedUsersList.get(targetName)), getContext());
            }
            break;

            case UnmutedUserGroup: {
                String groupName = ((UnmutedUserGroup) msg).getGroupName();
                String targetName = ((UnmutedUserGroup) msg).getTarget();
                if (checkGroupAndTarget(groupName, targetName))
                    groupsList.get(groupName).forward(new UnmutedUserGroup(msg, this.connectedUsersList.get(targetName)), getContext());
            }
            break;

            case RemoveUserGroup: {
                String groupName = ((RemoveUserGroup) msg).getGroupName();
                String targetName = ((RemoveUserGroup) msg).getTargetUser();
                if (checkGroupAndTarget(groupName, targetName)) {
                    groupsList.get(groupName).forward(new RemoveUserGroup(msg, connectedUsersList.get(targetName)), getContext());
                }
            }
            break;

            case AddCoAdminGroup: {
                String groupName = ((AddCoAdminGroup) msg).getGroupName();
                String targetName = ((AddCoAdminGroup) msg).getTargetUser();
                ActorRef targetActor = null;
                if (checkGroupAndTarget(groupName, targetName)) {
                    targetActor = connectedUsersList.get(targetName);
                    groupsList.get(groupName).forward(new AddCoAdminGroup(msg, targetActor), getContext());
                }
            }
            break;

            case RemoveCoAdminGroup: {
                String groupName = ((RemoveCoAdminGroup) msg).getGroupName();
                String targetName = ((RemoveCoAdminGroup) msg).getTargetUser();
                ActorRef targetActor = null;
                if (checkGroupAndTarget(groupName, targetName)) {
                    targetActor = connectedUsersList.get(targetName);
                    groupsList.get(groupName).forward(new RemoveCoAdminGroup(msg, targetActor), getContext());
                }
            }
            break;

            case InviteToGroup: {
                String groupName = ((InviteToGroup) msg).getGroupName();
                String targetName = ((InviteToGroup) msg).getTargetUser();
                ActorRef targetActor = null;
                if (checkGroupAndTarget(groupName, targetName)) {
                    targetActor = connectedUsersList.get(targetName);
                    groupsList.get(groupName).forward(new InviteToGroup(msg, targetActor), getContext());
                }
            }
            break;

        }
    }

    private boolean checkGroupAndTarget(String groupName, String targetName) {
        if (!groupsList.containsKey(groupName)) {
            getSender().tell(new FAIL(String.format("%s does not exists!", groupName)), getSelf());
            return false;
        } else if (!connectedUsersList.containsKey(targetName)) {
            getSender().tell(new FAIL(String.format("%s does not exists!", targetName)), getSelf());
            return false;
        } else
            return true;
    }

    private void Log(String toPrint) {
        System.out.println(toPrint);
    }


}
