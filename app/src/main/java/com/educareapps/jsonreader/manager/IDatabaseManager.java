package com.educareapps.jsonreader.manager;

import com.educareapps.jsonreader.dao.Item;
import com.educareapps.jsonreader.dao.Result;
import com.educareapps.jsonreader.dao.Task;
import com.educareapps.jsonreader.dao.TaskPack;
import com.educareapps.jsonreader.dao.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public interface IDatabaseManager {
    void closeDbConnections();

    boolean deleteItemById(Long l);

    void deleteItems();

    void deleteItemsByTask(long j);

    boolean deleteResultById(Long l);

    void deleteResults();

    boolean deleteTaskById(Long l);

    boolean deleteTaskPackById(Long l);

    void deleteTaskPacks();

    void deleteTasks();

    void deleteTasksByTaskPack(long j);

    void deleteUserByEmail(String str);

    boolean deleteUserById(Long l);

    void deleteUsers();

    void dropDatabase();

    boolean getCorrectResult(Long l);

    Item getItemById(Long l);

    int getMaxTaskPosition(long j);

    Result getResultById(Long l);

    Task getTaskById(Long l);

    TaskPack getTaskPackById(Long l);

    User getUserById(Long l);

    long insertItem(Item item);

    Result insertResult(Result result);

    Long insertTask(Task task);

    Long insertTaskPack(TaskPack taskPack);

    User insertUser(User user);

    List<Item> listItems();

    List<Result> listResults();

    List<TaskPack> listTaskPacks();

    List<Task> listTasks();

    List<Task> listTasksByTAskPackId(long j);

    ArrayList<User> listUsers();

    LinkedHashMap<Long, Item> loadTaskWiseItem(Task task);

    ArrayList<Item> searchImageFromItem(String str);

    ArrayList<Item> searchSoundFromItem(String str);

    ArrayList<Task> searchSoundFromTask(String str);

    void updateItem(Item item);

    void updateResult(Result result);

    void updateTask(Task task);

    void updateTaskPack(TaskPack taskPack);

    void updateUser(User user);

    User userWiseTask();
}
