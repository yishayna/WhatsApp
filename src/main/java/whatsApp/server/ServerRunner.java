package whatsApp.server;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

public class ServerRunner {
    public static void main(String[] args){
            final ActorSystem system = ActorSystem.create("whatsApp", ConfigFactory.load("server"));
            try {
                system.actorOf(Manager.props(), "Manager");
                System.out.println(">>> Press ANY KEY to exit <<<");
                System.in.read();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            } finally {
                system.terminate();
            }
        }
}
