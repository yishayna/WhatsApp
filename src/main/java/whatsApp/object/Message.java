package whatsApp.object;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected String content;
    protected String message ;

    public String getContent() {
        return this.content;
    }
    public String getMessage() {
        return this.message;
    }

}
