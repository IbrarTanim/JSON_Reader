package com.educareapps.jsonreader.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.educareapps.jsonreader.dao.TaskPack;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TASK_PACK".
*/
public class TaskPackDao extends AbstractDao<TaskPack, Long> {

    public static final String TABLENAME = "TASK_PACK";

    /**
     * Properties of entity TaskPack.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property LessonNumber = new Property(2, Integer.class, "lessonNumber", false, "LESSON_NUMBER");
        public final static Property AgeRange = new Property(3, int.class, "ageRange", false, "AGE_RANGE");
        public final static Property Description = new Property(4, String.class, "Description", false, "DESCRIPTION");
        public final static Property CreatedAt = new Property(5, java.util.Date.class, "createdAt", false, "CREATED_AT");
        public final static Property State = new Property(6, Boolean.class, "state", false, "STATE");
        public final static Property Type = new Property(7, String.class, "type", false, "TYPE");
    };


    public TaskPackDao(DaoConfig config) {
        super(config);
    }
    
    public TaskPackDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TASK_PACK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"LESSON_NUMBER\" INTEGER," + // 2: lessonNumber
                "\"AGE_RANGE\" INTEGER NOT NULL ," + // 3: ageRange
                "\"DESCRIPTION\" TEXT NOT NULL ," + // 4: Description
                "\"CREATED_AT\" INTEGER," + // 5: createdAt
                "\"STATE\" INTEGER," + // 6: state
                "\"TYPE\" TEXT);"); // 7: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TASK_PACK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, TaskPack entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
 
        Integer lessonNumber = entity.getLessonNumber();
        if (lessonNumber != null) {
            stmt.bindLong(3, lessonNumber);
        }
        stmt.bindLong(4, entity.getAgeRange());
        stmt.bindString(5, entity.getDescription());
 
        java.util.Date createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindLong(6, createdAt.getTime());
        }
 
        Boolean state = entity.getState();
        if (state != null) {
            stmt.bindLong(7, state ? 1L: 0L);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(8, type);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public TaskPack readEntity(Cursor cursor, int offset) {
        TaskPack entity = new TaskPack( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // lessonNumber
            cursor.getInt(offset + 3), // ageRange
            cursor.getString(offset + 4), // Description
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // createdAt
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // state
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, TaskPack entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setLessonNumber(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setAgeRange(cursor.getInt(offset + 3));
        entity.setDescription(cursor.getString(offset + 4));
        entity.setCreatedAt(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setState(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setType(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(TaskPack entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(TaskPack entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}