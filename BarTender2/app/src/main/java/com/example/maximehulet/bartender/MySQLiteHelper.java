package com.example.maximehulet.bartender;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by maximehulet on 27/04/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    /**
     * Nom du fichier sql contenant les instructions de création de la base de données. Le fichier
     * doit être placé dans le dossier assets/
     */
    private static final String DATABASE_SQL_FILENAME = "BarTender.sql";
    /**
     * Nom du fichier de la base de données.
     */
    private static final String DATABASE_NAME = "BarTender.sqlite";

    /**
     * Version de la base de données (à incrémenter en cas de modification de celle-ci afin que la
     * méthode onUpgrade soit appelée).
     *
     * @note Le numéro de version doit changer de manière monotone.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Instance de notre classe afin de pouvoir y accéder facilement depuis n'importe quel objet.
     */
    private static MySQLiteHelper instance;

    /**
     * Constructeur. Instancie l'utilitaire de gestion de la base de données.
     *
     * @param context Contexte de l'application.
     */
    private MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = this;
    }

    /**
     * Fournit une instance de notre MySQLiteHelper.
     *
     * @return MySQLiteHelper
     */
    public static MySQLiteHelper get() {
        if (instance == null) {
            return new MySQLiteHelper(BarTenderApp.getContext());
        }
        return instance;
    }

    /**
     * Méthode d'initialisation appelée lors de la création de la base de données.
     *
     * @param db Base de données à initialiser
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        initDatabase(db);
    }

    /**
     * Méthode de mise à jour lors du changement de version de la base de données.
     *
     * @param db         Base de données à mettre à jour.
     * @param oldVersion Numéro de l'ancienne version.
     * @param newVersion Numéro de la nouvelle version.
     *
     * @pre La base de données est dans la version oldVersion.
     * @post La base de données a été mise à jour vers la version newVersion.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /**
         * @note : Ici on se contente juste de supprimer toutes les données et de les re-créer par
         * après. Dans une vraie application en production (par ex. sur le Play Store), il faudra
         * faire en sorte que les données enregistrées par l'utilisateur ne soient pas complètement
         * effacées lorsqu'on veut mettre à jour la structure de la base de données.
         */
        deleteDatabase(db);
        onCreate(db);
    }

    /**
     * Crée les tables de la base de données et les remplit.
     *
     * @param db Base de données à initialiser.
     *
     * @note À l'avenir on peut imaginer aller chercher les requêtes à effectuer dans un fichier
     * local (dans le dossier assets) ou sur internet (sur un server distant), au lieu de les
     * encoder en dur ici. (En fait c’est une mauvaise pratique de les encoder en dur comme on a
     * fait ici, mais on a voulu simplifier le code pour des raisons didactiques.) Vous trouverez en
     * commentaires dans cette méthode le code permettant de charger la base de données depuis un
     * fichier SQL placé dans le dossier assets/.
     * @post Les tables nécessaires à l'application sont créées et les données initiales y sont
     * enregistrées.
     */
    private void initDatabase(SQLiteDatabase db) {
       db.execSQL( "DROP TABLE IF EXISTS \"DESCRIPTION\"");
        db.execSQL("CREATE TABLE \"DESCRIPTION\"(\"NAMED\" char(20) not null references \"DRINK\",\"LANGUAGE\" char(20) not null, \"TEXT\" char(700) not null, primary key (NAMED,LANGUAGE))");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Coca','Français','Description de Coca en Français')");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Coca','Anglais','Description of Coca cola in English')");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Fanta','Français','Description de Fanta en Français')");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Fanta','Anglais','Descriptionvof Fanta in English')");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Jupiler-25cl','Français','Description de Jupiler en Français')");
        db.execSQL("INSERT INTO \"DESCRIPTION\" VALUES('Jupiler-25cl','Anglais','Description of Jupiler in English')");
        db.execSQL("DROP TABLE IF EXISTS \"DRINK\"");
        db.execSQL("CREATE TABLE \"DRINK\"(NAMED CHAR(20) NOT NULL PRIMARY KEY, \"TYPED\" CHAR(20) NOT NULL, \"PICTURE\" INTEGER)");
        db.execSQL("INSERT INTO \"DRINK\" VALUES('Coca','soft','coca')");
        db.execSQL("INSERT INTO \"DRINK\" VALUES('Fanta','soft','fanta')");
        db.execSQL("INSERT INTO \"DRINK\" VALUES('Jupiler-25cl','bière',NULL)");
        db.execSQL("DROP TABLE IF EXISTS \"INVENTARY\"");
        db.execSQL("CREATE TABLE \"INVENTARY\"(\"NAMED\" char(20) not null primary key, \"PRICE\" double not null, \"MAXQ\" integer not null, \"RESTQ\" integer not null, \"TRESHOLD\" integer not null default 15)");
        db.execSQL("INSERT INTO \"INVENTARY\" VALUES('Coca',1,300,200,50)");
        db.execSQL("INSERT INTO \"INVENTARY\" VALUES('Fanta',1,300,110,50)");
        db.execSQL("INSERT INTO \"INVENTARY\" VALUES('Jupiler-25cl',1.5,500,50,100)");
        db.execSQL("INSERT INTO \"INVENTARY\" VALUES('Rochefort 10',3,400,60,70)");
        db.execSQL("DROP TABLE IF EXISTS \"MUSIC\"");
        db.execSQL("CREATE TABLE \"MUSIC\"(\"ARTIST\" char(20) not null, \"TITLE\" char(40) not null, \"TIME\" double not null ,\"CATEGORY\" char(30) not null, primary key (ARTIST, TITLE))");
        db.execSQL("INSERT INTO \"MUSIC\" VALUES('Nirvana','Lithium',3.5,'Rock')");
        db.execSQL("INSERT INTO \"MUSIC\" VALUES('Evanescence','Lithium',2.3,'Hard Rock')");
        db.execSQL("INSERT INTO \"MUSIC\" VALUES('Michael Jackson','Beat it',3.5,'Pop')");
        db.execSQL("INSERT INTO \"MUSIC\" VALUES('Michael Jackson','Thriller',3,'Pop')");
        db.execSQL("DROP TABLE IF EXISTS \"ORDER\"");
        db.execSQL("CREATE TABLE \"ORDER\"(\"ID\" integer not null primary key autoincrement, \"NAMED\" char(20) not null, \"TABLE\" integer not null, \"DRINKQ\" integer not null default 1,\"LOGIN\" char(20) not null references \"PERSON\")");
        db.execSQL("INSERT INTO \"ORDER\" VALUES(25,'Coca',1,2,'Jean')");
        db.execSQL("INSERT INTO \"ORDER\" VALUES(26,'Coca',1,2,'Simon')");
        db.execSQL("INSERT INTO \"ORDER\" VALUES(27,'Fanta',2,4,'Simon')");
        db.execSQL("DROP TABLE IF EXISTS \"PERSON\"");
        db.execSQL("CREATE TABLE \"PERSON\" (\"LOGIN\" char(20) PRIMARY KEY  NOT NULL ,\"PASSWORD\" char(30) NOT NULL ,\"TYPEP\" char(1) NOT NULL  DEFAULT ('Client') ,\"TOTSELL\" double)");
        db.execSQL("INSERT INTO \"PERSON\" VALUES('Jean','00000','Waiter',20)");
        db.execSQL("INSERT INTO \"PERSON\" VALUES('Paul','00000','Boss',NULL)");
        db.execSQL("INSERT INTO \"PERSON\" VALUES('Simon','11111','Waiter',20)");
        db.execSQL("INSERT INTO \"PERSON\" VALUES('Jacques','22222','Client',NULL)");
        db.execSQL("DROP TABLE IF EXISTS \"PLAYLIST\"");
        db.execSQL("CREATE TABLE \"PLAYLIST\"(\"NUMBER\" integer not null primary key, \"ARTIST\" char(20) not null, \"TITLE\" char(40) not null, \"TABLE\" integer not null)");
        db.execSQL("INSERT INTO \"PLAYLIST\" VALUES(1,'Michael Jackson','Thriller',1)");
        db.execSQL("INSERT INTO \"PLAYLIST\" VALUES(2,'Nirvana','Lithium',2)");

/*
        try {
            // Ouverture du fichier sql.
            BufferedReader in = new BufferedReader(new InputStreamReader(BarTenderApp.getContext().getAssets().open(DATABASE_SQL_FILENAME)));

            String line;
            // Parcourt du fichier ligne par ligne.
            while ((line = in.readLine()) != null) {
                /**
                 * @note : Pour des raisons de facilité, on ne prend en charge ici que les fichiers
                 * contenant une instruction par ligne. Si des instructions SQL se trouvent sur deux
                 * lignes, cela produira des erreurs (car l'instruction sera coupée)
                 */
          /*      if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
                    Log.d("MySQL query", line);
                    db.execSQL(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur de lecture du fichier " + DATABASE_SQL_FILENAME + " : " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL lors de la création de la base de données." +
                    "Vérifiez que chaque instruction SQL est au plus sur une ligne." +
                    "L'erreur est : " + e.getMessage(), e);
        }*/
    }
    public boolean open(){
        try {
            getWritableDatabase();
            return true;
        }catch (Throwable t){
            return false;
        }
    }



    /**
     * Supprime toutes les tables dans la base de données.
     *
     * @param db Base de données.
     *
     * @post Les tables de la base de données passées en argument sont effacées.
     */
    private void deleteDatabase(SQLiteDatabase db) {
        Cursor c = db.query("sqlite_master", new String[]{"name"}, "type = 'table' AND name NOT LIKE '%sqlite_%'", null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            db.execSQL("DROP TABLE IF EXISTS " + c.getString(0));
            c.moveToNext();
        }
    }

}
