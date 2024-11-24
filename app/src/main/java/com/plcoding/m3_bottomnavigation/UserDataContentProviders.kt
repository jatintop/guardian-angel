package com.plcoding.m3_bottomnavigation

// UserDataContentProvider.kt

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class UserDataContentProvider : ContentProvider() {
    private lateinit var dbHelper: DatabaseHelper

    companion object {
        const val AUTHORITY = "com.plcoding.m3_bottomnavigation.provider"
        private const val USER_TABLE = "user_data"
        private const val USER = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, USER_TABLE, USER)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = DatabaseHelper(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = dbHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            USER -> db.query(USER_TABLE, projection, selection, selectionArgs, null, null, sortOrder)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USER -> {
                val id = db.insert(USER_TABLE, null, values)
                context?.contentResolver?.notifyChange(uri, null)
                Uri.parse("$USER_TABLE/$id")
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USER -> {
                val count = db.update(USER_TABLE, values, selection, selectionArgs)
                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        return when (uriMatcher.match(uri)) {
            USER -> {
                val count = db.delete(USER_TABLE, selection, selectionArgs)
                context?.contentResolver?.notifyChange(uri, null)
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            USER -> "vnd.android.cursor.dir/$AUTHORITY.$USER_TABLE"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        companion object {
            private const val DATABASE_NAME = "user_data.db"
            private const val DATABASE_VERSION = 1
        }

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE $USER_TABLE (
                    _id INTEGER PRIMARY KEY AUTOINCREMENT,
                    phone_email TEXT,
                    password TEXT,
                    name TEXT
                )
            """)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $USER_TABLE")
            onCreate(db)
        }
    }
}