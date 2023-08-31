package service;

import entility.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatHandler {
    private static final String HEADER = "id,type,name,status,description,epic";
    private static final String DELIMETER = ",";
    public static String toString(Task task) {
        String result = task.getId() + DELIMETER +
                task.getType() + DELIMETER +
                task.getName() + DELIMETER +
                task.getStatus() + DELIMETER +
                task.getDescription();
        if (task.getType() == Type.SUBTASK) {
            result = result + DELIMETER + ((SubTask) task).getEpicId();
        }
        return result;
    }

   public static Task fromString(String value) {
        String [] text = value.split(",");
        Status status;
        if (text[3].equals("NEW")) {
            status = Status.NEW;// id,type,name,status,description,epic
        } else if (text[3].equals("IN_PROGRESS")) {
            status = Status.IN_PROGRESS;// id,type,name,status,description,epic
        } else {
            status = Status.DONE;
        }
       switch (text[1]) {
           case "TASK":
            Task task = new Task(text[2], text[4]);
            task.setStatus(status);
            task.setId(Integer.parseInt(text[0]));
            return task;
           case "EPIC":
               Epic epic = new Epic(text[2], text[4]);
               epic.setStatus(status);
               epic.setId(Integer.parseInt(text[0]));
               return epic;
           case "SUBTASK":
               SubTask subTask = new SubTask(text[2], text[4], Integer.parseInt(text[5]));
               subTask.setStatus(status);
               subTask.setId(Integer.parseInt(text[0]));
               return subTask;
           default:
               System.out.println("Ошибка в работе");
               return null;
       }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder stringHistory = new StringBuilder(new String());
        if (manager.getHistory() != null) {
            for (Task task : manager.getHistory()) {
                stringHistory.append(task.getId() + ",");
            }
            if (stringHistory.length() > 0) {
                stringHistory.deleteCharAt(stringHistory.length() - 1);
            }
        }
        return stringHistory.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List <Integer> numbers = new ArrayList <>();
        String [] number = value.split(",");
        for (int i = 0; i < number.length; i++) {
            numbers.add(Integer.parseInt(number[i]));
        }
        return numbers;
    }

    public static String getHeader() {
        return HEADER;
    }
}
