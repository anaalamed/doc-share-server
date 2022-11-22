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
    private final List<Integer> usersIdList;

    public UserIDList() {
        this.usersIdList = new ArrayList<>();
    }

    public void add(int userId) {
        this.usersIdList.add(userId);
    }

    public void remove(Integer userId) {
        this.usersIdList.remove(userId);
    }

    public boolean contains(int userId) {
        return this.usersIdList.contains(userId);
    }
}
