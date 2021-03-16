package whatsApp.object.messages;

import whatsApp.object.Message;

public class SendFileGroup extends Message {
    private final String groupName;
    private  String sourceUser;
    final private String filePath;
    private byte[] file;


    public SendFileGroup(String groupName,  String filePath, byte[] file) {
        this.groupName = groupName;
        this.sourceUser = null;
        this.filePath = filePath;
        this.file = file;
    }

    public SendFileGroup(Message msg, String sourceUser) {
        this.sourceUser = sourceUser;
        this.groupName = ((SendFileGroup)msg).groupName;
        this.filePath = ((SendFileGroup)msg).filePath;
        this.file = ((SendFileGroup)msg).file;
        this.message=msg.getMessage();
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public byte[] getFile() {
        return file;
    }

    public String getFilePath() {
        return filePath;
    }
}
