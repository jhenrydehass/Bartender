package com.example.maximehulet.bartender.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.util.ArrayList;

import com.example.maximehulet.bartender.MySQLiteHelper;


/**
 * Created by maximehulet on 27/04/15.
 */
public class User {
    private static final  String DB_COLUMN_LOGIN= "LOGIN";
    private static final String DB_COLUMN_PASSWORD = "PASSWORD";
    private static final String DB_TABLE = "PERSON";
    private static final String DB_COLUMN_TYPEP="TYPEP";

    /* Identifiant unique de l'utilisateur courant. Correspond à _id dans la base de données.
    */
    private final String username;
    /**
     * Nom (unique) de l'utilisateur courant. Correspond à u_nom dans la base de données.
     */
    private String password;
    /**
     * Mot de passe de l'utilisateur courant. Correspond à u_password dans la base de données.
     */
    private String typep = "Client";

    /**
     * Constructeur de l'utilisateur. Initialise une instance de l'utilisateur présent dans la base
     * de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même utilisateur.
     */
    private User(String username, String password, String typep) {

        this.username = username;
        this.typep = typep;
        this.password = password;

    }

    /**
     * Fournit l'id de l'utilisateur courant.
     */
    public String getLogin() {
        return username;
    }

    /**
     * Fournit le nom de l'utilisateur courant.
     */
    public String getPassword() {
        return password;
    }
    public String getTypep() {return typep;}

    /**
     * Connecte l'utilisateur courant.
     *
     * @param passwordToTry le mot de passe entré.
     *
     * @return Vrai (true) si l'utilisateur à l'autorisation de se connecter, false sinon.
     */
    public boolean login(String passwordToTry) {
        if (this.password.equals(passwordToTry)) {
            // Si le mot de passe est correct, modification de l'utilisateur connecté.
            User.connectedUser = this;
            return true;
        }
        return false;
    }

    /**
     * Fournit une représentation textuelle de l'utilisateur courant. (Ici le nom)
     *
     * @note Cette méthode est utilisée par l'adaptateur ArrayAdapter afin d'afficher la liste des
     * utilisateurs. (Voir LoginActivity).
     */
    public String toString() {
        return getLogin();
    }

    /******************************************************************************
     * Partie static de la classe.
     ******************************************************************************/



    /**
     * Utilisateur actuellement connecté à l'application. Correspond à null si aucun utilisateur
     * n'est connecté.
     */
    private static User connectedUser = null;

    /**
     * Fournit l'utilisateur actuellement connecté.
     */
    public static User getConnectedUser() {
        return User.connectedUser;
    }


    /**
     * Déconnecte l'utilisateur actuellement connecté à l'application.
     */
    public static void logout() {

        User.connectedUser = null;

    }

    public static User getUser(String name) {
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        String[] colonnes = {DB_COLUMN_LOGIN, DB_COLUMN_PASSWORD, DB_COLUMN_TYPEP};

        Cursor cursor = db.query(DB_TABLE, colonnes, DB_COLUMN_LOGIN + " LIKE \"" + name + "\"", null, null, null, null);

        return cursorToUser(cursor);
    }
    private static User cursorToUser(Cursor cursor){

        if(cursor.getCount()==0){
            return null;
        }
        cursor.moveToFirst();
        User user = new User(cursor.getString(0),cursor.getString(1),cursor.getString(2));

        cursor.close();

        return user;
    }
    public static long insertUser(User user){
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DB_COLUMN_LOGIN,user.getLogin());
        values.put(DB_COLUMN_PASSWORD,user.getPassword());
        values.put(DB_COLUMN_TYPEP,user.getTypep());

        return db.insert(DB_TABLE,null,values);

    }

    public static User addUser(String username, String password,String typep){
        User user = new User(username,password,typep);
        insertUser(user);
        return user;
    }

}
