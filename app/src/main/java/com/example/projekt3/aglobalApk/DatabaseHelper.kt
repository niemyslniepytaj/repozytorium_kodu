package com.example.projekt3.aglobalApk

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Tworzenie tabel
        db.execSQL(SQL_CREATE_TABLE_USTAWIENIA)
        db.execSQL(SQL_CREATE_TABLE_TURTLE)
        db.execSQL(SQL_CREATE_TABLE_AVATAR)
        db.execSQL(SQL_CREATE_TABLE_GRY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Usunięcie istniejących tabel
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USTAWIENIA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TURTLE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AVATAR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GRY")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "MyDatabase.db"

        // Nazwy tabel
        private const val TABLE_USTAWIENIA = "ustawienia"
        private const val TABLE_TURTLE = "turtle"
        private const val TABLE_AVATAR = "avatar"
        private const val TABLE_GRY = "gry"

        // Kolumny tabeli (wspólne dla wszystkich)
        private const val COLUMN_KEY = "klucz"
        private const val COLUMN_VALUE = "wartosc"

        // SQL do tworzenia tabel
        private const val SQL_CREATE_TABLE_USTAWIENIA =
            "CREATE TABLE IF NOT EXISTS $TABLE_USTAWIENIA (" +
                    "$COLUMN_KEY TEXT PRIMARY KEY, " +
                    "$COLUMN_VALUE TEXT NOT NULL)"

        private const val SQL_CREATE_TABLE_TURTLE =
            "CREATE TABLE IF NOT EXISTS $TABLE_TURTLE (" +
                    "$COLUMN_KEY TEXT PRIMARY KEY, " +
                    "$COLUMN_VALUE TEXT NOT NULL)"

        private const val SQL_CREATE_TABLE_AVATAR =
            "CREATE TABLE IF NOT EXISTS $TABLE_AVATAR (" +
                    "$COLUMN_KEY TEXT PRIMARY KEY, " +
                    "$COLUMN_VALUE TEXT NOT NULL)"

        private const val SQL_CREATE_TABLE_GRY =
            "CREATE TABLE IF NOT EXISTS $TABLE_GRY (" +
                    "$COLUMN_KEY TEXT PRIMARY KEY, " +
                    "$COLUMN_VALUE TEXT NOT NULL)"

    }

    // Funkcja do wstawiania lub aktualizacji danych
    // Funkcja do wstawiania lub aktualizacji danych
    fun baza_wstaw(tableName: String, key: String, value: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }

        // Sprawdzenie, czy klucz już istnieje
        val existingValue = baza_pobierz(tableName, key)
        Log.d("DatabaseHelper", "existing value: $existingValue")
        if (existingValue !=null) {
            // Aktualizacja wartości, jeśli klucz istnieje
            db.update(tableName, contentValues, "$COLUMN_KEY=?", arrayOf(key))
            Log.d("DatabaseHelper", "update danych: $tableName, $key, $value")
        } else {
            // Wstawienie nowej pary klucz-wartość
            db.insert(tableName, null, contentValues)
            Log.d("DatabaseHelper", "Wstawianie danych: $tableName, $key, $value")
        }
    }


    // Funkcja do pobierania wartości na podstawie klucza
    // Funkcja do pobierania wartości na podstawie klucza
    fun baza_pobierz(tableName: String, key: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            tableName,
            arrayOf(COLUMN_VALUE),
            "$COLUMN_KEY=?",
            arrayOf(key),
            null,
            null,
            null
        )
        var value: String? = "" // Domyślna wartość w przypadku braku klucza
        if (cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALUE))
        }
        cursor.close()
        return value
    }
}
