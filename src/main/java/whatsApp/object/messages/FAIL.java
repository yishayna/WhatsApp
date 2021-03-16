package whatsApp.object.messages;


import whatsApp.object.Message;

public class FAIL extends Message {

    public FAIL (String message){
        this.message = message;
    }

    public FAIL (String message, String content){
        this.message = message;
        this.content = content;
    }



}
