package com.flowertech.taskplanner;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Task.class, Category.class, ToDoList.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();
    public abstract ToDoListDao toDoListDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //creates database if there is none, otherwise returns instance
    static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "task_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //creates two tasks
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                TaskDao tDao = INSTANCE.taskDao();
                CategoryDao cDao = INSTANCE.categoryDao();
                ToDoListDao toDao = INSTANCE.toDoListDao();

                Task task = new Task();
                task.title = "Exam";
                task.state = State.inProgress;
                tDao.insert(task);
                task = new Task();
                task.title = "Homework";
                task.state = State.closed;
                tDao.insert(task);

                Category category = new Category();
                category.abbr = "BIO";
                category.title = "Biology";
                category.description = "Biology class";
                cDao.insert(category);
                category = new Category();
                category.abbr = "MAT";
                category.title = "Math";
                category.description = "Math class";
                cDao.insert(category);
                category = new Category();
                category.abbr = "GEO";
                category.title = "Geography";
                category.description = "Geography class";
                cDao.insert(category);

                ToDoList toDoList = new ToDoList();
                toDoList.taskListId = 1L;
                toDoList.checked = false;
                toDoList.description = "str. 5";
                toDao.insert(toDoList);
                toDoList = new ToDoList();
                toDoList.taskListId = 1L;
                toDoList.checked = false;
                toDoList.description = "str. 6";
                toDao.insert(toDoList);
                toDoList = new ToDoList();
                toDoList.taskListId = 1L;
                toDoList.checked = false;
                toDoList.description = "str. 7";
                toDao.insert(toDoList);
            });
        }
    };
}
