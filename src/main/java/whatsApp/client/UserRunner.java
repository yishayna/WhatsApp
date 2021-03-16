package whatsApp.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import whatsApp.object.CreateGroup;
import whatsApp.object.messages.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import static akka.actor.Actor.noSender;

public class UserRunner {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("client", ConfigFactory.load("client"));
        final ActorRef userActor = system.actorOf(User.props(), "userActor");

        Scanner scanner = new Scanner(System.in);
        try {
            String input;
            while(true){
                input = scanner.nextLine();
                if(input.startsWith("/user") )
                    handleUserCmd(userActor, input);
                else if (input.startsWith("/group"))
                    handleGroupCmd(userActor, input);
                else if(input.toLowerCase().contains("yes"))
                    acceptInvite(userActor);
                else if(input.startsWith("no"))
                    declineInvite(userActor);
                else if(input.startsWith("exit"))
                    break;
                else
                    Log("Unknown command: '" + input + "' ,Try again or type 'exit'");
            }
        } finally {
            scanner.close();
            system.terminate();
        }
    }


    private static void handleUserCmd(ActorRef userActor, String input) {
        String[] input_array = input.split("\\s+");
        String command = input_array[1];
        try {
            switch (command) {
                case "connect":
                    userActor.tell(new ConnectUser(input_array[2], userActor), noSender());
                    break;
                case "disconnect":
                    userActor.tell(new DisconnectUser(userActor), noSender());
                    break;
                case "text":
                    userActor.tell(new SendTextMessage(input_array[2], mergeText(input_array, 3)), noSender());
                    break;
                case "file":
                    String path = input_array[3];
                    byte[] msg = readFile(path);
                    if (msg != null)
                        userActor.tell(new SendFileMessage(input_array[2], msg, path), noSender());
                    break;
                default:
                    Log("Unknown command: '" + input + "' ,Try again or type 'exit'");
                    break;
            }

        }catch (Exception e){
            Log("Unknown command: '" + input + "' ,Try again or type 'exit'");
        }
    }

    private static void declineInvite(ActorRef userActor){};

    private static void acceptInvite(ActorRef userActor) {
        userActor.tell(new InviteToGroup(true),noSender());
    }

    private static void handleGroupCmd(ActorRef userActor, String input) {
        String[] input_array = input.split("\\s+");
        try {
            switch (input_array[1]) {
                case "create":
                    userActor.tell(new CreateGroup(input_array[2]), noSender());
                    break;
                case "leave":
                    userActor.tell(new LeaveGroup(input_array[2]), noSender());
                    break;
                case "send": {
                    switch (input_array[2]) {
                        case "text":
                            userActor.tell(new SendTextGroup(input_array[3], mergeText(input_array, 4)), noSender());
                            break;
                        case "file":
                            byte[] file = readFile(input_array[4]);
                            if (file != null)
                                userActor.tell(new SendFileGroup(input_array[3], input_array[4], file), ActorRef.noSender());
                            break;
                    }
                    break;
                }
                case "user": {
                    switch (input_array[2]) {
                        case "invite":
                            userActor.tell(new InviteToGroup(input_array[3], input_array[4]), noSender());
                            break;
                        case "remove":
                            userActor.tell(new RemoveUserGroup(input_array[3], input_array[4]), noSender());
                            break;
                        case "mute":
                            userActor.tell(new MuteUserGroup(input_array[3], input_array[4], input_array[5]), noSender());
                            break;
                        case "unmute":
                            userActor.tell(new UnmutedUserGroup(input_array[3], input_array[4]), noSender());
                            break;
                    }
                    break;
                }
                case "coadmin": {
                    switch (input_array[2]) {
                        case "add":
                            userActor.tell(new AddCoAdminGroup(input_array[3], input_array[4]), ActorRef.noSender());
                            break;
                        case "remove":
                            userActor.tell(new RemoveCoAdminGroup(input_array[3], input_array[4]), ActorRef.noSender());
                            break;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            Log("Unknown command: '" + input + "' ,Try again or type 'exit'");
        }
    }


    private static void Log (String toPrint){
        System.out.println(toPrint);
    }

    private static String mergeText(String[] array, int index){
        return String.join(" ", Arrays.copyOfRange(array, index, array.length));
    }

    private static byte[] readFile(String path){
        byte[] data = null;
        try{ data = Files.readAllBytes(Paths.get(path));
        System.out.println(Paths.get(path));}
        catch(IOException error){Log(error + " does not exist!"); }
        return data;
    }

}
