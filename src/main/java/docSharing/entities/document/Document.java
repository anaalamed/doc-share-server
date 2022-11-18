package docSharing.entities.document;

import docSharing.entities.User;


public class Document extends File{

    private Content content;

    public Document(int id,User user,File parent,String title,String url) {
        super(id, user,parent,title,url);
        this.content =new Content("");
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }
}
