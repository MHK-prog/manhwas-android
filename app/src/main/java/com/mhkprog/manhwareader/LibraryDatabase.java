package com.mhkprog.manhwareader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Stores the library as a relational tree so SQLite, not a JSON file, is the source of truth. */
final class LibraryDatabase extends SQLiteOpenHelper {
    private static final String TAG = "ManhwaReaderDb";
    private static final String DATABASE_NAME = "manhwa_reader.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE = "library_nodes";
    private static final int MAX_NODES = 200_000;
    private static final int MAX_DEPTH = 64;
    private static final int OBJECT = 1;
    private static final int ARRAY = 2;
    private static final int STRING = 3;
    private static final int NUMBER = 4;
    private static final int BOOLEAN = 5;
    private static final int NULL = 6;

    LibraryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "parent_id INTEGER REFERENCES " + TABLE + "(id) ON DELETE CASCADE, "
                + "child_key TEXT, ordinal INTEGER NOT NULL, kind INTEGER NOT NULL, value TEXT)");
        db.execSQL("CREATE INDEX library_nodes_parent_order ON " + TABLE + "(parent_id, ordinal)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future schema migrations belong here. Version 1 has no older SQLite schema to migrate.
    }

    JSONObject load() throws JSONException {
        SQLiteDatabase db = getReadableDatabase();
        List<Node> nodes = new ArrayList<>();
        try (Cursor cursor = db.query(TABLE,
                new String[]{"id", "parent_id", "child_key", "ordinal", "kind", "value"},
                null, null, null, null, "parent_id ASC, ordinal ASC, id ASC")) {
            while (cursor.moveToNext()) {
                long parent = cursor.isNull(1) ? -1 : cursor.getLong(1);
                nodes.add(new Node(cursor.getLong(0), parent,
                        cursor.isNull(2) ? null : cursor.getString(2), cursor.getInt(3),
                        cursor.getInt(4), cursor.isNull(5) ? null : cursor.getString(5)));
            }
        }
        if (nodes.isEmpty()) return null;

        Map<Long, List<Node>> children = new HashMap<>();
        Node root = null;
        for (Node node : nodes) {
            if (node.parentId < 0) root = node;
            else children.computeIfAbsent(node.parentId, ignored -> new ArrayList<>()).add(node);
        }
        if (root == null || root.kind != OBJECT) return null;
        Object value = buildValue(root, children, 0);
        return value instanceof JSONObject ? (JSONObject) value : null;
    }

    boolean save(JSONObject library) {
        if (library == null) return false;
        SQLiteDatabase db = getWritableDatabase();
        boolean transactionStarted = false;
        try {
            db.beginTransaction();
            transactionStarted = true;
            db.delete(TABLE, null, null);
            int[] count = {0};
            insertValue(db, null, null, 0, library, 0, count);
            db.setTransactionSuccessful();
            return true;
        } catch (Exception error) {
            Log.e(TAG, "Could not save the library transaction", error);
            return false;
        } finally {
            if (transactionStarted) db.endTransaction();
        }
    }

    private long insertValue(SQLiteDatabase db, Long parentId, String key, int ordinal,
                             Object value, int depth, int[] count) throws JSONException {
        if (depth > MAX_DEPTH || ++count[0] > MAX_NODES) {
            throw new IllegalArgumentException("Library data exceeds the database safety limit");
        }
        ContentValues row = new ContentValues();
        if (parentId == null) row.putNull("parent_id"); else row.put("parent_id", parentId);
        if (key == null) row.putNull("child_key"); else row.put("child_key", key);
        row.put("ordinal", ordinal);

        if (value instanceof JSONObject) {
            row.put("kind", OBJECT);
            long id = db.insertOrThrow(TABLE, null, row);
            JSONObject object = (JSONObject) value;
            List<String> names = new ArrayList<>();
            java.util.Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) names.add(iterator.next());
            for (int i = 0; i < names.size(); i++) {
                String name = names.get(i);
                insertValue(db, id, name, i, object.get(name), depth + 1, count);
            }
            return id;
        }
        if (value instanceof JSONArray) {
            row.put("kind", ARRAY);
            long id = db.insertOrThrow(TABLE, null, row);
            JSONArray array = (JSONArray) value;
            for (int i = 0; i < array.length(); i++) {
                insertValue(db, id, null, i, array.get(i), depth + 1, count);
            }
            return id;
        }
        if (value == null || value == JSONObject.NULL) {
            row.put("kind", NULL);
            row.putNull("value");
        } else if (value instanceof Boolean) {
            row.put("kind", BOOLEAN);
            row.put("value", value.toString());
        } else if (value instanceof Number) {
            row.put("kind", NUMBER);
            row.put("value", value.toString());
        } else {
            row.put("kind", STRING);
            row.put("value", value.toString());
        }
        return db.insertOrThrow(TABLE, null, row);
    }

    private Object buildValue(Node node, Map<Long, List<Node>> children, int depth) throws JSONException {
        if (depth > MAX_DEPTH) throw new JSONException("Library data nesting is too deep");
        if (node.kind == OBJECT) {
            JSONObject object = new JSONObject();
            List<Node> values = children.get(node.id);
            if (values != null) for (Node child : values) {
                if (child.key != null) object.put(child.key, buildValue(child, children, depth + 1));
            }
            return object;
        }
        if (node.kind == ARRAY) {
            JSONArray array = new JSONArray();
            List<Node> values = children.get(node.id);
            if (values != null) for (Node child : values) array.put(buildValue(child, children, depth + 1));
            return array;
        }
        if (node.kind == NULL) return JSONObject.NULL;
        if (node.kind == BOOLEAN) return Boolean.parseBoolean(node.value);
        if (node.kind == NUMBER) return new JSONTokener(node.value).nextValue();
        return node.value == null ? "" : node.value;
    }

    private static final class Node {
        final long id;
        final long parentId;
        final String key;
        final int ordinal;
        final int kind;
        final String value;

        Node(long id, long parentId, String key, int ordinal, int kind, String value) {
            this.id = id;
            this.parentId = parentId;
            this.key = key;
            this.ordinal = ordinal;
            this.kind = kind;
            this.value = value;
        }
    }
}
