package whatsApp.object.messages;

import whatsApp.object.Message;

public class SendTextGroup extends Message {
    private final String groupName;
    private  String sourceUser;
    private String time;

    public SendTextGroup(String groupName, String message) {
        this.groupName = groupName;
        this.message=message;
    }

    public SendTextGroup(Message msg, String sourceUser, String time) {
        this.groupName = ((SendTextGroup) msg).groupName;
        this.sourceUser = sourceUser;
        this.time = time;
        this.message=msg.getMessage();
    }

    public String getSourceUser() {
        return sourceUser;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTime() {
        return time;
    }
}
