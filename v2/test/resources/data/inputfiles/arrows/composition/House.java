package v2.test.resources.data.inputfiles.arrows.composition;

import java.util.ArrayList;
import java.util.List;

public class House {
    private List<Room> rooms;

    public House() {
        this.rooms = new ArrayList<>();
        // Composition: House creates and owns Rooms
        rooms.add(new Room("Living Room"));
        rooms.add(new Room("Bedroom"));
        rooms.add(new Room("Kitchen"));
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
