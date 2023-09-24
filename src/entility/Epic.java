package entility;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList <Integer> subtaskIds;
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        type = Type.EPIC;
        endTime = null;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Epic t = (Epic) obj;
        return t.getName().equals(getName())&&t.getDescription().equals(getDescription())&&t.getId()
                == (getId())&&t.getStatus().equals(getStatus())&&t.getType().equals(getType())&&t.getSubtaskIds().equals(getSubtaskIds());
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}