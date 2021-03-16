# WhatsApp

## How TO Run
To build the the project open Terminal in project directory and run:
mvn clean install

To run Server:
mvn exec:java -Dexec.mainClass="whatsApp.server.ServerRunner"

To run User:
mvn exec:java -Dexec.mainClass="whatsApp.client.UserRunner"
