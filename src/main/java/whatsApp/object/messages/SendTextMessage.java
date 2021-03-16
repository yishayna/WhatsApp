package whatsApp.object.messages;

import whatsApp.object.Message;

public class SendTextMessage extends Message {
    final private String target ;
    private String source ;
    private String time;

    public SendTextMessage(String target, String message){
        this.message = message;
        this.target = target;
        this.source = null;
        this.time = null;
    }

    public SendTextMessage(Message msg, String source, String time){
        SendTextMessage msgT = (SendTextMessage) msg;
        this.message = msgT.message;
        this.target = msgT.target;
        this.source = source;
        this.time = time;
        this.message=msg.getMessage();
    }

    public String getTarget() {
        return target;
    }

    public String getSource() {
        return source;
    }

    public String getTime() {
        return time;
    }
}
