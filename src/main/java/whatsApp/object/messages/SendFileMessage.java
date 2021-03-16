package whatsApp.object.messages;

import whatsApp.object.Message;

public class SendFileMessage extends Message {

    final private String filePath;
    private byte[] file;
    final private String target ;
    private String source ;
    private String time;

    public SendFileMessage(String target, byte[] file, String filePath){
        this.filePath = filePath;
        this.file = file;
        this.target = target;
        this.source = null;
        this.time = null;
    }


    public SendFileMessage(Message msg, String source, String time){
        SendFileMessage msgT = (SendFileMessage) msg;
        this.file = msgT.file;
        this.target = msgT.target;
        this.filePath = msgT.filePath;
        this.source = source;
        this.time = time;
        this.message=msg.getMessage();
    }

    public byte[] getFile() {
        return this.file;
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

    public String getFilePath() {
        return filePath;
    }
}
