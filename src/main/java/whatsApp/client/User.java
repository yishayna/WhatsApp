package whatsApp.client;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import whatsApp.object.CreateGroup;
import whatsApp.object.Message;
import whatsApp.object.messages.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;



public class User extends AbstractActor {
    enum MessageHandle {
        ConnectUser,
        DisconnectUser,
        SendTextMessage,
        SendFileMessage,
        CreateGroup,
        LeaveGroup,
        SendTextGroup,
        SendFileGroup,
        MuteUserGroup,
        UnmutedUserGroup,
        InviteToGroup,
        RemoveUserGroup,
        AddCoAdminGroup,
        RemoveCoAdminGroup



    }

    private String userName = "" ;
    private String grouptInvitedTo="";
    private String groupInvitedFrom="";
    final ActorSelection manager = getContext().actorSelection("akka://whatsApp@127.0.0.1:3553/user/Manager");
    final static Timeout timeout_time = new Timeout(Duration.create(5, TimeUnit.SECONDS));
    LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);


    public static Props props() {
        return Props.create(User.class, User::new);
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ConnectUser.class, msg ->handler(msg, MessageHandle.ConnectUser))
                .match(DisconnectUser.class, msg ->handler(msg, MessageHandle.DisconnectUser))
                .match(SendTextMessage.class, msg ->handler(msg, MessageHandle.SendTextMessage))
                .match(SendFileMessage.class, msg ->handler(msg, MessageHandle.SendFileMessage))
                .match(CreateGroup.class,msg ->handler(msg, MessageHandle.CreateGroup))
                .match(LeaveGroup.class,msg ->handler(msg, MessageHandle.LeaveGroup))
                .match(SendTextGroup.class,msg ->handler(msg, MessageHandle.SendTextGroup))
                .match(SendFileGroup.class,msg ->handler(msg, MessageHandle.SendFileGroup))
                .match(MuteUserGroup.class,msg ->handler(msg, MessageHandle.MuteUserGroup))
                .match(UnmutedUserGroup.class,msg ->handler(msg, MessageHandle.UnmutedUserGroup))
                .match(RemoveUserGroup.class,msg ->handler(msg, MessageHandle.RemoveUserGroup))
                .match(AddCoAdminGroup.class,msg ->handler(msg, MessageHandle.AddCoAdminGroup))
                .match(RemoveCoAdminGroup.class,msg ->handler(msg, MessageHandle.RemoveCoAdminGroup))
                .match(InviteToGroup.class,msg ->handler(msg, MessageHandle.InviteToGroup))
                .match(ACK.class, msg-> Log(msg.getMessage()))
                .match(FAIL.class, msg-> Log(msg.getMessage()))
                .build();
    }


    private void handler (Message msg, MessageHandle Case){


        switch (Case){
            case ConnectUser:
                this.userName = askManager(new ConnectUser(msg, getSelf()));
            break;

            case DisconnectUser:
                askManager(new DisconnectUser(this.userName, getSelf()));
            break;

            case CreateGroup:
                manager.tell(new CreateGroup(msg, this.userName),getSelf());
                break;

            case LeaveGroup:
                manager.tell(new LeaveGroup(msg, this.userName),getSelf());
                break;

            case SendTextGroup:
                manager.tell(new SendTextGroup(msg, this.userName,getTime()),getSelf());
                break;

            case SendFileGroup:
                if(((SendFileGroup)msg).getSourceUser() == null)
                    manager.tell(new SendFileGroup(msg, this.userName),getSelf());
                else {
                    SendFileGroup msgF = ((SendFileGroup) msg);
                    String writeDest = printFile(msgF.getFilePath(),msgF.getFile());
                    if(writeDest.contains("File received:"))
                        Log(writeDest);
                    else
                        Log(String.format("File Downloads Error: %s",writeDest));
                }
                break;
            case MuteUserGroup:
                manager.tell(new MuteUserGroup(msg, this.userName),getSelf());
                break;

            case UnmutedUserGroup:
                manager.tell(new UnmutedUserGroup(msg, this.userName),getSelf());
                break;

            case RemoveUserGroup:
                manager.tell(new RemoveUserGroup(msg, this.userName),getSelf());
                break;

            case AddCoAdminGroup:
                manager.tell(new AddCoAdminGroup(msg, this.userName),getSelf());
                break;

            case RemoveCoAdminGroup:
                manager.tell(new RemoveCoAdminGroup(msg, this.userName),getSelf());
                break;

            case InviteToGroup: {
                boolean response = ((InviteToGroup) msg).getTargetResponse();
                boolean askForInvite = ((InviteToGroup) msg).getAskForInvite();
                String groupName = ((InviteToGroup) msg).getGroupName();
                String groupInviteSource = ((InviteToGroup) msg).getSourceUser();
                if(response)
                    manager.tell(new InviteToGroup(grouptInvitedTo,this.userName,groupInvitedFrom,response),getSelf());
                else{
                    if(askForInvite) {
                        grouptInvitedTo=groupName;
                        groupInvitedFrom=groupInviteSource;
                        Log(String.format("You have been invited to %s, Accept?", groupName));
                    }
                    else
                        manager.tell(new InviteToGroup(msg,this.userName),getSelf());
                }

                }
                break;
            case SendTextMessage:{
                SendTextMessage msgT = ((SendTextMessage) msg);
                if (msgT.getSource() != null)
                    Log(String.format("%s:%s[user][%s]%s",msgT.getTarget(),msgT.getTime(),msgT.getSource(),msgT.getMessage()));
                else {

                    ActorRef targetRef = getTargetRef(msgT.getTarget());

                    if (targetRef != null) {
                        targetRef.tell(new SendTextMessage(msgT, this.userName, getTime()), getSelf());
                    }
                }
            }
            break;
            case SendFileMessage:{
                SendFileMessage msgF = ((SendFileMessage) msg);
                if(msgF.getSource() != null){
                    String writeDest = printFile(msgF.getFilePath(),((SendFileMessage) msg).getFile());
                    if(writeDest.contains("File received:"))
                        Log(String.format("%s[user][%s] %s",msgF.getTime(),msgF.getSource(),writeDest));
                    else
                        Log(String.format("%s[user][%s] File Downloads Error: %s",msgF.getTime(),msgF.getSource(),writeDest));
                }
                else {
                    ActorRef targetRef = getTargetRef(msgF.getTarget());
                    if (targetRef != null)
                        targetRef.tell(new SendFileMessage(msgF,this.userName, getTime()),getSelf());
                }
            }
        }
    }

    private String printFile(String pathMsg, byte[] file_data) {
        try {
            String fileSuffix = new SimpleDateFormat("_MM_dd_HH_mm_ss_").format(new Date())+pathMsg;
            String userDirectory = System.getProperty("user.home") + File.separatorChar+"whatsApp-file"+File.separatorChar+userName;
            Files.createDirectories(Paths.get(userDirectory));
            try (FileOutputStream stream = new FileOutputStream(userDirectory+File.separatorChar+fileSuffix)) {
                stream.write(file_data);
            }
            return " File received: " + userDirectory+File.separatorChar+fileSuffix;
        } catch (IOException error) {
            return error.getMessage();
        }
    }



    private void Log (String toPrint){
        System.out.println(toPrint);
    }


    private ActorRef getTargetRef(String userName){
        Future<Object> future = Patterns.ask(manager, new GetUserActor(userName), timeout_time);
        try {
            Object res = Await.result(future, timeout_time.duration());
            if(res instanceof GetUserActor)
                return ((GetUserActor) res).getActorRef();
            else
                Log(((FAIL) res).getMessage());
        }catch(Exception error){
            Log("server is offline!");
            error.printStackTrace();
        }
        return null;
    }

    static public String getTime(){
        LocalDateTime now = LocalDateTime.now();
        return ("["+now.getHour()+":"+now.getMinute()+"]");
    }

    private String askManager(Message msg){
        Future<Object> future = Patterns.ask(manager, msg, timeout_time);
        try {
            Object res = Await.result(future, timeout_time.duration());
            if (res instanceof ACK) {
                Log(((ACK) res).getMessage());
                return ((Message) res).getContent();
            } else if (res instanceof FAIL) {
                Log(((FAIL) res).getMessage());
            } else Log("Server is offline!");
        } catch (Exception error) {
            Log("server is offline!");
        }
        return null;
    }

}
