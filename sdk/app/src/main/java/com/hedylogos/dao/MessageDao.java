package com.hedylogos.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.hedylogos.bean.Message;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MESSAGE.
*/
public class MessageDao extends AbstractDao<Message, String> {

    public static final String TABLENAME = "MESSAGE";

    /**
     * Properties of entity Message.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property ConversationId = new Property(1, String.class, "conversationId", false, "CONVERSATION_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property From = new Property(3, String.class, "from", false, "FROM");
        public final static Property Timestamp = new Property(4, String.class, "timestamp", false, "TIMESTAMP");
        public final static Property MessageId = new Property(5, String.class, "messageId", false, "MESSAGE_ID");
        public final static Property Type = new Property(6, String.class, "type", false, "TYPE");
    };


    public MessageDao(DaoConfig config) {
        super(config);
    }
    
    public MessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MESSAGE' (" + //
                "'ID' TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "'CONVERSATION_ID' TEXT," + // 1: conversationId
                "'CONTENT' TEXT," + // 2: content
                "'FROM' TEXT," + // 3: from
                "'TIMESTAMP' TEXT," + // 4: timestamp
                "'MESSAGE_ID' TEXT," + // 5: messageId
                "'TYPE' TEXT);"); // 6: type
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_MESSAGE_ID ON MESSAGE" +
                " (ID);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MESSAGE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Message entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String conversationId = entity.getConversationId();
        if (conversationId != null) {
            stmt.bindString(2, conversationId);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(4, from);
        }
 
        String timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindString(5, timestamp);
        }
 
        String messageId = entity.getMessageId();
        if (messageId != null) {
            stmt.bindString(6, messageId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Message readEntity(Cursor cursor, int offset) {
        Message entity = new Message( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // conversationId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // from
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // timestamp
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // messageId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Message entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setConversationId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFrom(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTimestamp(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMessageId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Message entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Message entity) {
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
