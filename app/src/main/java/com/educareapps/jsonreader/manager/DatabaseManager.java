package com.educareapps.jsonreader.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.educareapps.jsonreader.dao.DaoMaster;
import com.educareapps.jsonreader.dao.DaoMaster.DevOpenHelper;
import com.educareapps.jsonreader.dao.DaoSession;
import com.educareapps.jsonreader.dao.Item;
import com.educareapps.jsonreader.dao.ItemDao;
import com.educareapps.jsonreader.dao.Result;
import com.educareapps.jsonreader.dao.Task;
import com.educareapps.jsonreader.dao.TaskDao;
import com.educareapps.jsonreader.dao.TaskPack;
import com.educareapps.jsonreader.dao.User;
import com.educareapps.jsonreader.dao.UserDao;
import com.educareapps.jsonreader.dao.UserDao.Properties;
import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.WhereCondition;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseManager implements IDatabaseManager, AsyncOperationListener {
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    private static DatabaseManager instance;
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations = new CopyOnWriteArrayList();
    private Context context;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase database;
    private DevOpenHelper mHelper = new DevOpenHelper(this.context, "sample-database", null);

    public DatabaseManager(Context context) {
        this.context = context;
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public void onAsyncOperationCompleted(AsyncOperation operation) {
        this.completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        this.asyncSession.waitForCompletion(1000);
        this.asyncSession.isCompleted();
    }

    public void openReadableDb() throws SQLiteException {
        this.database = this.mHelper.getReadableDatabase();
        this.daoMaster = new DaoMaster(this.database);
        this.daoSession = this.daoMaster.newSession();
        this.asyncSession = this.daoSession.startAsyncSession();
        this.asyncSession.setListener(this);
    }

    public void openWritableDb() throws SQLiteException {
        this.database = this.mHelper.getWritableDatabase();
        this.daoMaster = new DaoMaster(this.database);
        this.daoSession = this.daoMaster.newSession();
        this.asyncSession = this.daoSession.startAsyncSession();
        this.asyncSession.setListener(this);
    }

    public void closeDbConnections() {
        if (this.daoSession != null) {
            this.daoSession.clear();
            this.daoSession = null;
        }
        if (this.database != null && this.database.isOpen()) {
            this.database.close();
        }
        if (this.mHelper != null) {
            this.mHelper.close();
            this.mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            DaoMaster.dropAllTables(this.database, true);
            this.mHelper.onCreate(this.database);
            this.asyncSession.deleteAll(User.class);
            this.asyncSession.deleteAll(Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized User insertUser(User user) {
        if (user != null) {
            try {
                openWritableDb();
                this.daoSession.getUserDao().insert(user);
                Log.d(TAG, "Inserted user: " + user.getName() + " to the schema.");
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public synchronized ArrayList<User> listUsers() {
        ArrayList<User> arrayList;
        List<User> users = null;
        try {
            openReadableDb();
            users = this.daoSession.getUserDao().loadAll();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (users != null) {
            arrayList = new ArrayList(users);
        } else {
            arrayList = null;
        }
        return arrayList;
    }

    public synchronized void updateUser(User user) {
        if (user != null) {
            try {
                openWritableDb();
                this.daoSession.update(user);
                Log.d(TAG, "Updated user: " + user.getName() + " from the schema.");
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void deleteUserByEmail(String email) {
        try {
            openWritableDb();
            UserDao userDao = this.daoSession.getUserDao();
            List<User> userToDelete = userDao.queryBuilder().where(Properties.Name.eq(email), new WhereCondition[0]).list();
            for (User user : userToDelete) {
                userDao.delete(user);
            }
            this.daoSession.clear();
            Log.d(TAG, userToDelete.size() + " entry. " + "Deleted user: " + email + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public synchronized boolean deleteUserById(Long userId) {
        boolean z;
        try {
            openWritableDb();
            this.daoSession.getUserDao().deleteByKey(userId);
            this.daoSession.clear();
            z = true;
        } catch (Exception e) {
            e.printStackTrace();
            z = false;
        }
        return z;
    }

    public synchronized User getUserById(Long userId) {
        User user;
        user = null;
        try {
            openReadableDb();
            user = (User) this.daoSession.getUserDao().load(userId);
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public synchronized void deleteUsers() {
        try {
            openWritableDb();
            this.daoSession.getUserDao().deleteAll();
            this.daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long insertTask(Task task) {
        Long id = null;
        if (task == null) {
            return null;
        }
        try {
            openWritableDb();
            id = Long.valueOf(this.daoSession.getTaskDao().insert(task));
            Log.d(TAG, "Inserted task: " + task.getName() + " to the schema.");
            this.daoSession.clear();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return id;
        }
    }

    public List<Task> listTasks() {
        List<Task> tasks;
        List<Task> tasks2 = null;
        try {
            openReadableDb();
            tasks2 = this.daoSession.getTaskDao().loadAll();
            this.daoSession.clear();
            tasks = tasks2;
        } catch (Exception e) {
            e.printStackTrace();
            tasks = tasks2;
        }
        if (tasks != null) {
            return new ArrayList(tasks);
        }
        tasks2 = tasks;
        return tasks;
    }

    public List<Task> listTasksByTAskPackId(long taskPackId) {
        List<Task> tasks;
        List<Task> tasks2 = null;
        try {
            openWritableDb();
            tasks2 = this.daoSession.getTaskDao().queryBuilder().where(TaskDao.Properties.TaskPackId.eq(Long.valueOf(taskPackId)), new WhereCondition[0]).orderAsc(TaskDao.Properties.SlideSequence).list();
            this.daoSession.clear();
            tasks = tasks2;
        } catch (Exception e) {
            e.printStackTrace();
            tasks = tasks2;
        }
        if (tasks != null) {
            return new ArrayList(tasks);
        }
        tasks2 = tasks;
        return tasks;
    }

    public void updateTask(Task task) {
        if (task != null) {
            try {
                openWritableDb();
                this.daoSession.update(task);
                Log.d(TAG, "Updated task: " + task.getName() + " from the schema.");
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteTaskById(Long taskId) {
        try {
            openWritableDb();
            this.daoSession.getTaskDao().deleteByKey(taskId);
            this.daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Task getTaskById(Long taskId) {
        Task task = null;
        try {
            openReadableDb();
            task = (Task) this.daoSession.getTaskDao().load(taskId);
            this.daoSession.clear();
            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return task;
        }
    }

    public void deleteTasks() {
        try {
            openWritableDb();
            this.daoSession.getTaskDao().deleteAll();
            this.daoSession.clear();
            Log.d(TAG, "Delete all tasks from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User userWiseTask() {
        User user = null;
        try {
            Throwable th;
            openReadableDb();
            ArrayList<User> users = new ArrayList();
            Cursor cursor = this.daoSession.getDatabase().rawQuery("Select * from User", null);
            try {
                DateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                if (cursor.moveToFirst()) {
                    do {
                        User user2 = user;
                        try {
                            user = new User();
                            user.setId(Long.valueOf(cursor.getString(cursor.getColumnIndex(Properties.Id.columnName))));
                            user.setName(cursor.getString(cursor.getColumnIndex(Properties.Password.columnName)));
                            user.setPassword(cursor.getString(cursor.getColumnIndex(Properties.Password.columnName)));
                            user.setActive(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Properties.Active.columnName))).booleanValue());
                            users.add(user);
                        } catch (Throwable th2) {
                            th = th2;
                            user = user2;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                this.daoSession.clear();
                return user;
            } catch (Throwable th3) {
                th = th3;
            }
            cursor.close();
            throw th;
        } catch (Exception e) {
            e.printStackTrace();
            return user;
        }
    }

    public long insertItem(Item item) {
        Long id = null;
        if (item != null) {
            try {
                openWritableDb();
                id = Long.valueOf(this.daoSession.getItemDao().insert(item));
                Log.d(TAG, "Inserted task: " + item.getX() + " to the schema.");
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return id.longValue();
    }

    public List<Item> listItems() {
        List<Item> list = null;
        try {
            openReadableDb();
            list = this.daoSession.getItemDao().loadAll();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList(list);
        }
        return list;
    }

    public void updateItem(Item item) {
        if (item != null) {
            try {
                openWritableDb();
                this.daoSession.update(item);
                Log.d(TAG, "Updated item: " + item.getX() + " from the schema.");
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteItemById(Long itemId) {
        try {
            openWritableDb();
            this.daoSession.getItemDao().deleteByKey(itemId);
            this.daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Item getItemById(Long itemId) {
        Item item = null;
        try {
            openReadableDb();
            item = (Item) this.daoSession.getItemDao().load(itemId);
            this.daoSession.clear();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return item;
        }
    }

    public void deleteItems() {
        try {
            openWritableDb();
            this.daoSession.getItemDao().deleteAll();
            this.daoSession.clear();
            Log.d(TAG, "Delete all items from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Result insertResult(Result result) {
        if (result != null) {
            try {
                openWritableDb();
                this.daoSession.getResultDao().insert(result);
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<Result> listResults() {
        List<Result> list = null;
        try {
            openReadableDb();
            list = this.daoSession.getResultDao().loadAll();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null) {
            return new ArrayList(list);
        }
        return list;
    }

    public void updateResult(Result result) {
        if (result != null) {
            try {
                openWritableDb();
                this.daoSession.update(result);
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteResultById(Long resultId) {
        try {
            openWritableDb();
            this.daoSession.getResultDao().deleteByKey(resultId);
            this.daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Result getResultById(Long resultId) {
        Result result = null;
        try {
            openReadableDb();
            result = (Result) this.daoSession.getResultDao().load(resultId);
            this.daoSession.clear();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public void deleteResults() {
        try {
            openWritableDb();
            this.daoSession.getResultDao().deleteAll();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getCorrectResult(Long key) {
        boolean result = false;
        Cursor cursor;
        try {
            openReadableDb();
            ArrayList<User> users = new ArrayList();
            cursor = this.daoSession.getDatabase().rawQuery("Select * from Result where key =" + key, null);
            if (cursor.moveToFirst()) {
                do {
                    result = true;
                } while (cursor.moveToNext());
            } else {
                result = false;
            }
            cursor.close();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            cursor.close();
        }
        return result;
    }

    public LinkedHashMap<Long, Item> loadTaskWiseItem(Task task) {
        LinkedHashMap<Long, Item> items = new LinkedHashMap();
        Cursor cursor;
        try {
            openReadableDb();
            cursor = this.daoSession.getDatabase().rawQuery("Select * from Item where task =" + task.getId(), null);
            if (cursor.moveToFirst()) {
                do {
                    Item item = new Item();
                    item.setId(Long.valueOf(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Id.columnName))));
                    item.setX(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.f11X.columnName))));
                    item.setY(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.f12Y.columnName))));
                    item.setRotation(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.Rotation.columnName))));
                    item.setKey(Long.valueOf(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Key.columnName))));
                    item.setIsCircleView(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.IsCircleView.columnName))));
                    item.setCircleColor(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CircleColor.columnName))));
                    item.setUserText(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.UserText.columnName)));
                    item.setTextColor(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TextColor.columnName))));
                    item.setTextSize(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TextSize.columnName))));
                    item.setBorderColor(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BorderColor.columnName))));
                    item.setBackgroundColor(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BackgroundColor.columnName))));
                    item.setDrawable(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.Drawable.columnName))));
                    item.setWidth(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Width.columnName))));
                    item.setHeight(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Height.columnName))));
                    item.setLeft(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Left.columnName))));
                    item.setRight(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Right.columnName))));
                    item.setTop(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Top.columnName))));
                    item.setBottom(Float.valueOf(cursor.getFloat(cursor.getColumnIndex(ItemDao.Properties.Bottom.columnName))));
                    item.setImagePath(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ImagePath.columnName)));
                    item.setTaskId(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.TaskId.columnName)));
                    item.setType(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.Type.columnName)));
                    item.setResult(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.Result.columnName)));
                    item.setAllowDragDrop(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AllowDragDrop.columnName)));
                    item.setDragDropTarget(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.DragDropTarget.columnName)));
                    item.setNavigateTo(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.NavigateTo.columnName)));
                    item.setOpenApp(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.OpenApp.columnName)));
                    item.setShowedBy(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.ShowedBy.columnName)));
                    item.setHideBy(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.HideBy.columnName)));
                    item.setCornerRound(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CornerRound.columnName)));
                    item.setCloseApp(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.CloseApp.columnName)));
                    item.setOpenUrl(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.OpenUrl.columnName)));
                    item.setItemSound(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ItemSound.columnName)));
                    item.setFontTypeFace(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.FontTypeFace.columnName)));
                    item.setFontAlign(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.FontAlign.columnName)));
                    item.setAutoPlay(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.AutoPlay.columnName)));
                    item.setSoundDelay(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.SoundDelay.columnName)));
                    item.setBorderPixel(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.BorderPixel.columnName)));
                    item.setShowedByTarget(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ShowedByTarget.columnName)));
                    item.setHiddenByTarget(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.HiddenByTarget.columnName)));
                    item.setHelper(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.Helper.columnName)));
                    item.setTutorialX(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TutorialX.columnName)));
                    item.setTutorialY(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TutorialY.columnName)));
                    item.setTutorialTag(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.TutorialTag.columnName)));
                    item.setTutorialAnimation(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.TutorialAnimation.columnName)));
                    item.setShowedMiniFeedBack(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ShowedMiniFeedBack.columnName)));
                    item.setHideMiniFeedback(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.HideMiniFeedback.columnName)));
                    item.setExtraOne(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.ExtraOne.columnName))));
                    item.setExtraTwo(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.ExtraTwo.columnName))));
                    item.setExtraThree(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ExtraThree.columnName)));
                    item.setExtraFour(cursor.getString(cursor.getColumnIndex(ItemDao.Properties.ExtraFour.columnName)));
                    item.setExtraFive(Integer.valueOf(cursor.getInt(cursor.getColumnIndex(ItemDao.Properties.ExtraFive.columnName))));
                    items.put(Long.valueOf(cursor.getLong(cursor.getColumnIndex(ItemDao.Properties.Key.columnName))), item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            cursor.close();
        }
        return items;
    }

    public Long insertTaskPack(TaskPack taskPask) {
        Long id = null;
        if (taskPask == null) {
            return null;
        }
        try {
            openWritableDb();
            id = Long.valueOf(this.daoSession.getTaskPackDao().insert(taskPask));
            Log.d(TAG, "Inserted task: " + taskPask.getName() + " to the schema.");
            this.daoSession.clear();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return id;
        }
    }

    public List<TaskPack> listTaskPacks() {
        List<TaskPack> taskPacks;
        List<TaskPack> taskPacks2 = null;
        try {
            openReadableDb();
            taskPacks2 = this.daoSession.getTaskPackDao().loadAll();
            this.daoSession.clear();
            taskPacks = taskPacks2;
        } catch (Exception e) {
            e.printStackTrace();
            taskPacks = taskPacks2;
        }
        if (taskPacks != null) {
            return new ArrayList(taskPacks);
        }
        taskPacks2 = taskPacks;
        return taskPacks;
    }

    public void updateTaskPack(TaskPack taskPack) {
        if (taskPack != null) {
            try {
                openWritableDb();
                this.daoSession.update(taskPack);
                this.daoSession.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean deleteTaskPackById(Long taskPackId) {
        try {
            openWritableDb();
            this.daoSession.getTaskPackDao().deleteByKey(taskPackId);
            this.daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public TaskPack getTaskPackById(Long taskPackId) {
        TaskPack taskPack = null;
        try {
            openReadableDb();
            taskPack = (TaskPack) this.daoSession.getTaskPackDao().load(taskPackId);
            this.daoSession.clear();
            return taskPack;
        } catch (Exception e) {
            e.printStackTrace();
            return taskPack;
        }
    }

    public void deleteTaskPacks() {
    }

    public void deleteTasksByTaskPack(long taskPackId) {
        try {
            openWritableDb();
            TaskDao taskDao = this.daoSession.getTaskDao();
            for (Task task : taskDao.queryBuilder().where(TaskDao.Properties.TaskPackId.eq(Long.valueOf(taskPackId)), new WhereCondition[0]).list()) {
                taskDao.delete(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getMaxTaskPosition(long taskPackId) {
        openWritableDb();
        List<Task> tasks = this.daoSession.getTaskDao().queryBuilder().where(TaskDao.Properties.TaskPackId.eq(Long.valueOf(taskPackId)), new WhereCondition[0]).orderDesc(TaskDao.Properties.SlideSequence).limit(1).list();
        if (tasks.size() > 0) {
            return ((Task) tasks.get(0)).getSlideSequence();
        }
        return 0;
    }

    public void deleteItemsByTask(long taskId) {
        try {
            openWritableDb();
            ItemDao itemDao = this.daoSession.getItemDao();
            for (Item task : itemDao.queryBuilder().where(ItemDao.Properties.Task.eq(Long.valueOf(taskId)), new WhereCondition[0]).list()) {
                itemDao.delete(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> loadItemByTask(Task task) {
        try {
            openWritableDb();
            ArrayList<Item> items = (ArrayList) this.daoSession.getItemDao().queryBuilder().where(ItemDao.Properties.Task.eq(task.getId()), new WhereCondition[0]).list();
            this.daoSession.clear();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Task> searchSoundFromTask(String sound) {
        List<Task> tasks = null;
        try {
            openWritableDb();
            tasks = this.daoSession.getTaskDao().queryBuilder().whereOr(TaskDao.Properties.PositiveSound.eq(sound), TaskDao.Properties.NegativeSound.eq(sound), TaskDao.Properties.FeedbackSound.eq(sound)).list();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tasks != null) {
            return new ArrayList(tasks);
        }
        return null;
    }

    public ArrayList<Item> searchSoundFromItem(String sound) {
        List<Item> items = null;
        try {
            openWritableDb();
            items = this.daoSession.getItemDao().queryBuilder().where(ItemDao.Properties.ItemSound.eq(sound), new WhereCondition[0]).list();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (items != null) {
            return new ArrayList(items);
        }
        return null;
    }

    public ArrayList<Item> searchImageFromItem(String imgPath) {
        List<Item> items = null;
        try {
            openWritableDb();
            items = this.daoSession.getItemDao().queryBuilder().where(ItemDao.Properties.ImagePath.eq(imgPath), new WhereCondition[0]).list();
            this.daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (items != null) {
            return new ArrayList(items);
        }
        return null;
    }
}
