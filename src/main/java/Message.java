import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    private String chat;
    private String username;

    public Message(String username, String chat) {
        this.username = username;
        this.chat = chat;
    }
}
