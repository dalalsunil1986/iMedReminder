package com.cryptic.imed.app;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import java.io.File;
import java.io.InputStream;

/**
 * @author sharafat
 */
public abstract class AbstractDbHelper extends OrmLiteSqliteOpenHelper {
    @Inject
    private static Application application;

    public static OrmLiteSqliteOpenHelper getHelper() {
        return OpenHelperManager.getHelper(application, DbHelper.class);
    }

    public static void release() {
        OpenHelperManager.releaseHelper();
    }

    public AbstractDbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                            int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    public AbstractDbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                            int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public AbstractDbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                            int databaseVersion, File configFile) {
        super(context, databaseName, factory, databaseVersion, configFile);
    }

    public AbstractDbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory,
                            int databaseVersion, InputStream stream) {
        super(context, databaseName, factory, databaseVersion, stream);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        if (!db.isReadOnly()) {
            //enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
