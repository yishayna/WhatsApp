package whatsApp.object;

public class CreateGroup extends Message {
    private final String groupName;
    private  String admin;


    public CreateGroup(String groupName) {
        this.groupName = groupName;
    }

    public CreateGroup(Message msg, String admin) {
        CreateGroup msgG = (CreateGroup)msg;
        this.groupName = msgG.getGroupName();
        this.admin = admin;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAdmin() {
        return admin;
    }
}
