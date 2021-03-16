package whatsApp.object.messages;

import whatsApp.object.Message;

public class ACK extends Message {

    public ACK (String message){
        this.message = message;
    }

    public ACK (String message, String content){
        this.message = message;
        this.content = content;
    }



}
