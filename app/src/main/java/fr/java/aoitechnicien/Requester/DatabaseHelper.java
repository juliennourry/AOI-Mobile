package fr.java.aoitechnicien.Requester;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.java.aoitechnicien.Models.ModelApiItem;
import fr.java.aoitechnicien.Models.ModelApiSite;
import fr.java.aoitechnicien.Models.ModelApiUser;
import fr.java.aoitechnicien.Models.ModelCrossSiteContrat;
import fr.java.aoitechnicien.Models.ModelCrossThirdpartyContrat;
import fr.java.aoitechnicien.Models.ModelItem;
import fr.java.aoitechnicien.Models.ModelSite;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "aoi_tech.db";
    private static final int DATABASE_VERSION = 7;
    String sql;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        sql = "CREATE TABLE IF NOT EXISTS appareil (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, id_site INTEGER NOT NULL, label TEXT NOT NULL, category TEXT NOT NULL, uuid TEXT NOT NULL, access TEXT NOT NULL, onAt DATETIME NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS site (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER NOT NULL, label TEXT NOT NULL, contrat TEXT NOT NULL, tiers TEXT NOT NULL, createdAt DATETIME NOT NULL, deletedAt DATETIME)";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS intervention (id INTEGER PRIMARY KEY AUTOINCREMENT, id_sync INTEGER, fkItemsite INTEGER, fkUser INTEGER, note TEXT, type TEXT, stop TEXT, startDate DATETIME, endDate DATETIME, signName TEXT, signData TEXT, coordinates TEXT,  createdAt TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        sql = "DROP TABLE IF EXISTS user";
        db.execSQL(sql);
        onCreate(db);
        if (oldVersion < 2) {
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
            onCreate(db);
        }
    }

    public boolean onClean(SQLiteDatabase db) {
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

    public boolean verifyAndInsertItem(SQLiteDatabase db, ModelApiItem item) {
        try {
            ContentValues values = new ContentValues();
            values.put("id_sync", item.getId());
            values.put("id_site", item.getSite().getId());
            values.put("label", item.getLabel());
            values.put("category", item.getCategory().getLabel());
            values.put("uuid", item.getUuid());
            values.put("access", "0");
            values.put("onAt", item.getOnAt());
            values.put("createdAt", item.getCreatedAt());
            values.put("deletedAt", item.getDeletedAt());

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

    public Boolean checkUuidItem(SQLiteDatabase db, String uuid) {
        Boolean result = false;
        Cursor cursor = db.rawQuery("SELECT * FROM appareil WHERE access = '1' AND uuid = ?", new String[]{uuid});
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
                ModelItem appareil = new ModelItem(0,0,"","","","","","","", 0);
                appareil.setIdSync(cursor.getInt(cursor.getColumnIndexOrThrow("id_sync")));
                appareil.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
                appareil.setOnAt(cursor.getString(cursor.getColumnIndexOrThrow("onAt")));
                appareil.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                appareil.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("category")));
                appareil.setIdSite(cursor.getString(cursor.getColumnIndexOrThrow("id_site")));
                appareil.setAccess(cursor.getInt(cursor.getColumnIndexOrThrow("access")));
                itemList.add(appareil);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
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

        try {
            db.insertOrThrow("intervention", null, values);
            valid_request = true;
        } catch(Exception e) {
            Log.e("DEBUG_THREAD_INFO_insertIntervention", e.toString());
        }

        return valid_request;
    }

    public String getIdSyncUser(SQLiteDatabase db, String login) {
        String idsync = null;
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{login});
        if (cursor.moveToFirst()) {
            idsync = String.valueOf(cursor.getColumnIndexOrThrow("id_sync"));
        }
        cursor.close();
        db.close();
        return idsync;
    }


}
