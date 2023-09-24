package entility;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    final static DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    protected String name;
    protected String description;
    protected Status status;
    protected Integer duration;
    protected LocalDateTime startTime;

    protected Type type;
    protected int id;

    public Task(String name, String description, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.parse(startTime, FORMATTER_TIME);
        this.duration = duration;
        this.status = Status.NEW;
        type = Type.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.startTime = null;
        this.duration = null;
        this.status = Status.NEW;
        type = Type.TASK;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        if (duration == null && getEndTime() == null) {
            return "[name = " + name
                    + ", descripsion = " + description
                    + ", duration=" + duration
                    + ", startTime=null"
                    + ", endTime=" + getEndTime()
                    + ", status = " + status
                    + ", id = " + id + "]";
        } else {
            return "[name = " + name
                    + ", descripsion = " + description
                    + ", duration=" + duration
                    + ", startTime=" + startTime.format(FORMATTER_TIME)
                    + ", endTime=" + getEndTime().format(FORMATTER_TIME)
                    + ", status = " + status
                    + ", id = " + id + "]";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        Task t = (Task) obj;
        return t.getName().equals(getName())&&t.getDescription().equals(getDescription())&&t.getId()
                == (getId())&&t.getStatus().equals(getStatus())&&t.getType().equals(getType());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration == null) {
            return null;
        } else {
            return startTime.plusMinutes(duration);
        }
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
