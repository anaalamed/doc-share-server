package docSharing.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserIDList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @ElementCollection
    private final List<Integer> usersId;

    public UserIDList() {
        this.usersId = new ArrayList<>();
    }

    public void add(int userId) {
        this.usersId.add(userId);
    }

    public void remove(int userId) {
        this.usersId.remove(userId);
    }

    public boolean contains(int userId) {
        return this.usersId.contains(userId);
    }
}
