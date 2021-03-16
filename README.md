# WhatsApp

Messaging system with Actor per user using AKA Libery. 
In the first running of the Server (manager), we create a new Actor to handle manager requests. The manager responds only to new connections requests and creating new groups. 
When establishing a new connection, we create a new actor for the user. Users can connect with a specific username if it's not already in use.    
When a user creates a new group, he asks for the manager to create a new Actor under "system/manager" and saves a reference to the group for dispatch messages.

For send messages between users or group members, the sender asks from the manager the Actor ref of the target and sends him the Message *directly*.

To wrap all the requests from a user to a user, a user to a group, and user to the manager, we create an abstract class named "Message." Message class contains only Final fields to ensure we didn't use mutations in our code. Every change in a Message object returns a new instance with the updated details.
To specify the types of the optional messages and to organize our implementation, we use extended class of Message with appropriate names and fields.  

## Summary:
- manager and users are Actors under "system"
- Groups are Actors under "system/manager"
- Messages are sent directly
- mutations are not allowed in all the messages

## Build & Run
To build the the project open Terminal in project directory and run:
mvn clean install

To run Server:
mvn exec:java -Dexec.mainClass="whatsApp.server.ServerRunner"

To run User:
mvn exec:java -Dexec.mainClass="whatsApp.client.UserRunner"
