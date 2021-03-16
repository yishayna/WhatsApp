package whatsApp.object.messages;


import whatsApp.object.Message;

public class GroupDeleted extends Message {
    final private String groupName ;

    public GroupDeleted(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
