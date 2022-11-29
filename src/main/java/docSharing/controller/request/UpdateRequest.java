package docSharing.controller.request;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UpdateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userEmail;
    private UpdateType type;
    private String content;
    private int startPosition;
    private int endPosition;

    public UpdateRequest() {
    }

    public UpdateRequest(UpdateRequestBuilder builder) {
        this.userEmail = builder.userEmail;
        this.type = builder.type;
        this.content = builder.content;
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int position) {
        this.startPosition = position;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public enum UpdateType{
        DELETE,
        APPEND,
        DELETE_RANGE,
        APPEND_RANGE
    }

    public static class UpdateRequestBuilder {
        private String userEmail;
        private UpdateType type;
        private String content;
        private int startPosition;
        private int endPosition;

        public UpdateRequestBuilder() {
        }

        public UpdateRequestBuilder setUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public UpdateRequestBuilder setType(UpdateType type) {
            this.type = type;
            return this;
        }

        public UpdateRequestBuilder setContent(String content) {
            this.content = content;
            return this;
        }

        public UpdateRequestBuilder setStartPosition(int startPosition) {
            this.startPosition = startPosition;
            return this;
        }

        public UpdateRequestBuilder setEndPosition(int endPosition) {
            this.endPosition = endPosition;
            return this;
        }

        public UpdateRequest build() {
            return new UpdateRequest(this);
        }
    }
}