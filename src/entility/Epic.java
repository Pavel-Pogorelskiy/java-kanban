package entility;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList <Integer> subtaskIds;
    private String status;

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

}