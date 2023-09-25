package entility;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        type = Type.SUBTASK;
        status = Status.NEW;
    }

    public SubTask(String name, String description, int epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        type = Type.SUBTASK;
        status = Status.NEW;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            SubTask t = (SubTask) obj;
            return t.getName().equals(getName())&&t.getDescription().equals(getDescription())&&t.getId()
                    == (getId())&&t.getStatus().equals(getStatus())&&t.getType().equals(getType())&&t.getEpicId() == getEpicId();
    }
}
