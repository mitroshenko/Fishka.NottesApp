package com.mitroshenko.mynotes.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Создание класса для записи в БД
class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    //открыть БД
    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    //Функция для записи
    suspend fun insertToDb(title: String, content: String, time: String) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)

        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }
    suspend fun updateItem(title: String, content: String, id: Int, time: String) = withContext(Dispatchers.IO) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)


        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    suspend fun readDbData(searchText : String): ArrayList<ListItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"

        //Заполняем данными
        //Считываем с помощью курсора, помещаем данные в курсор
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null, selection, arrayOf("%$searchText%"), null, null, null)


        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataId =
                cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val time =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))
            val item = ListItem()
            item.title = dataTitle
            item.desc = dataContent
            item.id = dataId
            item.time = time
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }

    fun closeDb() {
        myDbHelper.close()
    }

}