package com.cryptic.imed.app;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.cryptic.imed.R;
import com.cryptic.imed.domain.*;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author sharafat
 *
 * Note: To enable all debug messages for all ORMLite classes, use the following command:
 *          adb shell setprop log.tag.ORMLite DEBUG
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final Logger log = LoggerFactory.getLogger(DbHelper.class);

    private static final String DB_NAME = "imedreminder.db";
    private static final int DB_VERSION = 1;

    @Inject
    private static Application application;

    public static OrmLiteSqliteOpenHelper getHelper() {
        return OpenHelperManager.getHelper(application, DbHelper.class);
    }

    public static void release() {
        OpenHelperManager.releaseHelper();
    }

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Medicine.class);
            TableUtils.createTable(connectionSource, Doctor.class);
            TableUtils.createTable(connectionSource, Dosage.class);
            TableUtils.createTable(connectionSource, PrescriptionMedicine.class);
            TableUtils.createTable(connectionSource, Prescription.class);
        } catch (SQLException e) {
            log.error("Error creating database.", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }
}
