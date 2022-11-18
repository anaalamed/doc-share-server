package docSharing.controller.request;


public class UpdateRequest {
    private String user;
    private UpdateType type;
    private String content;
    private int position;

    public UpdateRequest() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public enum UpdateType{
        DELETE,
        APPEND,
        DELETE_RANGE,
        APPEND_RANGE
    }

    @Override
    public String toString() {
        return "UpdateRequest{" +
                "user='" + user + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", position=" + position +
                '}';
    }
}