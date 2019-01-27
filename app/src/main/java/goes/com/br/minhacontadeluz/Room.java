package goes.com.br.minhacontadeluz;

/**
 * Created by matheusgoes on 20/07/15.
 */
public class Room {
    private int id;
    private String name;

    public Room() {
    }

    public Room(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Room(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
