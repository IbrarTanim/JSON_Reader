package com.educareapps.jsonreader.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.educareapps.jsonreader.C0189R;
import com.educareapps.jsonreader.dao.Item;
import com.educareapps.jsonreader.dao.ItemDao;
import com.educareapps.jsonreader.dao.Task;
import com.educareapps.jsonreader.dao.TaskDao;
import com.educareapps.jsonreader.dao.TaskPack;
import com.educareapps.jsonreader.dao.TaskPackDao;
import com.educareapps.jsonreader.dao.User;
import com.educareapps.jsonreader.dao.UserDao;
import com.educareapps.jsonreader.dao.UserDao.Properties;
import com.educareapps.jsonreader.manager.DatabaseManager;
import com.educareapps.jsonreader.manager.IDatabaseManager;
import com.educareapps.jsonreader.utilitis.StaticAccess;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Share {
    public String LabImage;
    public String LabSound;
    public String appDataPath;
    public String appImagePath;
    public String appSoundPath;
    Context context;
    private IDatabaseManager databaseManager;
    public String generatedPathDel;
    public String jsonFileName;
    public String receivedImagePath;
    public String receivedJSONPath;
    public String receivedLabImage;
    public String receivedLabSound;
    public String receivedMainPath;
    public String receivedSoundPath;
    public String shareImagePath = (Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE);
    public String shareJSONPath = (Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED + StaticAccess.JSON);
    public String shareLabImage = (Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED + StaticAccess.ANDROID_DATA_LABIMAGE);
    public String shareLabSound = (Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED + StaticAccess.ANDROID_DATA_LABSOUND);
    public String shareSoundPath = (Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED + StaticAccess.ANDROID_DATA_PACKAGE_SOUND);
    public String zipCreatePath;
    public String zipPath;

    public Share(Context context) {
        this.context = context;
        this.databaseManager = new DatabaseManager(context);
        this.appDataPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName();
        this.appImagePath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        this.appSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
        this.receivedMainPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH;
        this.receivedJSONPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.JSON;
        this.receivedImagePath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_PACKAGE_IMAGE;
        this.receivedSoundPath = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_PACKAGE_SOUND;
        this.receivedLabImage = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_LABIMAGE;
        this.receivedLabSound = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.RECEIVED_PATH + StaticAccess.ANDROID_DATA_LABSOUND;
        this.LabImage = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_LABIMAGE;
        this.LabSound = Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + context.getPackageName() + StaticAccess.ANDROID_DATA_LABSOUND;
        this.jsonFileName = StaticAccess.JSON_FILE_NAME;
        this.zipPath = Environment.getExternalStorageDirectory() + StaticAccess.ORANGE_APP_GENERATED_ROOT_SLASH;
        this.generatedPathDel = Environment.getExternalStorageDirectory() + StaticAccess.SD_CARD_ROOT_ORANGE_APP_GENERATED;
        this.zipCreatePath = Environment.getExternalStorageDirectory() + StaticAccess.ORANGE_APP_NAME;
    }

    public String generateJSON() throws JSONException {
        ArrayList<User> users = this.databaseManager.listUsers();
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Iterator it = users.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            JSONObject userData = new JSONObject();
            userData.put(Properties.Id.columnName, user.getId());
            userData.put(Properties.Name.columnName, user.getName());
            userData.put(Properties.Password.columnName, user.getPassword());
            userData.put(Properties.Active.columnName, user.getActive());
            userData.put(Properties.CreatedAt.columnName, user.getCreatedAt());
            userData.put(Properties.UpdatedAt.columnName, user.getUpdatedAt());
            jsonArray.put(userData);
        }
        json.put(UserDao.TABLENAME, jsonArray);
        try {
            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + this.context.getPackageName() + StaticAccess.JSON);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }
            String fileName = StaticAccess.FILE_NAME + System.currentTimeMillis() + StaticAccess.DOT_JSON;
            FileWriter writer = new FileWriter(new File(sdCardDirectory, fileName));
            writer.append(json.toString());
            writer.flush();
            writer.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void readJSON(String jsonFile) {
        FileInputStream fileInputStream=null;
        try {
            fileInputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory() + StaticAccess.ANDROID_DATA + this.context.getPackageName() + StaticAccess.JSON, jsonFile));
            FileChannel fileChannel = fileInputStream.getChannel();
            String jsonStr = Charset.defaultCharset().decode(fileChannel.map(MapMode.READ_ONLY, 0, fileChannel.size())).toString();
            fileInputStream.close();
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray(UserDao.TABLENAME);
            ArrayList<User> users = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy");
                User user = new User();
                user.setId(Long.valueOf(jsonObject.getString(Properties.Id.columnName)));
                user.setName(jsonObject.getString(Properties.Name.columnName));
                user.setPassword(jsonObject.getString(Properties.Password.columnName));
                user.setActive(Boolean.valueOf(jsonObject.getString(Properties.Active.columnName)).booleanValue());
                user.setCreatedAt(dateFormat.parse(jsonObject.getString(Properties.CreatedAt.columnName)));
                user.setUpdatedAt(dateFormat.parse(jsonObject.getString(Properties.UpdatedAt.columnName)));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            fileInputStream.close();
        }
    }

    public String zipDir(String FileName) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String fileName = FileName + StaticAccess.DOT_MAP;
        String zipFile = this.zipCreatePath + fileName;
        File dirObj = new File(this.zipPath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        System.out.println("Creating : " + zipFile);
        addDir(dirObj, out);
        out.close();
        return fileName;
    }

    static void addDir(File dirObj, ZipOutputStream out) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out);
            } else {
                FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
                File curentPath = new File(files[i].getParent());
                System.out.println(" Adding: " + curentPath.getName().toString() + StaticAccess.SLASH + files[i].getName());
                out.putNextEntry(new ZipEntry(curentPath.getName().toString() + StaticAccess.SLASH + files[i].getName()));
                while (true) {
                    int len = in.read(tmpBuf);
                    if (len <= 0) {
                        break;
                    }
                    out.write(tmpBuf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
        }
    }

    public void shareFiles(String fileName) {
        Intent intentShareFile = new Intent("android.intent.action.SEND");
        File file = new File(this.zipCreatePath + fileName);
        if (file.exists()) {
            intentShareFile.setType(StaticAccess.SET_TYPE);
            intentShareFile.putExtra("android.intent.extra.STREAM", Uri.parse(StaticAccess.FILE_SLASH + file.getAbsolutePath()));
            intentShareFile.putExtra("android.intent.extra.SUBJECT", this.context.getString(C0189R.string.task_pack));
            intentShareFile.putExtra("android.intent.extra.TEXT", "");
            this.context.startActivity(Intent.createChooser(intentShareFile, this.context.getString(C0189R.string.share)));
        }
    }

    public void unZip(String zipFile) throws ZipException, IOException {
        System.out.println(zipFile);
        ZipFile zip = new ZipFile(new File(zipFile));
        new File(this.receivedMainPath).mkdir();
        Enumeration zipFileEntries = zip.entries();
        while (zipFileEntries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(this.receivedMainPath, currentEntry);
            destFile.getParentFile().mkdirs();
            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                byte[] data = new byte[2048];
                BufferedOutputStream dest = new BufferedOutputStream(new FileOutputStream(destFile), 2048);
                while (true) {
                    int currentByte = is.read(data, 0, 2048);
                    if (currentByte == -1) {
                        break;
                    }
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
            if (currentEntry.endsWith(StaticAccess.DOT_MAP)) {
                unZip(destFile.getAbsolutePath());
            }
        }
    }

    public void copyForShare(String inputFile, String inputPath, String outputPath) {
        FileNotFoundException fnfe1;
        InputStream inputStream;
        Exception e;
        OutputStream outputStream;
        if (inputFile != null && inputFile.length() > 0) {
            try {
                OutputStream out;
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                InputStream in = new FileInputStream(inputPath + inputFile);
                try {
                    out = new FileOutputStream(outputPath + inputFile);
                } catch (FileNotFoundException e2) {
                    fnfe1 = e2;
                    inputStream = in;
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e3) {
                    e = e3;
                    inputStream = in;
                    Log.e("tag", e.getMessage());
                }
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int read = in.read(buffer);
                        if (read != -1) {
                            out.write(buffer, 0, read);
                        } else {
                            in.close();
                            try {
                                out.flush();
                                out.close();
                                return;
                            } catch (FileNotFoundException e4) {
                                fnfe1 = e4;
                                outputStream = out;
                                Log.e("tag", fnfe1.getMessage());
                            } catch (Exception e5) {
                                e = e5;
                                outputStream = out;
                                Log.e("tag", e.getMessage());
                            }
                        }
                    }
                } catch (FileNotFoundException e6) {
                    fnfe1 = e6;
                    outputStream = out;
                    inputStream = in;
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e7) {
                    e = e7;
                    outputStream = out;
                    inputStream = in;
                    Log.e("tag", e.getMessage());
                }
            } catch (FileNotFoundException e8) {
                fnfe1 = e8;
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e9) {
                e = e9;
                Log.e("tag", e.getMessage());
            }
        }
    }

    public void deleteGeneratedFolder() {
        deleteFile(this.generatedPathDel);
    }

    public void generateTaskPackJSON(ArrayList<TaskPack> taskPacks) throws JSONException {
        JSONArray mainArray = new JSONArray();
        JSONObject finalJsonObject = new JSONObject();
        Iterator it = taskPacks.iterator();
        while (it.hasNext()) {
            TaskPack taskPack = (TaskPack) it.next();
            JSONArray taskArray = new JSONArray();
            Iterator it2 = ((ArrayList) this.databaseManager.listTasksByTAskPackId(taskPack.getId().longValue())).iterator();
            while (it2.hasNext()) {
                Task task = (Task) it2.next();
                LinkedHashMap<Long, Item> items = this.databaseManager.loadTaskWiseItem(task);
                JSONArray itemArray = new JSONArray();
                if (items != null) {
                    for (Entry<Long, Item> itemValue : items.entrySet()) {
                        Item item = (Item) itemValue.getValue();
                        JSONObject itemObject = new JSONObject();
                        itemObject.put(ItemDao.Properties.f11X.columnName, item.getX());
                        itemObject.put(ItemDao.Properties.f12Y.columnName, item.getY());
                        itemObject.put(ItemDao.Properties.Rotation.columnName, item.getRotation());
                        itemObject.put(ItemDao.Properties.Key.columnName, item.getKey());
                        itemObject.put(ItemDao.Properties.IsCircleView.columnName, item.getIsCircleView());
                        itemObject.put(ItemDao.Properties.CircleColor.columnName, item.getCircleColor());
                        itemObject.put(ItemDao.Properties.UserText.columnName, item.getUserText());
                        itemObject.put(ItemDao.Properties.TextColor.columnName, item.getTextColor());
                        itemObject.put(ItemDao.Properties.TextSize.columnName, item.getTextSize());
                        itemObject.put(ItemDao.Properties.BorderColor.columnName, item.getBorderColor());
                        itemObject.put(ItemDao.Properties.BackgroundColor.columnName, item.getBackgroundColor());
                        itemObject.put(ItemDao.Properties.Drawable.columnName, item.getDrawable());
                        itemObject.put(ItemDao.Properties.Width.columnName, item.getWidth());
                        itemObject.put(ItemDao.Properties.Height.columnName, item.getHeight());
                        itemObject.put(ItemDao.Properties.Left.columnName, item.getLeft());
                        itemObject.put(ItemDao.Properties.Right.columnName, item.getRight());
                        itemObject.put(ItemDao.Properties.Top.columnName, item.getTop());
                        itemObject.put(ItemDao.Properties.Bottom.columnName, item.getBottom());
                        itemObject.put(ItemDao.Properties.ImagePath.columnName, item.getImagePath());
                        itemObject.put(ItemDao.Properties.Type.columnName, item.getType());
                        itemObject.put(ItemDao.Properties.OpenApp.columnName, item.getOpenApp());
                        itemObject.put(ItemDao.Properties.Result.columnName, item.getResult());
                        itemObject.put(ItemDao.Properties.AllowDragDrop.columnName, item.getAllowDragDrop());
                        itemObject.put(ItemDao.Properties.DragDropTarget.columnName, item.getDragDropTarget());
                        itemObject.put(ItemDao.Properties.CornerRound.columnName, item.getCornerRound());
                        itemObject.put(ItemDao.Properties.NavigateTo.columnName, item.getNavigateTo());
                        itemObject.put(ItemDao.Properties.ShowedBy.columnName, item.getShowedBy());
                        itemObject.put(ItemDao.Properties.HideBy.columnName, item.getHideBy());
                        itemObject.put(ItemDao.Properties.CloseApp.columnName, item.getCloseApp());
                        itemObject.put(ItemDao.Properties.ItemSound.columnName, item.getItemSound());
                        itemObject.put(ItemDao.Properties.OpenUrl.columnName, item.getOpenUrl());
                        itemObject.put(ItemDao.Properties.FontTypeFace.columnName, item.getFontTypeFace());
                        itemObject.put(ItemDao.Properties.FontAlign.columnName, item.getFontAlign());
                        itemObject.put(ItemDao.Properties.AutoPlay.columnName, item.getAutoPlay());
                        itemObject.put(ItemDao.Properties.SoundDelay.columnName, item.getSoundDelay());
                        itemObject.put(ItemDao.Properties.BorderPixel.columnName, StaticAccess.DEFAULT_BORDER_PIXEL);
                        itemObject.put(ItemDao.Properties.ShowedByTarget.columnName, item.getShowedByTarget());
                        itemObject.put(ItemDao.Properties.HiddenByTarget.columnName, item.getHiddenByTarget());
                        itemObject.put(ItemDao.Properties.Helper.columnName, item.getHelper());
                        itemObject.put(ItemDao.Properties.TutorialX.columnName, item.getTutorialX());
                        itemObject.put(ItemDao.Properties.TutorialY.columnName, item.getTutorialY());
                        itemObject.put(ItemDao.Properties.TutorialTag.columnName, item.getTutorialTag());
                        itemObject.put(ItemDao.Properties.TutorialAnimation.columnName, item.getTutorialAnimation());
                        itemObject.put(ItemDao.Properties.ShowedMiniFeedBack.columnName, item.getShowedMiniFeedBack());
                        itemObject.put(ItemDao.Properties.HideMiniFeedback.columnName, item.getHideMiniFeedback());
                        itemObject.put(ItemDao.Properties.ExtraOne.columnName, item.getExtraOne());
                        itemObject.put(ItemDao.Properties.ExtraTwo.columnName, item.getExtraTwo());
                        itemObject.put(ItemDao.Properties.ExtraThree.columnName, item.getExtraThree());
                        itemObject.put(ItemDao.Properties.ExtraFour.columnName, item.getExtraFour());
                        itemObject.put(ItemDao.Properties.ExtraFive.columnName, item.getExtraFive());
                        itemArray.put(itemObject);
                    }
                }
                JSONObject taskObject = new JSONObject();
                taskObject.put(TaskDao.Properties.UniqId.columnName, task.getUniqId());
                taskObject.put(TaskDao.Properties.Name.columnName, task.getName());
                taskObject.put(TaskDao.Properties.TaskImage.columnName, task.getTaskImage());
                taskObject.put(TaskDao.Properties.Type.columnName, task.getType());
                taskObject.put(TaskDao.Properties.BackgroundColor.columnName, task.getBackgroundColor());
                taskObject.put(TaskDao.Properties.BackgroundImage.columnName, task.getBackgroundImage());
                taskObject.put(TaskDao.Properties.Active.columnName, task.getActive());
                taskObject.put(TaskDao.Properties.CreatedAt.columnName, task.getCreatedAt());
                taskObject.put(TaskDao.Properties.UpdatedAt.columnName, task.getUpdatedAt());
                taskObject.put(TaskDao.Properties.UserId.columnName, task.getUserId());
                taskObject.put(TaskDao.Properties.SlideSequence.columnName, task.getSlideSequence());
                taskObject.put(TaskDao.Properties.FeedbackImage.columnName, task.getFeedbackImage());
                taskObject.put(TaskDao.Properties.FeedbackAnimation.columnName, task.getFeedbackAnimation());
                taskObject.put(TaskDao.Properties.PositiveAnimation.columnName, task.getPositiveAnimation());
                taskObject.put(TaskDao.Properties.NegativeAnimation.columnName, task.getNegativeAnimation());
                taskObject.put(TaskDao.Properties.FeedbackSound.columnName, task.getFeedbackSound());
                taskObject.put(TaskDao.Properties.PositiveSound.columnName, task.getPositiveSound());
                taskObject.put(TaskDao.Properties.NegativeSound.columnName, task.getNegativeSound());
                taskObject.put(TaskDao.Properties.FeedbackType.columnName, task.getFeedbackType());
                taskObject.put(TaskDao.Properties.ErrorBgColor.columnName, task.getErrorBgColor());
                taskObject.put(TaskDao.Properties.Errortext.columnName, task.getErrortext());
                taskObject.put(TaskDao.Properties.ErrorImage.columnName, task.getErrorImage());
                taskObject.put(TaskDao.Properties.ErrorMandatoryScreen.columnName, task.getErrorMandatoryScreen());
                taskObject.put(TaskDao.Properties.SequenceText.columnName, task.getSequenceText());
                taskObject.put(TaskDao.Properties.Template.columnName, task.getTemplate());
                taskObject.put(TaskDao.Properties.Tutorial.columnName, task.getTutorial());
                taskObject.put(TaskDao.Properties.Transition.columnName, task.getTransition());
                taskObject.put(TaskDao.Properties.TaskExtraOne.columnName, task.getTaskExtraOne());
                taskObject.put(TaskDao.Properties.TaskExtraTwo.columnName, task.getTaskExtraTwo());
                taskObject.put(TaskDao.Properties.TaskExtraThree.columnName, task.getTaskExtraThree());
                taskObject.put(TaskDao.Properties.TaskExtraFour.columnName, task.getTaskExtraFour());
                taskObject.put(TaskDao.Properties.TaskExtraFive.columnName, task.getTaskExtraFive());
                taskObject.put(TaskDao.Properties.Bubble.columnName, task.getBubble());
                taskObject.put(ItemDao.TABLENAME, itemArray);
                taskArray.put(taskObject);
            }
            JSONObject taskPackObject = new JSONObject();
            taskPackObject.put(TaskPackDao.Properties.Name.columnName, taskPack.getName());
            taskPackObject.put(TaskPackDao.Properties.LessonNumber.columnName, taskPack.getLessonNumber());
            taskPackObject.put(TaskPackDao.Properties.AgeRange.columnName, taskPack.getAgeRange());
            taskPackObject.put(TaskPackDao.Properties.Description.columnName, taskPack.getDescription());
            taskPackObject.put(TaskPackDao.Properties.Type.columnName, taskPack.getType());
            taskPackObject.put(TaskPackDao.Properties.State.columnName, false);
            taskPackObject.put(TaskPackDao.Properties.CreatedAt.columnName, taskPack.getCreatedAt());
            taskPackObject.put(TaskDao.TABLENAME, taskArray);
            mainArray.put(taskPackObject);
        }
        finalJsonObject.put(TaskPackDao.TABLENAME, mainArray);
        try {
            File sdCardDirectory = new File(this.shareJSONPath);
            if (!sdCardDirectory.exists()) {
                sdCardDirectory.mkdirs();
            }
            FileWriter fileWriter = new FileWriter(new File(sdCardDirectory, this.jsonFileName));
            fileWriter.append(finalJsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readSharedTaskPackJSONtoDatabase(String filePath) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(filePath));
            FileChannel fileChannel = fileInputStream.getChannel();
            String jsonStr = Charset.defaultCharset().decode(fileChannel.map(MapMode.READ_ONLY, 0, fileChannel.size())).toString();
            fileInputStream.close();
            JSONObject jsonObj = new JSONObject(jsonStr);
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy");
            JSONArray taskPackArray = jsonObj.getJSONArray(TaskPackDao.TABLENAME);
            for (int h = 0; h < taskPackArray.length(); h++) {
                JSONObject taskPackObject = taskPackArray.getJSONObject(h);
                TaskPack taskPack = new TaskPack();
                taskPack.setName(taskPackObject.getString(TaskPackDao.Properties.Name.columnName));
                TaskPack taskPack2 = taskPack;
                taskPack2.setLessonNumber(Integer.valueOf(taskPackObject.getInt(TaskPackDao.Properties.LessonNumber.columnName)));
                taskPack.setAgeRange(taskPackObject.getInt(TaskPackDao.Properties.AgeRange.columnName));
                taskPack.setDescription(taskPackObject.getString(TaskPackDao.Properties.Description.columnName));
                taskPack.setType(taskPackObject.getString(TaskPackDao.Properties.Type.columnName));
                try {
                    taskPack2 = taskPack;
                    taskPack2.setState(Boolean.valueOf(taskPackObject.getBoolean(TaskPackDao.Properties.State.columnName)));
                } catch (Exception e) {
                    taskPack.setState(Boolean.valueOf(false));
                }
                Long taskPackId = this.databaseManager.insertTaskPack(taskPack);
                JSONArray taskArray = taskPackObject.getJSONArray(TaskDao.TABLENAME);
                for (int i = 0; i < taskArray.length(); i++) {
                    JSONObject taskObject = taskArray.getJSONObject(i);
                    Task task = new Task();
                    task.setTaskPackId(taskPackId);
                    task.setUniqId(taskObject.getLong(TaskDao.Properties.UniqId.columnName));
                    task.setName(taskObject.getString(TaskDao.Properties.Name.columnName));
                    task.setTaskImage(taskObject.getString(TaskDao.Properties.TaskImage.columnName));
                    task.setType(taskObject.getString(TaskDao.Properties.Type.columnName));
                    Task task2 = task;
                    task2.setBackgroundColor(Integer.valueOf(taskObject.getString(TaskDao.Properties.BackgroundColor.columnName)).intValue());
                    try {
                        task.setBackgroundImage(taskObject.getString(TaskDao.Properties.BackgroundImage.columnName));
                    } catch (Exception e2) {
                        task.setBackgroundImage("");
                    }
                    task2 = task;
                    task2.setActive(Boolean.valueOf(taskObject.getString(TaskDao.Properties.Active.columnName)));
                    task2 = task;
                    task2.setUserId(Long.valueOf(taskObject.getString(TaskDao.Properties.UserId.columnName)).longValue());
                    task2 = task;
                    task2.setSlideSequence(Integer.valueOf(taskObject.getString(TaskDao.Properties.SlideSequence.columnName)).intValue());
                    task.setFeedbackImage(taskObject.getString(TaskDao.Properties.FeedbackImage.columnName));
                    task2 = task;
                    task2.setFeedbackAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.FeedbackAnimation.columnName)).intValue());
                    task2 = task;
                    task2.setPositiveAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.PositiveAnimation.columnName)).intValue());
                    task2 = task;
                    task2.setNegativeAnimation(Integer.valueOf(taskObject.getString(TaskDao.Properties.NegativeAnimation.columnName)).intValue());
                    task.setFeedbackSound(taskObject.getString(TaskDao.Properties.FeedbackSound.columnName));
                    task.setPositiveSound(taskObject.getString(TaskDao.Properties.PositiveSound.columnName));
                    task.setNegativeSound(taskObject.getString(TaskDao.Properties.NegativeSound.columnName));
                    task.setErrorBgColor(taskObject.getInt(TaskDao.Properties.ErrorBgColor.columnName));
                    task.setErrortext(taskObject.getString(TaskDao.Properties.Errortext.columnName));
                    task.setErrorImage(taskObject.getString(TaskDao.Properties.ErrorImage.columnName));
                    task.setErrorMandatoryScreen(taskObject.getInt(TaskDao.Properties.ErrorMandatoryScreen.columnName));
                    task2 = task;
                    task2.setFeedbackType(Integer.valueOf(taskObject.getString(TaskDao.Properties.FeedbackType.columnName)).intValue());
                    task2 = task;
                    task2.setTransition(Integer.valueOf(taskObject.getString(TaskDao.Properties.Transition.columnName)).intValue());
                    task2 = task;
                    task2.setTutorial(Integer.valueOf(taskObject.getString(TaskDao.Properties.Tutorial.columnName)).intValue());
                    task.setSequenceText(taskObject.getString(TaskDao.Properties.SequenceText.columnName));
                    task.setTemplate(taskObject.getInt(TaskDao.Properties.Template.columnName));
                    try {
                        task.setTaskExtraOne(taskObject.getInt(TaskDao.Properties.TaskExtraOne.columnName));
                    } catch (Exception e3) {
                        task.setTaskExtraOne(0);
                    }
                    try {
                        task2 = task;
                        task2.setTaskExtraTwo(Integer.valueOf(taskObject.getInt(TaskDao.Properties.TaskExtraTwo.columnName)));
                    } catch (Exception e4) {
                        task.setTaskExtraTwo(Integer.valueOf(0));
                    }
                    try {
                        task.setTaskExtraThree(taskObject.getString(TaskDao.Properties.TaskExtraThree.columnName));
                    } catch (Exception e5) {
                        task.setTaskExtraThree("");
                    }
                    try {
                        task.setTaskExtraFour(taskObject.getString(TaskDao.Properties.TaskExtraFour.columnName));
                    } catch (Exception e6) {
                        task.setTaskExtraFour("");
                    }
                    try {
                        task2 = task;
                        task2.setTaskExtraFive(Integer.valueOf(taskObject.getInt(TaskDao.Properties.TaskExtraFive.columnName)));
                    } catch (Exception e7) {
                        task.setTaskExtraFive(Integer.valueOf(0));
                    }
                    try {
                        task2 = task;
                        task2.setBubble(Integer.valueOf(taskObject.getInt(TaskDao.Properties.Bubble.columnName)));
                    } catch (Exception e8) {
                        task.setBubble(Integer.valueOf(0));
                    }
                    Long taskId = this.databaseManager.insertTask(task);
                    JSONArray itemArray = taskObject.getJSONArray(ItemDao.TABLENAME);
                    for (int j = 0; j < itemArray.length(); j++) {
                        String str;
                        JSONObject itemObject = itemArray.getJSONObject(j);
                        Item item = new Item();
                        item.setTask(taskId);
                        item.setX(Float.valueOf(itemObject.getString(ItemDao.Properties.f11X.columnName)));
                        item.setY(Float.valueOf(itemObject.getString(ItemDao.Properties.f12Y.columnName)));
                        item.setRotation(Integer.valueOf(itemObject.getString(ItemDao.Properties.Rotation.columnName)));
                        item.setKey(Long.valueOf(itemObject.getString(ItemDao.Properties.Key.columnName)));
                        item.setIsCircleView(Integer.valueOf(itemObject.getString(ItemDao.Properties.IsCircleView.columnName)));
                        item.setCircleColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.CircleColor.columnName)));
                        if (itemObject.getString(ItemDao.Properties.UserText.columnName) == null) {
                            str = "";
                        } else {
                            str = itemObject.getString(ItemDao.Properties.UserText.columnName);
                        }
                        item.setUserText(str);
                        item.setTextColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.TextColor.columnName)));
                        item.setTextSize(Integer.valueOf(itemObject.getString(ItemDao.Properties.TextSize.columnName)));
                        item.setBorderColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.BorderColor.columnName)));
                        item.setBackgroundColor(Integer.valueOf(itemObject.getString(ItemDao.Properties.BackgroundColor.columnName)));
                        item.setDrawable(Integer.valueOf(itemObject.getString(ItemDao.Properties.Drawable.columnName)));
                        item.setWidth(Float.valueOf(itemObject.getString(ItemDao.Properties.Width.columnName)));
                        item.setHeight(Float.valueOf(itemObject.getString(ItemDao.Properties.Height.columnName)));
                        item.setLeft(Float.valueOf(itemObject.getString(ItemDao.Properties.Left.columnName)));
                        item.setRight(Float.valueOf(itemObject.getString(ItemDao.Properties.Right.columnName)));
                        item.setTop(Float.valueOf(itemObject.getString(ItemDao.Properties.Top.columnName)));
                        item.setBottom(Float.valueOf(itemObject.getString(ItemDao.Properties.Bottom.columnName)));
                        item.setImagePath(itemObject.getString(ItemDao.Properties.ImagePath.columnName));
                        item.setType(itemObject.getString(ItemDao.Properties.Type.columnName));
                        if (itemObject.getString(ItemDao.Properties.Result.columnName) == null) {
                            str = "";
                        } else {
                            str = itemObject.getString(ItemDao.Properties.Result.columnName);
                        }
                        item.setResult(str);
                        if (itemObject.getString(ItemDao.Properties.OpenApp.columnName) == null) {
                            str = "";
                        } else {
                            str = itemObject.getString(ItemDao.Properties.OpenApp.columnName);
                        }
                        item.setOpenApp(str);
                        item.setAllowDragDrop(itemObject.getInt(ItemDao.Properties.AllowDragDrop.columnName));
                        item.setDragDropTarget(itemObject.getLong(ItemDao.Properties.DragDropTarget.columnName));
                        item.setCornerRound(itemObject.getInt(ItemDao.Properties.CornerRound.columnName));
                        item.setNavigateTo(itemObject.getLong(ItemDao.Properties.NavigateTo.columnName));
                        item.setShowedBy(itemObject.getLong(ItemDao.Properties.ShowedBy.columnName));
                        item.setHideBy(itemObject.getLong(ItemDao.Properties.HideBy.columnName));
                        item.setCloseApp(itemObject.getInt(ItemDao.Properties.CloseApp.columnName));
                        if (itemObject.getString(ItemDao.Properties.ItemSound.columnName) == null) {
                            str = "";
                        } else {
                            str = itemObject.getString(ItemDao.Properties.ItemSound.columnName);
                        }
                        item.setItemSound(str);
                        if (itemObject.getString(ItemDao.Properties.OpenUrl.columnName) == null) {
                            str = "";
                        } else {
                            str = itemObject.getString(ItemDao.Properties.OpenUrl.columnName);
                        }
                        item.setOpenUrl(str);
                        item.setFontTypeFace(itemObject.getInt(ItemDao.Properties.FontTypeFace.columnName));
                        item.setFontAlign(itemObject.getInt(ItemDao.Properties.FontAlign.columnName));
                        item.setAutoPlay(itemObject.getInt(ItemDao.Properties.AutoPlay.columnName));
                        item.setSoundDelay(itemObject.getInt(ItemDao.Properties.SoundDelay.columnName));
                        item.setBorderPixel(itemObject.getInt(ItemDao.Properties.BorderPixel.columnName));
                        item.setShowedByTarget(itemObject.getString(ItemDao.Properties.ShowedByTarget.columnName));
                        item.setHiddenByTarget(itemObject.getString(ItemDao.Properties.HiddenByTarget.columnName));
                        item.setHelper(itemObject.getInt(ItemDao.Properties.Helper.columnName));
                        item.setBorderPixel(StaticAccess.DEFAULT_BORDER_PIXEL);
                        item.setTutorialX(itemObject.getInt(ItemDao.Properties.TutorialX.columnName));
                        item.setTutorialY(itemObject.getInt(ItemDao.Properties.TutorialY.columnName));
                        item.setTutorialAnimation(itemObject.getInt(ItemDao.Properties.TutorialAnimation.columnName));
                        item.setTutorialTag(itemObject.getLong(ItemDao.Properties.TutorialTag.columnName));
                        item.setShowedMiniFeedBack(itemObject.getString(ItemDao.Properties.ShowedMiniFeedBack.columnName));
                        item.setHideMiniFeedback(itemObject.getString(ItemDao.Properties.HideMiniFeedback.columnName));
                        try {
                            item.setExtraOne(Integer.valueOf(itemObject.getInt(ItemDao.Properties.ExtraOne.columnName)));
                        } catch (Exception e9) {
                            item.setExtraOne(Integer.valueOf(0));
                        }
                        try {
                            item.setExtraTwo(Integer.valueOf(itemObject.getInt(ItemDao.Properties.ExtraTwo.columnName)));
                        } catch (Exception e10) {
                            item.setExtraTwo(Integer.valueOf(0));
                        }
                        try {
                            item.setExtraThree(itemObject.getString(ItemDao.Properties.ExtraThree.columnName));
                        } catch (Exception e11) {
                            item.setExtraThree("");
                        }
                        try {
                            item.setExtraFour(itemObject.getString(ItemDao.Properties.ExtraFour.columnName));
                        } catch (Exception e12) {
                            item.setExtraFour("");
                        }
                        try {
                            item.setExtraFive(Integer.valueOf(itemObject.getInt(ItemDao.Properties.ExtraFive.columnName)));
                        } catch (Exception e13) {
                            item.setExtraFive(Integer.valueOf(0));
                        }
                        this.databaseManager.insertItem(item);
                    }
                }
            }
        } catch (Exception e14) {
            e14.printStackTrace();
        } catch (Throwable th) {
            fileInputStream.close();
        }
    }

    public void deleteReceivedFolder() {
        File dir = new File(this.receivedMainPath);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                new File(dir, file).delete();
            }
        }
    }

    public void deleteFile(String path) {
        if (new File(path).exists()) {
            try {
                Runtime.getRuntime().exec("rm -r " + path);
            } catch (IOException e) {
            }
        }
    }

    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (!sourceLocation.isDirectory()) {
            File directory = targetLocation.getParentFile();
            if (directory == null || directory.exists() || directory.mkdirs()) {
                InputStream in = new FileInputStream(sourceLocation);
                OutputStream out = new FileOutputStream(targetLocation);
                byte[] buf = new byte[1024];
                while (true) {
                    int len = in.read(buf);
                    if (len > 0) {
                        out.write(buf, 0, len);
                    } else {
                        in.close();
                        out.close();
                        return;
                    }
                }
            }
            throw new IOException("Cannot create dir " + directory.getAbsolutePath());
        } else if (targetLocation.exists() || targetLocation.mkdirs()) {
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        } else {
            throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
        }
    }

    public void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectoryWholeFolder(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectoryWholeFolder(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }
        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private void copyFile(File source, File target) throws IOException {
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            while (true) {
                int length = in.read(buf);
                if (length > 0) {
                    out.write(buf, 0, length);
                } else {
                    return;
                }
            }
        } catch (Exception e) {
        }
    }
}
