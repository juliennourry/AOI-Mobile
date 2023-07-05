package fr.java.aoitechnicien.Requester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import fr.java.aoitechnicien.Function.CheckCurrentMonth;
import fr.java.aoitechnicien.Function.CheckArrayDiff;
import fr.java.aoitechnicien.Models.ModelApiBonIntervention;
import fr.java.aoitechnicien.Models.ModelApiDemand;
import fr.java.aoitechnicien.Models.ModelApiFrequency;
import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiItemReduce;
import fr.java.aoitechnicien.Models.ModelApiMaintTask;
import fr.java.aoitechnicien.Models.ModelApiMaintTaskDone;
import fr.java.aoitechnicien.Models.ModelApiMountage;
import fr.java.aoitechnicien.Models.ModelApiMountageDone;
import fr.java.aoitechnicien.Models.ModelApiNote;
import fr.java.aoitechnicien.Models.ModelApiOfftime;
import fr.java.aoitechnicien.Models.ModelApiRMFrequency;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelApiTiers;
import fr.java.aoitechnicien.Models.ModelCrossSiteContrat;
import fr.java.aoitechnicien.Models.ModelCrossThirdpartyContrat;
import fr.java.aoitechnicien.Models.ModelItem;
import fr.java.aoitechnicien.Models.ModelMaintTaskCategory;
import fr.java.aoitechnicien.Models.ModelMontage;
import fr.java.aoitechnicien.Models.ModelSite;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_teamdesk_v1.db";
    private static final int DATABASE_VERSION = 10;
    String sql;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void deleteDatabase(Context context) {
        close();
        context.deleteDatabase(DATABASE_NAME);
    }

    public void onCreate(SQLiteDatabase db) {

        sql = "CREATE TABLE IF NOT EXISTS sync (id INTEGER PRIMARY KEY AUTOINCREMENT, createdAt TEXT, token TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, name TEXT NOT NULL, lastname TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL, role TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS item_site (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, fk_item_id INT NOT NULL, fk_site_id INT NOT NULL, label TEXT NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME, gen_planning TINYINT(1), uuid CHAR(36) NOT NULL)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS item (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, fk_category_id INT NOT NULL, label TEXT NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS appareil (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, id_site INTEGER NOT NULL, label TEXT NOT NULL, category TEXT NOT NULL, fk_category INTEGER, uuid TEXT NOT NULL, access TEXT NOT NULL, onAt DATETIME NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME, desactivatedAt DATETIME, phone TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS site (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, label TEXT NOT NULL, contrat TEXT NOT NULL, tiers TEXT NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS intervention (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fkItemsite INTEGER, fkUser INTEGER, note TEXT, type TEXT, stop TEXT, startDate DATETIME, endDate DATETIME, signName TEXT, signData TEXT, coordinates TEXT,  createdAt TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS offtime (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fk_itemsite INTEGER, startAt DATETIME, endAt DATETIME, status TEXT, createdAt DATETIME NOT NULL, send TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS mountage (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fk_category_id INTEGER, label TEXT, estimated_time FLOAT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS mountageDone (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, label TEXT, doneAt DATETIME, createdAt DATETIME NOT NULL, fk_mountage_step INTEGER, fk_itemsite INTEGER, doneTime FLOAT, send TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS frequency (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, label TEXT, fk_category_id INTEGER, n_day INTEGER, status TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS task (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fk_frequency INTEGER, fk_mainttaskcategory INTEGER, label TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS groups (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fk_frequency INTEGER, fk_category INTEGER, label TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS taskdone (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, label TEXT, doneAt DATETIME, local_realmadridfrequency INTEGER, local_maintenanceform INTEGER, fk_maintenanceform INTEGER, fk_realmadridfrequency INTEGER, fk_mainttask INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS realmadridfrequency (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, startDate DATETIME, endDate DATETIME, fk_itemsite INTEGER, fk_frequency INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME, send TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS maintenanceform (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, startDate DATETIME, endDate DATETIME, note TEXT, coordinates TEXT, ref TEXT, status TEXT, fk_itemsite INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME, send TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS api (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS bon_intervention (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, reference TEXT, title TEXT, description TEXT, state TEXT, doneAt DATETIME, doneTime INTEGER, origin TEXT, type TEXT, signataire TEXT, signature TEXT, fk_demand INTEGER, fk_note INTEGER, fk_user INTEGER, fk_thirdparty INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, reference TEXT, title TEXT, description TEXT, origin TEXT, origin_phone TEXT, origin_mail TEXT, state TEXT, doneAt TEXT, doneTime TEXT, suspendedAt DATETIME, suspendedReason TEXT, fk_contact INTEGER, fk_itemsite INTEGER, fk_site INTEGER, fk_tiers INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME, send TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, description TEXT, private TEXT, fk_demand INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS document (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, name TEXT, extension TEXT, fk_demand INTEGER, fk_intervention INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande_x_contact (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, contact INTEGER, send TEXT, deleted TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande_x_itemsite (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, itemsite INTEGER, send TEXT, deleted TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande_x_site (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, site INTEGER, send TEXT, deleted TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande_x_tiers (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, tiers INTEGER, send TEXT, deleted TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS demande_x_user (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, user INTEGER, send TEXT, deleted TEXT)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS tiers (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, ref TEXT, label TEXT, customer TEXT, supplier TEXT, email TEXT, phone TEXT, address TEXT, zip TEXT, city TEXT, origin TEXT, createdAt DATETIME, deletedAt DATETIME)";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int versionDifference = newVersion - oldVersion;

        if(versionDifference == 0){
            onCreate(db);
        }

        if(versionDifference >= 2){

            onClean(db);
            onCreate(db);

        } else {

            if(oldVersion < newVersion){

                sql = "DROP TABLE IF EXISTS user";
                db.execSQL(sql);
                sql = "DROP TABLE IF EXISTS sync";
                db.execSQL(sql);

                sql = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, name TEXT NOT NULL, lastname TEXT NOT NULL, phone TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL, role TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS sync (id INTEGER PRIMARY KEY AUTOINCREMENT, createdAt TEXT, token TEXT)";
                db.execSQL(sql);

            }

            if(oldVersion == 1 && newVersion == 2){
                sql = "CREATE TABLE IF NOT EXISTS api (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
            }

            if(oldVersion == 2 && newVersion == 3){
                sql = "CREATE TABLE IF NOT EXISTS bon_intervention (id INTEGER PRIMARY KEY AUTOINCREMENT, reference TEXT, title TEXT, description TEXT, state TEXT, doneAt DATETIME, doneTime TEXT, origin TEXT, type TEXT, signataire TEXT, signature TEXT, fk_demand INTEGER, fk_note INTEGER, fk_user INTEGER, fk_thirdparty INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande (id INTEGER PRIMARY KEY AUTOINCREMENT, reference TEXT, title TEXT, description TEXT, origin TEXT, origin_phone TEXT, state TEXT, doneAt TEXT, doneTime TEXT, suspendedAt DATETIME, suspendedReason TEXT, fk_contact INTEGER, fk_itemsite INTEGER, fk_site INTEGER, fk_tiers INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, private TEXT, fk_demande INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS document (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, extension TEXT, fk_demande INTEGER, fk_intervention INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
            }
            if(oldVersion == 3 && newVersion == 4) {
                sql = "CREATE TABLE IF NOT EXISTS bon_intervention_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, reference TEXT, title TEXT, description TEXT, state TEXT, doneAt DATETIME, doneTime INTEGER, origin TEXT, type TEXT, signataire TEXT, signature TEXT, fk_demand INTEGER, fk_note INTEGER, fk_user INTEGER, fk_thirdparty INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "INSERT INTO bon_intervention_temp SELECT * FROM bon_intervention";
                db.execSQL(sql);
                sql = "DROP TABLE bon_intervention";
                db.execSQL(sql);
                sql = "ALTER TABLE bon_intervention_temp RENAME TO bon_intervention";
                db.execSQL(sql);
            }
            if(oldVersion == 5 && newVersion == 6) {
                sql = "DROP TABLE IF EXISTS demande";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, reference TEXT, title TEXT, description TEXT, origin TEXT, origin_phone TEXT, origin_mail TEXT, state TEXT, doneAt TEXT, doneTime TEXT, suspendedAt DATETIME, suspendedReason TEXT, fk_contact INTEGER, fk_itemsite INTEGER, fk_site INTEGER, fk_tiers INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME, send TEXT)";
                db.execSQL(sql);
            }
            if(oldVersion == 6 && newVersion == 7) {
                sql = "CREATE TABLE IF NOT EXISTS demande_x_contact (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, contact INTEGER)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_itemsite (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, itemsite INTEGER)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_site (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, site INTEGER)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_tiers (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, tiers INTEGER)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_user (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, user INTEGER)";
                db.execSQL(sql);
            }
            if(oldVersion == 7 && newVersion == 8) {
                sql = "DROP TABLE IF EXISTS demande_x_contact";
                db.execSQL(sql);
                sql = "DROP TABLE IF EXISTS demande_x_itemsite";
                db.execSQL(sql);
                sql = "DROP TABLE IF EXISTS demande_x_site";
                db.execSQL(sql);
                sql = "DROP TABLE IF EXISTS demande_x_tiers";
                db.execSQL(sql);
                sql = "DROP TABLE IF EXISTS demande_x_user";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_contact (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, contact INTEGER, send TEXT, deleted TEXT)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_itemsite (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, itemsite INTEGER, send TEXT, deleted TEXT)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_site (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, site INTEGER, send TEXT, deleted TEXT)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_tiers (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, tiers INTEGER, send TEXT, deleted TEXT)";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS demande_x_user (id INTEGER PRIMARY KEY AUTOINCREMENT, demande INTEGER, user INTEGER, send TEXT, deleted TEXT)";
                db.execSQL(sql);
            }
            if(oldVersion == 8 && newVersion == 9) {
                sql = "CREATE TABLE IF NOT EXISTS note_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, description TEXT, private TEXT, fk_demand INTEGER, fk_user INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "INSERT INTO note_temp (id, id_sync, description, private, fk_demand, fk_user, createdAt, deletedAt) SELECT id, id_sync, description, private, fk_demande, fk_user, createdAt, deletedAt FROM note";
                db.execSQL(sql);
                sql = "DROP TABLE note";
                db.execSQL(sql);
                sql = "ALTER TABLE note_temp RENAME TO note";
                db.execSQL(sql);
                sql = "CREATE TABLE IF NOT EXISTS document_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, name TEXT, extension TEXT, fk_demand INTEGER, fk_intervention INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
                db.execSQL(sql);
                sql = "INSERT INTO document_temp (id, id_sync, name, extension, fk_demand, fk_intervention, createdAt, deletedAt) SELECT id, id_sync, name, extension, fk_demande, fk_intervention, createdAt, deletedAt FROM document";
                db.execSQL(sql);
                sql = "DROP TABLE document";
                db.execSQL(sql);
                sql = "ALTER TABLE document_temp RENAME TO document";
                db.execSQL(sql);
            }
            if(oldVersion == 9 && newVersion == 10) {

                sql = "CREATE TABLE IF NOT EXISTS realmadridfrequency_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, startDate DATETIME, endDate DATETIME, fk_itemsite INTEGER, fk_frequency INTEGER, createdAt DATETIME NOT NULL, deletedAt DATETIME, send TEXT)";
                db.execSQL(sql);
                sql = "INSERT INTO realmadridfrequency_temp (id, id_sync, startDate, endDate, fk_itemsite, fk_frequency, createdAt, deletedAt) SELECT id, id_sync, startDate, endDate, fk_itemsite, fk_frequency, createdAt, deletedAt FROM realmadridfrequency";
                db.execSQL(sql);
                sql = "DROP TABLE realmadridfrequency";
                db.execSQL(sql);
                sql = "ALTER TABLE realmadridfrequency_temp RENAME TO realmadridfrequency";
                db.execSQL(sql);
            }
            if(oldVersion == 10 && newVersion == 11) {
                sql = "CREATE TABLE IF NOT EXISTS tiers (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, ref TEXT, label TEXT, customer TEXT, supplier TEXT, email TEXT, phone TEXT, address TEXT, zip TEXT, city TEXT, origin TEXT, createdAt DATETIME, deletedAt DATETIME)";
                db.execSQL(sql);

                sql = "CREATE TABLE IF NOT EXISTS appareil_temp (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, id_site INTEGER NOT NULL, label TEXT NOT NULL, category TEXT NOT NULL, fk_category INTEGER, uuid TEXT NOT NULL, access TEXT NOT NULL, onAt DATETIME NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME, desactivatedAt DATETIME, phone TEXT)";
                db.execSQL(sql);
                sql = "INSERT INTO appareil_temp (id, id_sync, id_site, label, category, fk_category, uuid, access, onAt, createdAt, deletedAt) SELECT id, id_sync, id_site, label, category, fk_category, uuid, access, onAt, createdAt, deletedAt FROM appareil";
                db.execSQL(sql);
                sql = "DROP TABLE appareil";
                db.execSQL(sql);
                sql = "ALTER TABLE appareil_temp RENAME TO appareil";
                db.execSQL(sql);
            }

        }

//        sql = "ALTER TABLE mountagedone ADD COLUMN send TEXT";
//        db.execSQL(sql);
//        sql = "ALTER TABLE frequency ADD COLUMN label TEXT";
//        db.execSQL(sql);
//        sql = "ALTER TABLE taskdone ADD COLUMN doneAt DATETIME";
//        db.execSQL(sql);
//        sql = "ALTER TABLE taskdone ADD COLUMN local_realmadridfrequency INTEGER";
//        db.execSQL(sql);
//        sql = "ALTER TABLE taskdone ADD COLUMN local_maintenanceform INTEGER";
//        db.execSQL(sql);
    }

    public void onClean(SQLiteDatabase db) {
        sql = "DROP TABLE IF EXISTS sync";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS user";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS item_site";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS item";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS appareil";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS site";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS intervention";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS offtime";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS mountage";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS mountageDone";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS frequency";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS task";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS groups";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS taskdone";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS realmadridfrequency";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS maintenanceform";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS api";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS bon_intervention";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS note";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS document";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande_x_contact";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande_x_itemsite";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande_x_site";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande_x_tiers";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS demande_x_user";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS tiers";
        db.execSQL(sql);
    }

    public boolean debugAction(SQLiteDatabase db) {
        // -- REMOVE THE DROP
        sql = "DROP TABLE IF EXISTS realmadridfrequency";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS maintenanceform";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS taskdone";
        db.execSQL(sql);
        onCreate(db);
        return true;
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{tableName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isSync(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM sync WHERE '1'", null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getSync(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM sync WHERE '1'", null);
        return cursor;
    }

    public Cursor getAPI(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM api WHERE '1'", null);
        return cursor;
    }
    public Boolean isConnect(SQLiteDatabase db, String email, String password) {
        String password_crypted;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email=?", new String[]{email});
        Boolean exist = cursor.getCount() == 1;
        if(exist){
            if (cursor.moveToFirst()) {
                password_crypted = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                if(!password_crypted.trim().isEmpty()) {
                    if (encoder.matches(password, password_crypted)) {
                        return true;
                    }
                }
            }
        }
        cursor.close();
        return false;
    }

    public Boolean hasUrl(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM api WHERE url IS NOT NULL", new String[]{});
        Boolean exist = cursor.getCount() == 1;
        if(exist){
            if (cursor.moveToFirst()) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public boolean insertAPI(SQLiteDatabase db, String url) {
        long timestamp = System.currentTimeMillis();

        try {
            ContentValues values = new ContentValues();
            values.put("url", url);
            values.put("createdAt", String.valueOf(timestamp));

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM api WHERE 1", new String[]{});
            if (cursor.getCount() > 0) {
                // User already exists, update the record
                db.update("api", values, "id = ?", new String[]{String.valueOf(1)});
            } else {
                // User does not exist, insert a new record
                try {
                    db.insertOrThrow("api", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_API_CONFIG", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_API_CONFIG", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertUser(SQLiteDatabase db, ModelApiUser user) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", user.getId());
            values.put("name", user.getProfile().getName());
            values.put("lastname", user.getProfile().getLastname());
            values.put("phone", user.getProfile().getPhone());
            values.put("email", user.getProfile().getEmail());
            values.put("password", user.getPassword());
            values.put("role", user.getRole());
            values.put("createdAt", user.getCreatedAt());
            values.put("deletedAt", user.getDeletedAt());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE id_sync = ?", new String[]{String.valueOf(user.getId())});
            if (cursor.getCount() > 0) {
                // User already exists, update the record
                db.update("user", values, "id_sync = ?", new String[]{String.valueOf(user.getId())});
            } else {
                // User does not exist, insert a new record
                try {
                    db.insertOrThrow("user", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertUser", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertUser", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertItem(SQLiteDatabase db, ModelApiItemReduce item) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", item.getId());
            values.put("id_site", item.getSite().getId());
            values.put("label", item.getLabel());
            values.put("fk_category", item.getCategory().getId());
            values.put("category", item.getCategory().getLabel());
            values.put("uuid", item.getUuid());
            values.put("access", "0");
            values.put("onAt", item.getOnAt());
            values.put("createdAt", item.getCreatedAt());
            values.put("deletedAt", item.getDeletedAt());
            values.put("desactivatedAt", item.getDesactivatedAt());
            values.put("phone", item.getPhone());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE id_sync = ?", new String[]{String.valueOf(item.getId())});
            if (cursor.getCount() > 0) {
                db.update("appareil", values, "id_sync = ?", new String[]{String.valueOf(item.getId())});
            } else {
                try {
                    db.insertOrThrow("appareil", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertItem", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertItem", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertSite(SQLiteDatabase db, ModelApiSite site) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", site.getId());
            values.put("label", site.getLabel());

            values.put("contrat", "Aucun contrat");
            values.put("tiers", "Aucun tiers");
            List<ModelCrossSiteContrat> listSiteContrat = site.getCrossSiteContrat();
            for (ModelCrossSiteContrat siteContrat : listSiteContrat) {
                values.put("contrat", siteContrat.getContrat().getRef());

                List<ModelCrossThirdpartyContrat> listThirdpartyContrat = siteContrat.getContrat().getCrossThirdpartyContract();
                for (ModelCrossThirdpartyContrat thirdpartyContrat : listThirdpartyContrat) {
                    values.put("tiers", thirdpartyContrat.getThirdparty().getLabel());

                }
            }

            values.put("createdAt", site.getCreatedAt());
            values.put("deletedAt", site.getDeletedAt());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM site WHERE id_sync = ?", new String[]{String.valueOf(site.getId())});
            if (cursor.getCount() > 0) {
                db.update("site", values, "id_sync = ?", new String[]{String.valueOf(site.getId())});
            } else {
                try {
                    db.insertOrThrow("site", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertSite_In", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertSite_Global", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsert(SQLiteDatabase db, String token) {
        long timestamp = System.currentTimeMillis();

        try {
            ContentValues values = new ContentValues();
            values.put("token", token);
            values.put("createdAt", String.valueOf(timestamp));

            Cursor cursor = db.rawQuery("SELECT * FROM sync WHERE id = 1", null);
            if (cursor.getCount() > 0) {
                db.update("sync", values, "id = 1", null);
            } else {
                try {
                    db.insertOrThrow("sync", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsert", e.toString());
                }
            }
            cursor.close();

            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsert", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAppareilAccess(SQLiteDatabase db, String id_sync) {

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE id_sync = ?", new String[]{id_sync});
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_verifyAppareilAccess", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public String getSyncDTB(SQLiteDatabase db, String columnName) {
        String data = null;
        Cursor cursor = db.rawQuery("SELECT * FROM sync WHERE id='1'", null);
        boolean exists = cursor.getCount() > 0;

        if (cursor.moveToFirst()) {
            data = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        }
        cursor.close();
        return data;
    }

    public String getApiUrl(SQLiteDatabase db, String columnName) {
        String data = null;
        Cursor cursor = db.rawQuery("SELECT * FROM api WHERE id='1'", null);
        boolean exists = cursor.getCount() > 0;

        if (cursor.moveToFirst()) {
            data = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        }
        cursor.close();
        return data;
    }

    public List<JSONObject> getInterventions(SQLiteDatabase db, String id) {
        List<JSONObject> interventionList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM intervention WHERE id_sync = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("startDate", cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
                    obj.put("endDate", cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
                    obj.put("note", cursor.getString(cursor.getColumnIndexOrThrow("note")));
                    obj.put("type", cursor.getString(cursor.getColumnIndexOrThrow("type")));
                    obj.put("coordinates", cursor.getString(cursor.getColumnIndexOrThrow("coordinates")));
                    obj.put("fkUser", "/api/users/"+cursor.getString(cursor.getColumnIndexOrThrow("fkUser")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fkItemsite")));
                    obj.put("status", 0);
                    obj.put("stop", cursor.getInt(cursor.getColumnIndexOrThrow("stop")) == 1);
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                    obj.put("signName", cursor.getString(cursor.getColumnIndexOrThrow("signName")));
                    obj.put("signData", cursor.getString(cursor.getColumnIndexOrThrow("signData")));
                } catch (JSONException e) {
                    Log.e("DEBUG_INTERVENTION_GET", e.toString());
                    throw new RuntimeException(e);
                }
                interventionList.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return interventionList;
    }

    public List<JSONObject> getOfftime(SQLiteDatabase db, String send) {
        List<JSONObject> offtimeList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM offtime WHERE send = ?", new String[]{send});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_itemsite")));
                    obj.put("startAt", cursor.getString(cursor.getColumnIndexOrThrow("startAt")));
                    obj.put("endAt", cursor.getString(cursor.getColumnIndexOrThrow("endAt")));
                    obj.put("status", 0);
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_OFFTIME_GET", e.toString());
                    throw new RuntimeException(e);
                }
                offtimeList.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return offtimeList;
    }

    public Boolean checkAccessItem(SQLiteDatabase db, String idUser, String emailUser) {
        String data = null;
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE id_sync=? AND email =?", new String[]{idUser, emailUser});
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        return exists;
    }

    public Boolean updateAccessItem(SQLiteDatabase db, Integer idItem, Boolean accessing) {
        try {
            ContentValues values = new ContentValues();
            values.put("access", "0");
            if(accessing){
                values.put("access", "1");
            }

            Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE id_sync = ?", new String[]{idItem.toString()});
            if (cursor.getCount() > 0) {
                db.update("appareil", values, "id_sync = ?", new String[]{idItem.toString()});
            }
            cursor.close();

            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateAccessItem", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Boolean updateIntervention(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            db.update("intervention", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateIntervention", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Boolean updateOfftime(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            values.put("send", "0");
            db.update("offtime", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateOFFTIME", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }


    public Boolean checkUuidItem(SQLiteDatabase db, String uuid) {
        Boolean result = false;
//        Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE access = '1' AND uuid = ? AND deletedAt IS NULL", new String[]{uuid});
        Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE desactivatedAt IS NULL AND uuid = ?", new String[]{uuid});
        if (cursor.getCount() > 0) {
            result = true;
        }
        cursor.close();

        return result;
    }

    public List<ModelItem> getAppareil(SQLiteDatabase db, String uuid) {
        List<ModelItem> itemList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE access = '1' AND uuid = ?", new String[]{uuid});
        if (cursor.moveToFirst()) {
            do {
                ModelItem appareil = new ModelItem(0,0,"","","","","",0, "","", 0);
                appareil.setIdSync(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                appareil.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
                appareil.setOnAt(cursor.getString(cursor.getColumnIndexOrThrow("onAt")));
                appareil.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                appareil.setFkCategory(cursor.getInt(cursor.getColumnIndexOrThrow("fk_category")));
                appareil.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                appareil.setIdSite(cursor.getString(cursor.getColumnIndexOrThrow("id_site")));
                appareil.setAccess(cursor.getInt(cursor.getColumnIndexOrThrow("access")));
                itemList.add(appareil);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public List<ModelSite> getSite(SQLiteDatabase db, String id) {
        List<ModelSite> appareilList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM site WHERE id_sync = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            do {
                ModelSite site = new ModelSite(0,"", "", "", null);
                site.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                site.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
                site.setContrat(cursor.getString(cursor.getColumnIndexOrThrow("contrat")));
                site.setTiers(cursor.getString(cursor.getColumnIndexOrThrow("tiers")));
                appareilList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return appareilList;
    }

    public boolean insertIntervention(SQLiteDatabase db, Map<String, String> map) {
        Boolean valid_request = false;

        ContentValues values = new ContentValues();
        values.put("id_sync", 0);
        values.put("fkItemsite", map.get("fkItemsite"));
        values.put("fkUser", map.get("fkUser"));
        values.put("note", map.get("note"));
        values.put("type", map.get("type"));
        values.put("stop", map.get("stop"));
        values.put("startDate", map.get("startDate"));
        values.put("endDate", map.get("endDate"));
        values.put("signName", map.get("signName"));
        values.put("signData", map.get("signData"));
        values.put("coordinates", map.get("coordinates"));
        values.put("createdAt", map.get("createdAt"));

        try {
            db.insertOrThrow("intervention", null, values);
            if(map.get("stop") == "1") {
                Boolean create_offtime = checkOfftimeItem(db, map.get("fkItemsite"));

                if(create_offtime){
                    insertOfftime(db, map.get("fkItemsite"));
                }
            }
            valid_request = true;
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_INFO_insertIntervention", e.toString());
        }

        return valid_request;
    }

    public boolean insertOfftime(SQLiteDatabase db, String fkItemsite) {
        Boolean valid_request;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(timeZone);
        Date currentDate = calendar.getTime();
        String formattedDate = dateFormat.format(currentDate);

        ContentValues values = new ContentValues();
        values.put("id_sync", 0);
        values.put("fk_itemsite", fkItemsite);
        values.put("startAt", String.valueOf(formattedDate));
        values.put("status", 0);
        values.put("createdAt", String.valueOf(currentDate));
        values.put("send", "1");

        try {
            db.insertOrThrow("offtime", null, values);
            valid_request = true;
        } catch(Exception e) {
            Log.e("DEBUG_INSERT_OFFTIME", e.toString());
            valid_request = false;
        }

        return valid_request;
    }

    public Boolean checkOfftimeItem(SQLiteDatabase db, String fkItemsite) {
        Boolean create_offtime = false;
        Cursor cursor = db.rawQuery("SELECT * FROM offtime WHERE fk_itemsite = ? ORDER BY startAt DESC LIMIT 1", new String[]{fkItemsite});
        if (cursor.moveToFirst()) {
            if(!cursor.isNull(cursor.getColumnIndexOrThrow("endAt"))){
                create_offtime = true;
            } else {
                create_offtime = false;
            }

        } else {
            create_offtime = true;
        }
        cursor.close();
        return create_offtime;
    }

    public Boolean onlineOfftime(SQLiteDatabase db, String fk_itemsite) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(timeZone);
        Date currentDate = calendar.getTime();
        try {
            ContentValues values = new ContentValues();
            values.put("endAt", String.valueOf(currentDate));
            values.put("send", "1");
            db.update("offtime", values, "fk_itemsite = ? AND endAt IS NULL", new String[]{fk_itemsite});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateIntervention", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public static String getIdSyncUser(SQLiteDatabase db, String login) {
        String idsync = null;
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{login});
        if (cursor.moveToFirst()) {
            idsync = cursor.getString(cursor.getColumnIndexOrThrow("id_sync"));

        }
        cursor.close();
        return idsync;
    }

    public static Cursor getInfoUser(SQLiteDatabase db, String login) {
        String idsync = null;
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{login});
        return cursor;
    }

    public boolean verifyAndInsertOfftime(SQLiteDatabase db, ModelApiOfftime offtime) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        calendar.setTimeZone(timeZone);
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdAt = dateFormat.format(currentDate);
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", offtime.getId());
            values.put("startAt", offtime.getStartAt());
            if (offtime.getEndAt() != null) {
                values.put("endAt", offtime.getEndAt());
            } else {
                values.putNull("endAt");
            }
            values.put("status", 0);
            values.put("createdAt", offtime.getCreatedAt());
            values.put("fk_itemsite", offtime.getFkItemsite().replace("/api/item_sites/", ""));

            Cursor cursor = db.rawQuery("SELECT * FROM offtime WHERE id_sync = ?", new String[]{String.valueOf(offtime.getId())});
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0 && cursor.getString(cursor.getColumnIndexOrThrow("send")).equals("0")) {
                    Log.e("DEBUF_ID_SYNC_OFFTIMEDOWN", String.valueOf(offtime.getId()));
                    db.update("offtime", values, "id_sync = ?", new String[]{String.valueOf(offtime.getId())});
                } else if(cursor.getCount() > 0 && cursor.getString(cursor.getColumnIndexOrThrow("send")).equals("1")) {

                } else {
                    try {
                        values.put("send", "0");
                        db.insertOrThrow("offtime", null, values);
                    } catch(Exception e) {
                        Log.e("DEBUG_THREAD_INFO_VerifyAndInsertOfftime", e.toString());
                    }
                }
            } else {
                try {
                    values.put("send", "0");
                    db.insertOrThrow("offtime", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertOfftime", e.toString());
                }
            }
            cursor.close();

            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_TOP_VerifyAndInsertOFFTIME", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertMountage(SQLiteDatabase db, ModelApiMountage mountage) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", mountage.getId());
            values.put("fk_category_id", Integer.parseInt(mountage.getFkCategory().replace("/api/categories/", "")));
            values.put("label", mountage.getLabel());
            values.put("estimated_time", mountage.getDoneTime());
            values.put("createdAt", mountage.getCreatedAt());
            values.put("deletedAt", mountage.getDeletedAt());

            // Check if already exists
            Cursor cursor = db.rawQuery("SELECT * FROM mountage WHERE id_sync = ?", new String[]{String.valueOf(mountage.getId())});
            if (cursor.getCount() > 0) {
                db.update("mountage", values, "id_sync = ?", new String[]{String.valueOf(mountage.getId())});
            } else {
                try {
                    db.insertOrThrow("mountage", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertMountage_In", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertMountage_Global", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertMountageDone(SQLiteDatabase db, ModelApiMountageDone mountagedone, Boolean apiroad) {
        try {
            ContentValues values = new ContentValues();
            values.put("label", mountagedone.getLabel());
            values.put("doneAt", mountagedone.getDoneAt());
            values.put("doneTime", mountagedone.getDoneTime());
            values.put("createdAt", mountagedone.getCreatedAt());
            values.put("fk_mountage_step", mountagedone.getFkMountageStep().replace("/api/mountage_steps/", ""));
            values.put("fk_itemsite", mountagedone.getFkItemsite().replace("/api/item_sites/", ""));



            // Check if already exists
            Cursor cursor;
            if(apiroad){
                cursor = db.rawQuery("SELECT * FROM mountageDone WHERE id_sync = ?", new String[]{String.valueOf(mountagedone.getId())});
            } else {
                cursor = db.rawQuery("SELECT * FROM mountageDone WHERE fk_mountage_step = ? AND fk_itemsite = ?", new String[]{String.valueOf(mountagedone.getFkMountageStep()), String.valueOf(mountagedone.getFkItemsite())});
            }
            if (cursor.getCount() > 0) {
                if(apiroad){
                    db.update("mountageDone", values, "id_sync = ?", new String[]{String.valueOf(mountagedone.getId())});
                } else {
                    values.put("send", "1");
                    db.update("mountageDone", values, "fk_mountage_step = ? AND fk_itemsite = ?", new String[]{String.valueOf(mountagedone.getFkMountageStep()), String.valueOf(mountagedone.getFkItemsite())});
                }
            } else {
                try {
                    values.put("id_sync", mountagedone.getId());
                    values.put("send", "1");
                    db.insertOrThrow("mountageDone", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertMountageDone_In", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertMountageDone_Global", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Cursor getMontage(SQLiteDatabase db, String id) {
        List<ModelMontage> montageList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM mountage WHERE fk_category_id = ?", new String[]{id});
        return cursor;
    }


    public List<JSONObject> getSendMountageDone(SQLiteDatabase db, String send) {
        List<JSONObject> mountagedoneList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM mountageDone WHERE send = ?", new String[]{send});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("label", cursor.getString(cursor.getColumnIndexOrThrow("label")));
                    obj.put("doneAt", cursor.getString(cursor.getColumnIndexOrThrow("doneAt")));
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                    obj.put("fkMountageStep", "/api/mountage_steps/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_mountage_step")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_itemsite")));
                    obj.put("doneTime", cursor.getFloat(cursor.getColumnIndexOrThrow("doneTime")));
                } catch (JSONException e) {
                    Log.e("DEBUG_MOUNTAGEDONE_GET", e.toString());
                    throw new RuntimeException(e);
                }
                mountagedoneList.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return mountagedoneList;
    }

    public Boolean updateMountageDone(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            values.put("send", "0");
            db.update("mountageDone", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateMOUNTAGEDONE", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteMountageDone(SQLiteDatabase db, String id) {
        List<ModelMontage> montageList = new ArrayList<>();
        Cursor cursor = db.rawQuery("DELETE FROM mountageDone WHERE id = ?", new String[]{id});
        return true;
    }

    public Cursor getMontageDone(SQLiteDatabase db, String mountage_id, String item_id) {
        Cursor cursor = db.rawQuery("SELECT * FROM mountageDone WHERE fk_mountage_step = ? AND fk_itemsite = ?", new String[]{mountage_id, item_id});
        return cursor;
    }

    public boolean verifyAndInsertFrequency(SQLiteDatabase db, ModelApiFrequency frequency, Integer fk_category_id) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", frequency.getId());
            values.put("fk_category_id", fk_category_id);
            values.put("n_day", frequency.getnDay());
            values.put("status", frequency.getStatus());
            values.put("createdAt", frequency.getCreatedAt());
            values.put("deletedAt", frequency.getDeletedAt());
            values.put("label", frequency.getLabel());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM frequency WHERE id_sync = ?", new String[]{String.valueOf(frequency.getId())});
            if (cursor.getCount() > 0) {
                // User already exists, update the record
                db.update("frequency", values, "id_sync = ?", new String[]{String.valueOf(frequency.getId())});
            } else {
                // User does not exist, insert a new record
                try {
                    db.insertOrThrow("frequency", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertFrequency", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertFrequency", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Cursor getFrequency(SQLiteDatabase db, String id) {
        List<ModelMontage> montageList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM frequency WHERE fk_category_id = ?", new String[]{id});
        return cursor;
    }

    public Cursor getFrequencyById(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT * FROM frequency WHERE id_sync = ?", new String[]{id});
        return cursor;
    }

    public static Cursor getFrequencyByLocalId(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT * FROM frequency WHERE id = ?", new String[]{id});
        return cursor;
    }

    public boolean verifyAndInsertTask(SQLiteDatabase db, ModelApiMaintTask task) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", task.getId());
            values.put("fk_frequency", task.getFkFrequency().replace("/api/frequencies/", ""));
            values.put("fk_mainttaskcategory", task.getFkMainttaskcategory().getId());
            values.put("label", task.getLabel());
            values.put("createdAt", task.getCreatedAt());
            values.put("deletedAt", task.getDeletedAt());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM task WHERE id_sync = ?", new String[]{String.valueOf(task.getId())});
            if (cursor.getCount() > 0) {
                db.update("task", values, "id_sync = ?", new String[]{String.valueOf(task.getId())});
            } else {
                try {
                    db.insertOrThrow("task", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTask_in", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTask_out", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertGroups(SQLiteDatabase db, ModelMaintTaskCategory groups) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", groups.getId());
            values.put("fk_frequency", groups.getFkFrequency().replace("/api/frequencies/", ""));
            values.put("fk_category", groups.getFkCategory().replace("/api/categories/", ""));
            values.put("label", groups.getLabel());
            values.put("createdAt", groups.getCreatedAt());
            values.put("deletedAt", groups.getDeletedAt());

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM groups WHERE id_sync = ?", new String[]{String.valueOf(groups.getId())});
            if (cursor.getCount() > 0) {
                db.update("groups", values, "id_sync = ?", new String[]{String.valueOf(groups.getId())});
            } else {
                try {
                    db.insertOrThrow("groups", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertGroups_out", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertGroups_in", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Cursor getGroups(SQLiteDatabase db, String fk_frequency, String fk_category) {
        Cursor cursor = db.rawQuery("SELECT * FROM groups WHERE fk_frequency = ? AND fk_category = ?", new String[]{fk_frequency, fk_category});
        return cursor;
    }

    public Cursor getTasks(SQLiteDatabase db, String fk_frequency, String fk_groups) {
        Cursor cursor = db.rawQuery("SELECT * FROM task WHERE fk_frequency = ? AND fk_mainttaskcategory = ?", new String[]{fk_frequency, fk_groups});
        return cursor;
    }

    public Cursor getTasksByFreq(SQLiteDatabase db, String fk_frequency) {
        Cursor cursor = db.rawQuery("SELECT * FROM task WHERE fk_frequency = ?", new String[]{fk_frequency});
        return cursor;
    }

    public Cursor getMaintenanceForm(SQLiteDatabase db, String id) {
        Cursor cursor = db.rawQuery("SELECT * FROM maintenanceform WHERE id_sync = ?", new String[]{id});
        return cursor;
    }

    public boolean verifyAndInsertTaskDone(SQLiteDatabase db, ModelApiMaintTaskDone taskdone) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", taskdone.getId());
            values.put("label", taskdone.getLabel());
            values.put("doneAt", taskdone.getDoneAt());
            values.put("createdAt", taskdone.getCreatedAt());
            values.put("deletedAt", taskdone.getDeletedAt());
            values.put("fk_realmadridfrequency", taskdone.getFkRealmadridfrequency().replace("/api/real_madrid_frequencies/", ""));
            values.put("fk_maintenanceform", taskdone.getFkMaintenanceform().replace("/api/maintenance_forms/", ""));
            values.put("fk_mainttask", taskdone.getFkMainttask().getId());

            // -- SELECT local_maintenanceform & local_realmadridfrequency



            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM taskdone WHERE id_sync = ?", new String[]{String.valueOf(taskdone.getId())});
            if (cursor.getCount() > 0) {
                // -- GET LOCAL ID MAINTENANCE FORM
                Cursor cursor_maintenanceform = db.rawQuery("SELECT * FROM maintenanceform WHERE id_sync = ?", new String[]{String.valueOf(taskdone.getFkMaintenanceform().replace("/api/maintenance_forms/", ""))});
                if (cursor_maintenanceform.getCount() > 0) {
                    if (cursor_maintenanceform.moveToFirst()) {
                        do {
                            values.put("local_maintenanceform", cursor_maintenanceform.getInt(cursor_maintenanceform.getColumnIndexOrThrow("id")));
                        } while (cursor_maintenanceform.moveToNext());
                    }
                }
                // -- END
                // -- GET LOCAL ID REALMADRIDFREQUENCY
                Cursor cursor_rmf = db.rawQuery("SELECT * FROM realmadridfrequency WHERE id_sync = ?", new String[]{String.valueOf(taskdone.getFkRealmadridfrequency().replace("/api/real_madrid_frequencies/", ""))});
                if (cursor_rmf.getCount() > 0) {
                    if (cursor_rmf.moveToFirst()) {
                        do {
                            values.put("local_realmadridfrequency", cursor_rmf.getInt(cursor_rmf.getColumnIndexOrThrow("id")));
                        } while (cursor_rmf.moveToNext());
                    }
                }
                // -- END

//               DEFAULT NOT SYNC
                db.update("taskdone", values, "id_sync = ?", new String[]{String.valueOf(taskdone.getId())});
            } else {
                try {
                    db.insertOrThrow("taskdone", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTaskDone_in", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTaskDone_out", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertRMFrequency(SQLiteDatabase db, ModelApiRMFrequency rmf) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", rmf.getId());
            values.put("startDate", rmf.getStartDate());
            values.put("endDate", rmf.getEndDate());
            values.put("createdAt", rmf.getCreatedAt());
            values.put("deletedAt", rmf.getDeletedAt());
            values.put("fk_itemsite", rmf.getFkItemsite().replace("/api/item_sites/", ""));
            values.put("fk_frequency", rmf.getFkFrequency().replace("/api/frequencies/", ""));

            // Check if the user already exists
            Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE id_sync = ? OR (startDate = ? AND endDate = ?)", new String[]{String.valueOf(rmf.getId()), rmf.getStartDate(), rmf.getEndDate()});
            if (cursor.getCount() > 0) {
                // -- CONTROL PUT SEND = 1
                Boolean control = false;
                if (cursor.moveToFirst()) {
                    do {
                        String sendText = cursor.getString(cursor.getColumnIndexOrThrow("send"));
                        if(sendText.equals("1")){
                            control = true;
                        }
                    } while (cursor.moveToNext());
                }
                // -- DEFAULT : NOT SYNC
                if(!control){
                    db.update("realmadridfrequency", values, "id_sync = ?", new String[]{String.valueOf(rmf.getId())});
                }
            } else {
                try {
                    values.put("send", "0");
                    db.insertOrThrow("realmadridfrequency", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertRMF_in", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertRMF_out", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Long insertMaintenance(SQLiteDatabase db, Map<String, String> map) {
        Long valid_request = 0L;

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = format.format(currentDate);

        ContentValues values = new ContentValues();
        values.put("id_sync", 0);
        values.put("startDate", currentDateStr);
        values.put("coordinates", map.get("coordinates"));
        values.put("status", "0");
        values.put("fk_itemsite", map.get("fk_item"));
        values.put("fk_user", map.get("fk_user"));
        values.put("createdAt", currentDateStr);
        values.put("send", "9");

        try {
            valid_request = db.insertOrThrow("maintenanceform", null, values);
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_INFO_insertMaintenance", e.toString());
        }
        return valid_request;
    }

    public Boolean validMaintenance(SQLiteDatabase db, Map<String, String> map) {
        Boolean valid_request = false;

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        }
        String currentDateStr = format.format(currentDate);
        String idMaint = map.get("idMaint");
        String idSyncRMF = map.get("idSyncRMF");
        String idSyncFreq = map.get("idSyncFreq");
        String complete = map.get("complete");


        ContentValues values = new ContentValues();
        values.put("endDate", currentDateStr);
        values.put("note", map.get("note"));
        values.put("send", "0");

        Log.e("DEBUG_validMaintenance", map.get("idMaint"));
        Log.e("DEBUG_validMaintenance", currentDateStr);
        try {
            Log.e("DEBUG_validMaintenance", " :: 1 :: UPDATE");
            db.update("maintenanceform", values, "id = ?", new String[]{String.valueOf(map.get("idMaint"))});
            if(complete.equals("1")){
                Log.e("DEBUG_validMaintenance", " :: 2 :: COMPLETE = 1");
                // -- COMPARE CURRENTDATE WITH ENDATE RMF
                Cursor cursorRMF = getLocalRMF(db, idSyncRMF);
                Boolean control_superior_date = false;
                Date endDateRMF = currentDate;
                if (cursorRMF.moveToFirst()) {
                    do {
                        Log.e("DEBUG_validMaintenance", " :: 3 :: LOOP RMF");
                        String endDateString = cursorRMF.getString(cursorRMF.getColumnIndexOrThrow("endDate"));
                        try {
                            endDateRMF = dateFormat.parse(endDateString);
                        } catch (ParseException e) {
                            Log.e("DEBUG_validMaintenance", "Error parsing date: " + e.getMessage());
                        }
                        if(currentDate.compareTo(endDateRMF) > 0){
                            Log.e("DEBUG_validMaintenance", " :: 4 :: COMPARE SUP");
                            control_superior_date = true;
                        } else {
                            Log.e("DEBUG_validMaintenance", " :: 5 :: COMPARE INF OR EGUAL");
                        }
                    } while (cursorRMF.moveToNext());
                }
                // -- UPDATE CURRENT RMF
                if(!control_superior_date){
                    Log.e("DEBUG_validMaintenance", " :: 5 :: COMPARE INF SO UPDATE ENDDATE RMF");
                    ContentValues valuesComplete = new ContentValues();
                    valuesComplete.put("endDate", currentDateStr);
                    valuesComplete.put("send", "1");
                    db.update("realmadridfrequency", valuesComplete, "id_sync = ?", new String[]{idSyncRMF});
                }

                // -- CREATE NEW RMF
                Calendar calendar = Calendar.getInstance();
                if(!control_superior_date){
                    Log.e("DEBUG_validMaintenance", " :: 5 :: COMPARE INF SO CURRENTDATE");
                    calendar.setTime(currentDate);
                } else {
                    Log.e("DEBUG_validMaintenance", " :: 5 :: COMPARE INF SO ENDDATERMF");
                    calendar.setTime(endDateRMF);
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date nextDay = calendar.getTime();
                String formattedNextDay = dateFormat.format(nextDay);
                String formattedCurrentDate = dateFormat.format(currentDate);

                Cursor cursorFreq = getLocalFrequency(db, idSyncFreq);
                Log.e("DEBUG_PROCESSUS", ":: 1 ::");
                if (cursorFreq.moveToFirst()) {
                    do {
                        Log.e("DEBUG_PROCESSUS", ":: 2 ::");
                        calendar.setTime(nextDay);
                        calendar.add(Calendar.DAY_OF_YEAR, cursorFreq.getInt(cursorFreq.getColumnIndexOrThrow("n_day")));
                        Date lastDay = calendar.getTime();
                        String formattedLastDay = dateFormat.format(lastDay);
                        Log.e("DEBUG_PROCESSUS", ":: 2 :: " + String.valueOf(nextDay));
                        Log.e("DEBUG_PROCESSUS", ":: 2 :: " + String.valueOf(lastDay));
                        ContentValues valuesNewRMF = new ContentValues();
                        if (cursorRMF.moveToFirst()) {
                            do {
                                Log.e("DEBUG_PROCESSUS", ":: 2 :: " + String.valueOf(cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("fk_itemsite"))));
                                Log.e("DEBUG_PROCESSUS", ":: 2 :: " + String.valueOf(cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("fk_frequency"))));
                                valuesNewRMF.put("id_sync", 0);
                                valuesNewRMF.put("startDate", String.valueOf(formattedNextDay));
                                valuesNewRMF.put("endDate", String.valueOf(formattedLastDay));
                                valuesNewRMF.put("fk_itemsite", cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("fk_itemsite")));
                                valuesNewRMF.put("fk_frequency", cursorRMF.getInt(cursorRMF.getColumnIndexOrThrow("fk_frequency")));
                                valuesNewRMF.put("createdAt", String.valueOf(formattedCurrentDate));
                                valuesNewRMF.put("send", "0");
                            } while (cursorRMF.moveToNext());
                        }
                        try {
                            Log.e("DEBUG_PROCESSUS", ":: 3 ::");
                            db.insert("realmadridfrequency", null, valuesNewRMF);
                        } catch (Exception e) {
                            Log.e("DEBUG_PROCESSUS", ":: 3 ::" + e);
                            // Handle the ParseException here or rethrow it if necessary
                        }

                    } while (cursorFreq.moveToNext());

                }
            }
            valid_request = true;
        } catch(Exception e) {
            Log.e("DEBUG_validMaintenance", e.toString());
        }
        return valid_request;
    }

    public Cursor getLocalFrequency(SQLiteDatabase db, String idFreq) {
        Cursor cursor = db.rawQuery("SELECT * FROM frequency WHERE id_sync = ?", new String[]{idFreq});
        return cursor;
    }

    public Cursor getLocalRMF(SQLiteDatabase db, String idRMF) {
        Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE id_sync = ?", new String[]{idRMF});
        return cursor;
    }
    public Cursor getRMFrequency(SQLiteDatabase db, String startDate, String endDate, String id_item, String id_frequency) {
        Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE startDate LIKE ? AND endDate LIKE ? AND fk_itemsite = ? AND fk_frequency = ?", new String[]{"%" + startDate + "%", "%" + endDate + "%", id_item, id_frequency});
        return cursor;
    }

    public Cursor getLastRMFrequency(SQLiteDatabase db, String id_item, String id_frequency) {
        Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE fk_itemsite = ? AND fk_frequency = ? ORDER BY id DESC LIMIT 1", new String[]{id_item, id_frequency});
        return cursor;
    }

    public List<JSONObject> getRMF(SQLiteDatabase db, String send) {
        List<JSONObject> RMFList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE send = ?", new String[]{send});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("startDate", cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
                    obj.put("endDate", cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_itemsite")));
                    obj.put("fkFrequency", "/api/frequencies/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_frequency")));
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_RMF_GET", e.toString());
                    throw new RuntimeException(e);
                }
                RMFList.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return RMFList;
    }

    public Cursor getCountTaskDone(SQLiteDatabase db, String idMaint, String idRMF, String idTask) {
        Cursor cursor = db.rawQuery("SELECT * FROM taskdone WHERE local_realmadridfrequency = ? AND fk_mainttask = ?", new String[]{idRMF, idTask});
        boolean exists = cursor.getCount() > 0;
        return cursor;
    }
    public Cursor getTDRMF(SQLiteDatabase db, String localrmf) {
        Cursor cursor = db.rawQuery("SELECT * FROM taskdone WHERE local_realmadridfrequency = ?", new String[]{localrmf});
        return cursor;
    }

    public Cursor createRMF(SQLiteDatabase db, Map<String, String> mapRMF) {
        Cursor cursor = null;

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = format.format(currentDate);

        ContentValues values = new ContentValues();
        values.put("id_sync", "0");
        values.put("startDate", mapRMF.get("startDate"));
        values.put("endDate", mapRMF.get("endDate"));
        values.put("fk_itemsite", mapRMF.get("fk_itemsite"));
        values.put("fk_frequency", mapRMF.get("fk_frequency"));
        values.put("createdAt", currentDateStr);
        Boolean checkStep = false;
        try {
            db.insertOrThrow("realmadridfrequency", null, values);
            checkStep = true;
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_CREATERMF", e.toString());
        }
        if(checkStep) {
            String startDate = mapRMF.get("startDate");
            String endDate = mapRMF.get("endDate");
            String fk_itemsite = mapRMF.get("fk_itemsite");
            String fk_frequency = mapRMF.get("fk_frequency");
            cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE startDate = ? AND endDate = ? AND fk_itemsite = ? AND fk_frequency = ?", new String[]{startDate, endDate, fk_itemsite, fk_frequency});
        }
        return cursor;
    }

    public Boolean checkTaskDone(SQLiteDatabase db, Map<String, String> map) {
        Boolean valid_request = false;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM taskdone WHERE fk_mainttask = ? AND local_realmadridfrequency = ? AND local_maintenanceform = ?", new String[]{map.get("MaintTask"), map.get("localRMF"), map.get("localMaintenance")});
            if (cursor.getCount() > 0) {
                valid_request = true;
            }
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_INFO_insertTaskDone", e.toString());
        }
        return valid_request;
    }

    public Long insertTaskDone(SQLiteDatabase db, Map<String, String> map) {
        Long valid_request = 0L;

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        String currentDateStr = format.format(currentDate);

        ContentValues values = new ContentValues();
        values.put("id_sync", 0);
        values.put("label", map.get("label"));
        values.put("fk_mainttask", map.get("MaintTask"));
        values.put("createdAt", currentDateStr);
        values.put("doneAt", map.get("doneAt"));
        values.put("local_realmadridfrequency", map.get("localRMF"));
        values.put("local_maintenanceform", map.get("localMaintenance"));

        try {
            valid_request = db.insertOrThrow("taskdone", null, values);
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_INFO_insertTaskDone", e.toString());
        }
        return valid_request;
    }

    public Integer deleteTaskDone(SQLiteDatabase db, Map<String, String> map) {
        int rowsDeleted = db.delete("taskdone", "fk_mainttask = ? AND local_realmadridfrequency = ? AND local_maintenanceform = ?", new String[]{map.get("MaintTask"), map.get("localRMF"), map.get("localMaintenance")});
        return rowsDeleted;
    }

    public static Long checkAwaitMaint(SQLiteDatabase db, String id, String idUser) {
        Long valid_request = 0L;
        Cursor cursor = db.rawQuery("SELECT * FROM maintenanceform WHERE fk_itemsite = ? AND fk_user = ? AND send = ?", new String[]{id, idUser, "9"});

        if (cursor.moveToFirst()) {
            do {
                valid_request = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return valid_request;
    }

    public Boolean deleteOldMaintenance(SQLiteDatabase db, Map<String, String> map) {
        Cursor cursor = db.rawQuery("SELECT * FROM maintenanceform WHERE id != ? AND fk_itemsite = ? AND fk_user = ? AND send = ?", new String[]{map.get("Id"), map.get("FkItemSite"), map.get("FkUser"), "9"});
        if (cursor.moveToFirst()) {
            do {
                int rowsDeleted = db.delete("maintenanceform", "id = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")))});
                int tasksDeleted = db.delete("taskdone", "local_maintenanceform = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id")))});
            } while (cursor.moveToNext());
        }
        cursor.close();
        return true;
    }

    public static Cursor getHS(SQLiteDatabase db, Integer idUser) {
        Cursor cursor = db.rawQuery("SELECT apps.label as apps_label, site.label as site_label, offtime.startAt as off_start FROM appareil as apps " +
                        "INNER JOIN site ON apps.id_site = site.id_sync " +
                        "INNER JOIN offtime ON apps.id_sync = offtime.fk_itemsite " +
                "WHERE apps.access = ? AND offtime.endAt IS NULL",
                new String[]{"1"});
        return cursor;
    }

    public static Cursor getLocalIntervention(SQLiteDatabase db, Integer idUser) {
        Cursor cursor = db.rawQuery("SELECT id, startDate FROM intervention " +
                        "WHERE fkUser = ?",
                new String[]{String.valueOf(idUser)});
        return cursor;
    }

    public static Cursor getLocalMaintenance(SQLiteDatabase db, Integer idUser) {
        Cursor cursor = db.rawQuery("SELECT id, startDate FROM maintenanceform " +
                        "WHERE fk_user = ?",
                new String[]{String.valueOf(idUser)});
        return cursor;
    }

    public static Cursor getRetard(SQLiteDatabase db, Integer idUser) {
        Cursor cursor = db.rawQuery("SELECT apps.id_sync as apps_idsync, apps.label as apps_label, apps.onAt as apps_onAt, site.label as site_label, freq.id as fid, freq.n_day as nday, freq.label as freq_label FROM appareil as apps " +
                        "INNER JOIN site ON apps.id_site = site.id_sync " +
                        "INNER JOIN frequency as freq ON apps.fk_category = freq.fk_category_id " +
                        "WHERE apps.access = ? AND apps.deletedAt IS NULL AND site.deletedAt IS NULL AND freq.deletedAt IS NULL",
                new String[]{"1"});
        return cursor;
    }

    public List<JSONObject> getMaintenances(SQLiteDatabase db, String id) {
        List<JSONObject> maintenanceList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM maintenanceform WHERE id_sync = ? AND send = ?", new String[]{id, "0"});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("startDate", cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
                    obj.put("endDate", cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
                    obj.put("note", cursor.getString(cursor.getColumnIndexOrThrow("note")));
                    obj.put("coordinates", cursor.getString(cursor.getColumnIndexOrThrow("coordinates")));
                    obj.put("status", 0);
                    obj.put("ref", "BE_");
                    obj.put("fkUser", "/api/users/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_user")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_itemsite")));
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_MAINTENANCE_GET", e.toString());
                    throw new RuntimeException(e);
                }
                maintenanceList.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return maintenanceList;
    }

    public Boolean updateMaintenance(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            values.put("send", "1");
            db.update("maintenanceform", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateMaintenance", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public List<JSONObject> getLocalTaskDone(SQLiteDatabase db, String idLocalMaintenance) {
        List<JSONObject> maintenanceList = new ArrayList<>();
        Cursor cursor;
        if(idLocalMaintenance.length() > 0 && !idLocalMaintenance.equals("0")){
            cursor = db.rawQuery("SELECT * FROM taskdone WHERE local_maintenanceform = ? AND id_sync = ? AND deletedAt IS NULL", new String[]{idLocalMaintenance, "0"});
        } else {
            cursor = db.rawQuery("SELECT * FROM taskdone WHERE id_sync = ? AND deletedAt IS NULL", new String[]{"0"});
        }
        if (cursor.moveToFirst()) {
            do {
                String fkm = "";
                String fkrmf = "";
                    // -- get fk_maintenanceform if NULL
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_maintenanceform")) > 0){
                        fkm = cursor.getString(cursor.getColumnIndexOrThrow("fk_maintenanceform"));
                    } else {
                        Cursor cursor_maintenance = db.rawQuery("SELECT * FROM maintenanceform WHERE id = ? AND deletedAt IS NULL", new String[]{cursor.getString(cursor.getColumnIndexOrThrow("local_maintenanceform"))});

                        if (cursor_maintenance.moveToFirst()) {
                            do {
                                fkm = cursor_maintenance.getString(cursor_maintenance.getColumnIndexOrThrow("id_sync"));
                            } while (cursor_maintenance.moveToNext());
                        }
                    }

                    // -- get fk_realmadridfrequency if NULL
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_realmadridfrequency")) > 0){
                        fkrmf = cursor.getString(cursor.getColumnIndexOrThrow("fk_realmadridfrequency"));
                    } else {
                        Cursor cursor_rmf = db.rawQuery("SELECT * FROM realmadridfrequency WHERE id = ? AND deletedAt IS NULL", new String[]{cursor.getString(cursor.getColumnIndexOrThrow("local_realmadridfrequency"))});

                        if (cursor_rmf.moveToFirst()) {
                            do {
                                fkrmf = cursor_rmf.getString(cursor_rmf.getColumnIndexOrThrow("id_sync"));
                            } while (cursor_rmf.moveToNext());
                        }
                    }

                if(fkm.length() > 0 && fkrmf.length() > 0){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                        obj.put("label", cursor.getString(cursor.getColumnIndexOrThrow("label")));
                        obj.put("fkMaintenanceform", "/api/maintenance_forms/"+fkm);
                        obj.put("fkRealmadridfrequency", "/api/real_madrid_frequencies/"+fkrmf);
                        obj.put("fkMainttask", "/api/maint_tasks/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_mainttask")));
                        obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                        obj.put("doneAt", cursor.getString(cursor.getColumnIndexOrThrow("doneAt")));
                    } catch (JSONException e) {
                        Log.e("DEBUG_TASKDONE_GET", e.toString());
                        throw new RuntimeException(e);
                    }
                    maintenanceList.add(obj);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return maintenanceList;
    }

    public static Boolean updateTaskDoneRMF(SQLiteDatabase db, String idLocalMaintenance, String idSyncMaintenance) {
        Boolean valid_request = true;
        Cursor cursor = db.rawQuery("SELECT * FROM taskdone WHERE local_maintenanceform = ? AND deletedAt IS NULL AND local_realmadridfrequency IS NOT NULL",
                new String[]{idLocalMaintenance});

        if (cursor.moveToFirst()) {
            do {
                Cursor cursor_freq = getFrequencyByLocalId(db, String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("local_realmadridfrequency"))));
                if (cursor_freq.moveToFirst()) {
                    do {
                        try {
                            Integer idSyncFrequency = cursor_freq.getInt(cursor_freq.getColumnIndexOrThrow("id_sync"));

                            ContentValues values = new ContentValues();
                            values.put("fk_realmadridfrequency", idSyncFrequency);
                            values.put("fk_maintenanceform", idSyncMaintenance);
                            db.update("taskdone", values, "id = ? AND local_realmadridfrequency = ?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id"))), String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("local_realmadridfrequency")))});
                        } catch (Exception e) {
                            valid_request = false;
                            Log.e("DEBUG_TASKDONE_GET", e.toString());
                            throw new RuntimeException(e);
                        }

                    } while (cursor_freq.moveToNext());
                }
            } while (cursor.moveToNext());
        }

        return valid_request;
    }

    public Boolean updateTaskDone(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            db.update("taskdone", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateTaskDone", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public Boolean updateRMFrequency(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            db.update("realmadridfrequency", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateRMfrequency", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }


    public Boolean updateRMF(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            values.put("send", "0");
            db.update("offtime", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateRMF", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }


    public List<JSONObject> getRMFrequency(SQLiteDatabase db, String id_sync) {
        List<JSONObject> RMFrequencyList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM realmadridfrequency WHERE id_sync = ?", new String[]{id_sync});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("startDate", cursor.getString(cursor.getColumnIndexOrThrow("startDate")));
                    obj.put("endDate", cursor.getString(cursor.getColumnIndexOrThrow("endDate")));
                    obj.put("fkItemsite", "/api/item_sites/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_itemsite")));
                    obj.put("fkFrequency", "/api/frequencies/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_frequency")));
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_RMFrequency_GET", e.toString());
                    throw new RuntimeException(e);
                }
                RMFrequencyList.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return RMFrequencyList;
    }

    public boolean verifyAndInsertBonIntervention(SQLiteDatabase db, ModelApiBonIntervention bon) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", bon.getId());
            values.put("reference", bon.getReference());
            values.put("title", bon.getTitle());
            values.put("description", bon.getDescription());
            values.put("state", bon.getState());
            values.put("doneAt", bon.getDoneAt());
            values.put("doneTime", bon.getDoneTime());
            values.put("origin", bon.getOrigin());
            values.put("type", bon.getType());
            values.put("signataire", bon.getSignataire());
            values.put("signature", bon.getSignature());
            values.put("fk_demand", bon.getFkDemand().replace("/api/demands/", ""));
            values.put("fk_note", bon.getFkNote().replace("/api/notes/", ""));
            values.put("fk_user", bon.getFkUser().replace("/api/users/", ""));
            values.put("fk_thirdparty", bon.getFkThirdparty().replace("/api/thirdparties/", ""));
            values.put("createdAt", bon.getCreatedAt());
            values.put("deletedAt", bon.getDeletedAt());

            // Check if the bon_intervention already exists
            Cursor cursor = db.rawQuery("SELECT * FROM bon_intervention WHERE id_sync = ?", new String[]{String.valueOf(bon.getId())});
            if (cursor.getCount() > 0) {
                // Bon_intervention already exists, update the record
                db.update("bon_intervention", values, "id_sync = ?", new String[]{String.valueOf(bon.getId())});
            } else {
                // Bon_intervention does not exist, insert a new record
                try {
                    db.insertOrThrow("bon_intervention", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertBonIntervention_IN", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertBonIntervention_OUT", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public List<JSONObject> getBonIntervention(SQLiteDatabase db, String id_sync) {
        List<JSONObject> BonList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM bon_intervention WHERE id_sync = ?", new String[]{id_sync});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("reference", cursor.getString(cursor.getColumnIndexOrThrow("reference")));
                    obj.put("title", cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    obj.put("description", cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    obj.put("state", cursor.getString(cursor.getColumnIndexOrThrow("state")));
                    obj.put("doneAt", cursor.getString(cursor.getColumnIndexOrThrow("doneAt")));
                    obj.put("doneTime", cursor.getInt(cursor.getColumnIndexOrThrow("doneTime")));
                    obj.put("origin", cursor.getString(cursor.getColumnIndexOrThrow("origin")));
                    obj.put("type", cursor.getString(cursor.getColumnIndexOrThrow("type")));
                    obj.put("signataire", cursor.getString(cursor.getColumnIndexOrThrow("signataire")));
                    obj.put("signature", cursor.getString(cursor.getColumnIndexOrThrow("signature")));
                    obj.put("signature", cursor.getString(cursor.getColumnIndexOrThrow("signature")));
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_demand")) > 0){
                        obj.put("fkDemand", "/api/demands/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_demand")));
                    }
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_note")) > 0){
                        obj.put("fkNote", "/api/notes/"+cursor.getInt(cursor.getColumnIndexOrThrow("fk_note")));
                    }
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_user")) > 0) {
                        obj.put("fkUser", "/api/users/" + cursor.getInt(cursor.getColumnIndexOrThrow("fk_user")));
                    }
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_thirdparty")) > 0) {
                        obj.put("fkThirdparty", "/api/thirdparties/" + cursor.getInt(cursor.getColumnIndexOrThrow("fk_thirdparty")));
                    }
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_BONINTERVENTION_GET", e.toString());
                    throw new RuntimeException(e);
                }
                BonList.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return BonList;
    }

    public Boolean updateBonIntervention(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            db.update("bon_intervention", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateBonIntervention", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyAndInsertDemand(SQLiteDatabase db, ModelApiDemand dem) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", dem.getId());
            values.put("reference", dem.getReference());
            values.put("title", dem.getTitle());
            values.put("description", dem.getDescription());
            values.put("origin", dem.getOrigin());
            values.put("origin_phone", dem.getOriginPhone());
            values.put("origin_mail", dem.getOriginMail());
            values.put("state", dem.getState());
            values.put("doneAt", dem.getDoneAt());
            values.put("doneTime", dem.getDoneTime());
            values.put("suspendedAt", dem.getSuspendedAt());
            values.put("suspendedReason", dem.getSuspendedReason());

//            values.put("fk_contact", dem.getFkContactthirdparty().replace("/api/contact_thirdparties/", ""));
//            values.put("fk_itemsite", dem.getFkItemsite().replace("/api/item_sites/", ""));
//            values.put("fk_site", dem.getFkSite().replace("/api/sites/", ""));
//            values.put("fk_tiers", dem.getFkThirdparty().replace("/api/thirdparties/", ""));
//            values.put("fk_user", dem.getFkUser().replace("/api/users/", ""));

            values.put("createdAt", dem.getCreatedAt());
            values.put("deletedAt", dem.getDeletedAt());
            values.put("send", "0");

            // Check if the bon_intervention already exists
            Cursor cursor = db.rawQuery("SELECT * FROM demande WHERE id_sync = ?", new String[]{String.valueOf(dem.getId())});
            if (cursor.getCount() > 0) {
                // Bon_intervention already exists, update the record
                db.update("demande", values, "id_sync = ?", new String[]{String.valueOf(dem.getId())});
                linkFKDemand(db, dem.getId(), dem.getFkContactthirdparty(), dem.getFkItemsite(), dem.getFkSite(), dem.getFkThirdparty(), dem.getFkUser());
            } else {
                // Bon_intervention does not exist, insert a new record
                try {
                    db.insertOrThrow("demande", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertDemande_IN", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertDemande_OUT", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }

    public boolean linkFKDemand(SQLiteDatabase db, Integer id_sync, String[] contacts, String[] itemsites, String[] sites, String[] thirdparties, String[] users) {

        if(id_sync > 0){

            // -- UPDATE ALL DELETED TO REMOVE
            ContentValues removedValues = new ContentValues();
            removedValues.put("deleted", "1");
            db.update("demande_x_contact", removedValues, "demande = ?", new String[]{String.valueOf(id_sync)});
            db.update("demande_x_itemsite", removedValues, "demande = ?", new String[]{String.valueOf(id_sync)});
            db.update("demande_x_site", removedValues, "demande = ?", new String[]{String.valueOf(id_sync)});
            db.update("demande_x_tiers", removedValues, "demande = ?", new String[]{String.valueOf(id_sync)});
            db.update("demande_x_user", removedValues, "demande = ?", new String[]{String.valueOf(id_sync)});

            if(contacts.length > 0) {
                for (String contact : contacts) {
                    String str_contact = String.valueOf(contact.replace("/api/contact_thirdparties/", ""));
                    Cursor cursor_contact = db.rawQuery("SELECT * FROM demande_x_contact WHERE demande = ? AND contact = ?", new String[]{String.valueOf(id_sync), str_contact});
                    if (cursor_contact.getCount() == 0) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("demande", id_sync);
                            values.put("contact", str_contact);
                            values.put("send", "0");
                            values.put("deleted", "0");
                            db.insertOrThrow("demande_x_contact", null, values);
                        } catch (Exception e) {
                            Log.e("DEBUG_LOOP_CONTACT", String.valueOf(e));
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        removedValues.put("deleted", "0");
                        db.update("demande_x_contact", removedValues, "demande = ? AND contact = ?", new String[]{String.valueOf(id_sync), str_contact});
                    }
                }
            }

            if(itemsites.length > 0) {
                for (String itemsite : itemsites) {
                    String str_itemsite = String.valueOf(itemsite.replace("/api/item_sites/", ""));
                    Cursor cursor_itemsite = db.rawQuery("SELECT * FROM demande_x_itemsite WHERE demande = ? AND itemsite = ?", new String[]{String.valueOf(id_sync), str_itemsite});
                    if (cursor_itemsite.getCount() == 0) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("demande", id_sync);
                            values.put("itemsite", str_itemsite);
                            values.put("send", "0");
                            values.put("deleted", "0");
                            db.insertOrThrow("demande_x_itemsite", null, values);
                        } catch (Exception e) {
                            Log.e("DEBUG_LOOP_ITEMSITE", String.valueOf(e));
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        removedValues.put("deleted", "0");
                        db.update("demande_x_itemsite", removedValues, "demande = ? AND itemsite = ?", new String[]{String.valueOf(id_sync), str_itemsite});
                    }
                }
            }

            if(sites.length > 0) {
                for (String site : sites) {
                    String str_site = String.valueOf(site.replace("/api/sites/", ""));
                    Cursor cursor_site = db.rawQuery("SELECT * FROM demande_x_site WHERE demande = ? AND site = ?", new String[]{String.valueOf(id_sync), str_site});
                    if (cursor_site.getCount() == 0) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("demande", id_sync);
                            values.put("site", str_site);
                            values.put("send", "0");
                            values.put("deleted", "0");
                            db.insertOrThrow("demande_x_site", null, values);
                        } catch (Exception e) {
                            Log.e("DEBUG_LOOP_SITE", String.valueOf(e));
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        removedValues.put("deleted", "0");
                        db.update("demande_x_site", removedValues, "demande = ? AND site = ?", new String[]{String.valueOf(id_sync), str_site});
                    }
                }
            }

            if(thirdparties.length > 0) {
                for (String thirdparty : thirdparties) {
                    String str_thirdparty = String.valueOf(thirdparty.replace("/api/thirdparties/", ""));
                    Cursor cursor_thirdparty = db.rawQuery("SELECT * FROM demande_x_tiers WHERE demande = ? AND tiers = ?", new String[]{String.valueOf(id_sync), str_thirdparty});
                    if (cursor_thirdparty.getCount() == 0) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("demande", id_sync);
                            values.put("tiers", str_thirdparty);
                            values.put("send", "0");
                            values.put("deleted", "0");
                            db.insertOrThrow("demande_x_tiers", null, values);
                        } catch (Exception e) {
                            Log.e("DEBUG_LOOP_THIRDPARTY", String.valueOf(e));
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        removedValues.put("deleted", "0");
                        db.update("demande_x_tiers", removedValues, "demande = ? AND tiers = ?", new String[]{String.valueOf(id_sync), str_thirdparty});
                    }
                }
            }

            if(users.length > 0) {
                for (String user : users) {
                    String str_user = String.valueOf(user.replace("/api/users/", ""));
                    Cursor cursor_user = db.rawQuery("SELECT * FROM demande_x_user WHERE demande = ? AND user = ?", new String[]{String.valueOf(id_sync), str_user});
                    if (cursor_user.getCount() == 0) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put("demande", id_sync);
                            values.put("user", str_user);
                            values.put("send", "0");
                            values.put("deleted", "0");
                            db.insertOrThrow("demande_x_user", null, values);
                        } catch (Exception e) {
                            Log.e("DEBUG_LOOP_USER", String.valueOf(e));
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        removedValues.put("deleted", "0");
                        db.update("demande_x_user", removedValues, "demande = ? AND user = ?", new String[]{String.valueOf(id_sync), str_user});
                    }
                }
            }
        } else {
            Log.e("DEBUG_LINKFKDEMAND", "[Demande :: " + id_sync + "] ID SYNC ABSENT");
        }

        // -- DELETE LINK LOOP
        db.delete("demande_x_contact", "demande = ? AND send = ? AND deleted = ?", new String[]{String.valueOf(id_sync), "0", "1"});
        db.delete("demande_x_itemsite", "demande = ? AND send = ? AND deleted = ?", new String[]{String.valueOf(id_sync), "0", "1"});
        db.delete("demande_x_site", "demande = ? AND send = ? AND deleted = ?", new String[]{String.valueOf(id_sync), "0", "1"});
        db.delete("demande_x_tiers", "demande = ? AND send = ? AND deleted = ?", new String[]{String.valueOf(id_sync), "0", "1"});
        db.delete("demande_x_user", "demande = ? AND send = ? AND deleted = ?", new String[]{String.valueOf(id_sync), "0", "1"});

        return true;
    }


    public boolean verifyAndInsertNote(SQLiteDatabase db, ModelApiNote note) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", note.getId());
            values.put("description", note.getDescription());
            values.put("private", note.getPrivated());
            values.put("createdAt", note.getCreatedAt());
            values.put("deletedAt", note.getDeletedAt());
            values.put("fk_demand", note.getFkDemand().replace("/api/demands/", ""));
            values.put("fk_user", note.getFkUser().replace("/api/users/", ""));

            // Check if the note already exists
            Cursor cursor = db.rawQuery("SELECT * FROM note WHERE id_sync = ?", new String[]{String.valueOf(note.getId())});
            if (cursor.getCount() > 0) {
                // Bon_intervention already exists, update the record
                db.update("note", values, "id_sync = ?", new String[]{String.valueOf(note.getId())});
            } else {
                // Note does not exist, insert a new record
                try {
                    db.insertOrThrow("note", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertNote_IN", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertNote_OUT", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }


    public List<JSONObject> getNote(SQLiteDatabase db, String id_sync) {
        List<JSONObject> NoteList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM note WHERE id_sync = ?", new String[]{id_sync});
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("tempId", cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    obj.put("tempIdSync", cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                    obj.put("description", cursor.getString(cursor.getColumnIndexOrThrow("description")));
                    obj.put("private", cursor.getString(cursor.getColumnIndexOrThrow("private")));
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_demand")) > 0){
                        obj.put("fkDemand", "/api/demands/"+cursor.getString(cursor.getColumnIndexOrThrow("fk_demand")));
                    }
                    if(cursor.getInt(cursor.getColumnIndexOrThrow("fk_user")) > 0) {
                        obj.put("fkUser", "/api/users/" + cursor.getInt(cursor.getColumnIndexOrThrow("fk_user")));
                    }
                    obj.put("createdAt", cursor.getString(cursor.getColumnIndexOrThrow("createdAt")));
                } catch (JSONException e) {
                    Log.e("DEBUG_NOTE_GET", e.toString());
                    throw new RuntimeException(e);
                }
                NoteList.add(obj);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return NoteList;
    }


    public Boolean updateNote(SQLiteDatabase db, Integer idInter, Integer idRemote) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", idRemote);
            db.update("note", values, "id = ?", new String[]{idInter.toString()});
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_UpdateNote", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }


    public boolean verifyAndInsertTiers(SQLiteDatabase db, ModelApiTiers tiers) {
        try {
            ContentValues values = new ContentValues();
            values.put("ref", tiers.getRef());
            values.put("label", tiers.getLabel());
            values.put("customer", tiers.getCustomer());
            values.put("supplier", tiers.getSupplier());
            values.put("email", tiers.getEmail());
            values.put("phone", tiers.getPhone());
            values.put("address", tiers.getAddress());
            values.put("zip", tiers.getZip());
            values.put("city", tiers.getCity());
            values.put("origin", tiers.getOrigin());
            values.put("createdAt", tiers.getCreatedAt());
            values.put("deletedAt", tiers.getDeletedAt());

            // Check if the note already exists
            Cursor cursor = db.rawQuery("SELECT * FROM tiers WHERE id_sync = ?", new String[]{String.valueOf(tiers.getId())});
            if (cursor.getCount() > 0) {
                // Bon_intervention already exists, update the record
                db.update("tiers", values, "id_sync = ?", new String[]{String.valueOf(tiers.getId())});
            } else {
                // Note does not exist, insert a new record
                try {
                    db.insertOrThrow("tiers", null, values);
                } catch(Exception e) {
                    Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTiers_IN", e.toString());
                }
            }
            cursor.close();
            return true;
        } catch (Exception e) {
            Log.e("DEBUG_THREAD_INFO_VerifyAndInsertTiers_OUT", String.valueOf(e));
            e.printStackTrace();
            return false;
        }
    }
}
