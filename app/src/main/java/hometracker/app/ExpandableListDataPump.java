package hometracker.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> livingRoom = new ArrayList<String>();
        livingRoom.add("Big Cabinet");
        livingRoom.add("Small Cabinet");
        livingRoom.add("White cupboard");
        livingRoom.add("Brown cupboard");
        livingRoom.add("Shelf");

        List<String> storageRoom = new ArrayList<String>();
        storageRoom.add("Big Cabinet");
        storageRoom.add("Small Cabinet");
        storageRoom.add("White cupboard");
        storageRoom.add("Brown cupboard");
        storageRoom.add("Shelf");

        List<String> bathRoom = new ArrayList<String>();
        bathRoom.add("Big Cabinet");
        bathRoom.add("Small Cabinet");
        bathRoom.add("White cupboard");
        bathRoom.add("Brown cupboard");
        bathRoom.add("Shelf");

        expandableListDetail.put("Living room", livingRoom);
        expandableListDetail.put("Storage room", storageRoom);
        expandableListDetail.put("Bath", bathRoom);
        return expandableListDetail;
    }
}