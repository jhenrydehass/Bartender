package be.uclouvain.lsinf1225.bartender.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import android.graphics.BitmapFactory;

import be.uclouvain.lsinf1225.bartender.BarTenderApp;
import be.uclouvain.lsinf1225.bartender.MySQLiteHelper;


/**
 * Représente un élément de collection et permet de le gérer.
 *
 * Cette classe représente un élément de collection et permet de le gérer. Elle utilise pour cela la
 * base de données par l'intermédiaire du MySQLiteHelper.
 *
 * Les méthodes statiques permettent de récupérer des listes d'éléments de collection. Afin de
 * déterminer l'ordre de tri des éléments pour ces méthodes, les variables de classe order et
 * order_by sont utilisées. La variable order fait référence au nom de colonne sur lequel est
 * effectué le tri. Et la variable order_by est soit ASC (pour un tri croissant) ou DESC (pour un
 * tri décroissant).
 *
 * @author Damien Mercier
 * @version 1
 */
public class Song {

    /*
     * Noms des tables et des colonnes dans la base de données.
     */
    public static final String DB_TABLE_S = "songs";
    public static final String DB_TABLE_OWNS = "owns";

    public static final String DB_COL_ID = "s_id";
    public static final String DB_COL_TITLE = "s_title";
    public static final String DB_COL_ARTIST = "s_artist";
    public static final String DB_COL_FILENAME = "s_filename";
    public static final String DB_COL_UID = "u_id";
    public static final String DB_COL_RATING = "s_rating";

    /* Pour éviter les ambiguités dans les requêtes, il faut utiliser le format
     *      nomDeTable.nomDeColonne
     * lorsque deux tables possèdent le même nom de colonne.
     */
    public static final String DB_COL_S_ID = DB_TABLE_S + "." + DB_COL_ID;
    public static final String DB_COL_OWNS_ID = DB_TABLE_OWNS + "." + DB_COL_ID;

    /*
     * Pour joindre les deux tables dans une même requête.
     */
    public static final String DB_TABLES = DB_TABLE_S + " INNER JOIN " + DB_TABLE_OWNS + " ON " + DB_COL_S_ID + " = " + DB_COL_OWNS_ID;


    /**
     * Nom de colonne sur laquelle le tri est effectué
     */
    public static String order_by = DB_COL_TITLE;
    /**
     * Ordre de tri : ASC pour croissant et DESC pour décroissant
     */
    public static String order = "ASC";

    /**
     * ID unique de notre élément courant. Correspond à ci_id dans la base de données.
     */
    private final int id;

    /**
     * Nom de notre élément courant. Correspond à ci_name dans la base de données.
     */
    private String title;

    /**
     * Description de notre élément courant. Correspond à ci_description dans la base de données.
     * Est facultatif.
     */
    private String artist;

    /**
     * Note (rating) de notre élément courant entre 0 et 5. Correspond à ci_rating dans la base de
     * données. Est facultatif. Comme la note dépend de l'utilisateur, on a ici la liste des
     * différentes notes par id de l'utilisateur.
     */
    private SparseArray<Float> rating;

    /**
     * Nom du fichier photo de notre élément courant. Correspond à ci_picture dans la base de
     * données. Est facultatif.
     */
    private String filename;


    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getFilename() {
        return filename;
    }

    public String toString(){
        return getTitle()+ " - "+ getArtist();
    }

    /**
     * Constructeur de notre élément de collection. Initialise une instance de l'élément présent
     * dans la base de données.
     *
     * @note Ce constructeur est privé (donc utilisable uniquement depuis cette classe). Cela permet
     * d'éviter d'avoir deux instances différentes d'un même élément dans la base de données, nous
     * utiliserons la méthode statique get(ciId) pour obtenir une instance d'un élément de notre
     * collection.
     */
    private Song(int ciId) {

        // On enregistre l'id dans la variable d'instance.
        this.id = ciId;
        // On enregistre l'instance de l'élément de collection courant dans la hashMap.
        Song.songSparseArray.put(ciId, this);

        // On charge les données depuis la base de données.
        loadData();
    }


    /**
     * Fournit l'id de l'élément de collection courant.
     */
    public int getId() {
        return id;
    }

    /**
     * Fournit le nom de l'élément de collection courant.
     */


    /**
     * Fournit la description de l'élément de collection courant ou null s'il n'y a aucune
     * description.
     */


    /**
     * Fournit la note de l'élément de collection courant (comprise entre 0 et 5).
     *//*
    public float getRating() {
       return rating.get(User.getConnectedUser().getId());
    }*/

    /**
     * Fournit l'image Bitmap correspondant à l'élément de collection courant ou null s'il n'y en a
     * pas.
     */

    public Bitmap getPicture() {
        if (this.filename == null) {
            // S'il n'y a pas de nom de fichier, il n'y a pas d'image.
            return null;
        }

        try {
            /**
             *  @note Pour des questions de facilité, le choix a été fait de stocker les fichiers
             *  des photos dans la mémoire interne de l'application.
             *  Lisez https://developer.android.com/training/basics/data-storage/files.html afin de
             *  comprendre les différentes possibilités.*/


           FileInputStream in = BarTenderApp.getContext().openFileInput(filename);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();

            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Modifie la note de l'objet courant pour l'utilisateur actuellement connecté à l'application.
     *
     * @param newRating Nouvelle note pour l´objet courant.
     *
     * @return Retourne vrai (true) si l´opération s´est bien déroulée, faux (false) sinon.
     * @pre newRating doit être compris dans [0;5].
     * @post Modifie la newRating de l'objet courant dans la base de données.
     */
    public boolean setRating(float newRating) {

        // Vérification de la pré-condition.
        if (newRating < 0 || newRating > 5) {
            return false;
        }

        // Récupération de la base de données en mode "écriture".
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

        // Indique les valeurs à mettre à jour.
        ContentValues values = new ContentValues();
        values.put(DB_COL_RATING, newRating);

       /* // Indique sur quelle ligne effectuer la mise à jour.
        String selection = DB_COL_UID + " = ? AND " + DB_COL_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(User.getConnectedUser().getId()), String.valueOf(id)};*/

       /* // Requête UPDATE sur la base de données.
        db.update(DB_TABLE_OWNS, values, selection, selectionArgs);*/

        // Fermeture de la base de données.
        db.close();

        /*// Mise à jour de la note de l'élément courant pour l'utilisateur connecté.
        this.rating.put(User.getConnectedUser().getId(), newRating);*/

        return true;
    }

    /**
     * (Re)charge les informations depuis la base de données.
     *
     * @pre L'id de l'élément est indiqué dans this.id et l'élément existe dans la base de données.
     * @post Les informations de l'élément sont chargées dans les variables d'instance de la
     * classe.
     */
    private void loadData() {
        // Récupération de la base de données en mode "lecture".
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        // Colonnes pour lesquelles il nous faut les données.
        String[] columns = new String[]{DB_COL_TITLE, DB_COL_ARTIST, DB_COL_FILENAME};

        // Critères de sélection de la ligne :
        String selection = DB_COL_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        // Requête SELECT à la base de données.
        Cursor c = db.query(DB_TABLE_S, columns, selection, selectionArgs, null, null, null);

        // Placement du curseur sur le  premier résultat (ici le seul puisque l'objet est unique).
        c.moveToFirst();

        // Copie des données de la ligne vers les variables d'instance de l'objet courant.
        this.title = c.getString(0);
        this.artist = c.getString(1);
        this.filename = c.getString(2);

        // Fermeture du curseur.
        c.close();

        /* Récupération des différentes notes pour les différents utilisateurs. */

        this.rating = new SparseArray<Float>();

        // Colonnes à récupérérer.
        columns = new String[]{DB_COL_UID, DB_COL_RATING};

        // Critères de sélection de la ligne.
        selection = DB_COL_ID + " = ?";
        selectionArgs = new String[]{String.valueOf(id)};

        // Requête SELECT à la base de données.
        c = db.query(DB_TABLE_OWNS, columns, selection, selectionArgs, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            // On copie les résultats dans la variable d'instance rating.
            this.rating.put(c.getInt(0), c.getFloat(1));
            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        c.close();
        db.close();
    }


    /******************************************************************************
     * Partie static de la classe.
     ******************************************************************************/

    /**
     * Contient les instances déjà existantes des objets afin d'éviter de créer deux instances du
     * même objet.
     */
    private static final SparseArray<Song> songSparseArray = new SparseArray<Song>();


    /**
     * Crée un nouvel élément dans la base de données et l'associe à l'utilisateur actuellement
     * connecté.
     *
     * @param name        Nom du nouvel élément de collection.
     * @param description Description du nouvel élément de collection.
     * @param rating      Note pour le nouvel élément de collection. Doit être comprise entre 0 et 5
     *                    (sinon est considérée comme null).
     * @param picture     Fichier picture pour le nouvel objet.
     *
     * @return Vrai (true) en cas de succès, faux (false) en cas d'échec.
     * @post Enregistre le nouvel objet dans la base de données.
     */
    public static boolean create(String name, String description, float rating, String picture) {

        // Récupération de la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getWritableDatabase();

        // Définition des valeurs pour le nouvel élément dans la table "collected_items".
        ContentValues cv = new ContentValues();
        cv.put(DB_COL_TITLE, name);
        cv.put(DB_COL_ARTIST, description);
        cv.put(DB_COL_FILENAME, picture);

        // Ajout à la base de données (table collected_items).
        int ci_id = (int) db.insert(DB_TABLE_S, null, cv);

        if (ci_id == -1) {
            return false; // En cas d'erreur d'ajout, on retourne false directement.
        }
        cv.clear();

        // Définition des valeurs pour le nouvel élément dans la table "owns".
        cv.put(DB_COL_ID, ci_id);
       // cv.put(DB_COL_UID, User.getConnectedUser().getId());
        cv.put(DB_COL_RATING, rating);

        int result = (int) db.insert(DB_TABLE_OWNS, null, cv);

        if (result == -1) {
            // En cas d'erreur dans l'ajout de la deuxième table, il faut supprimer la ligne qu'on
            // vient d'ajouter dans la première table pour ne pas qu'il y ait un élément qui n'est
            // dans la collection de personne.
            db.delete(DB_TABLE_S, DB_COL_ID + " = ?", new String[]{String.valueOf(ci_id)});
            return false;
        }
        return true;
    }

    /**
     * Fournit la liste de tous les éléments de la collection de l'utilisateur courant.
     *
     * @return Liste d'éléments.
     */
    /*public static ArrayList<Song> getSongs() {
        // Récupération de l'ID de l'utilisateur courant.
        //int u_id = User.getConnectedUser().getId();

        // Critère de sélection : appartient à l'utilisateur courant.
        String selection = DB_COL_UID + " = ?";
       // String[] selectionArgs = new String[]{String.valueOf(u_id)};

        // Le critère de sélection est passé à la sous-méthode de récupération des éléments.
        //return getSongs(selection, selectionArgs);
    }*/

    /**
     * Fournit la liste de tous les éléments de la collection de l'utilisateur courant dont le nom
     * contient searchQuery.
     *
     * @param searchQuery Requête de recherche.
     *
     * @return Liste d'éléments de collection répondant à la requête de recherche.
     */
    /*public static ArrayList<Song> searchCollectedItems(String searchQuery) {
        // Récupération de l'id de l'utilisateur courant.
       // int u_id = User.getConnectedUser().getId();

        // Critères de sélection (partie WHERE) : appartiennent à l'utilisateur courant et ont un nom
        // correspondant à la requête de recherche.
        String selection = DB_COL_UID + " = ? AND " + DB_COL_TITLE + " LIKE ?";
       // String[] selectionArgs = new String[]{String.valueOf(u_id), "%" + searchQuery + "%"};

        // Les critères de selection sont passés à la sous-méthode de récupération des éléments.
        //return getSongs(selection, selectionArgs);
    }*/

    /**
     * Fournit la liste de tous les objets correspondant aux critères de sélection demandés.
     *
     * Cette méthode est une sous-méthode de getCollectedItems et de searchCollectedItems.
     *
     * @param selection     Un filtre déclarant quels éléments retourner, formaté comme la clause
     *                      SQL WHERE (excluant le WHERE lui-même). Donner null retournera tous les
     *                      éléments.
     * @param selectionArgs Vous pouvez inclure des ? dans selection, qui seront remplacés par les
     *                      valeurs de selectionArgs, dans leur ordre d'apparition dans selection.
     *                      Les valeurs seront liées en tant que chaînes.
     *
     * @return Liste d'objets. La liste peut être vide si aucun objet ne correspond.
     */

    private static ArrayList<Song> getSongs(String selection, String[] selectionArgs) {
        // Initialisation de la liste des collectedItems.
        ArrayList<Song> songs = new ArrayList<Song>();

        // Récupération du SQLiteHelper pour récupérer la base de données.
        SQLiteDatabase db = MySQLiteHelper.get().getReadableDatabase();

        // Colonnes à récupérer. Ici uniquement l'id de l'élément, le reste sera récupéré par
        // loadData() à la création de l'instance de l'élément. (choix de développement).
        String[] columns = new String[]{DB_COL_S_ID};

        // Requête SELECT à la base de données.
        Cursor c = db.query(DB_TABLES, columns, selection, selectionArgs, null, null, Song.order_by + " " + Song.order);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            // Id de l'élément.
            int ciId = c.getInt(0);
            // L'instance de l'élément de collection est récupéré avec la méthode get(ciId)
            // (Si l'instance n'existe pas encore, elle est créée par la méthode get)
            Song song = Song.get(ciId);

            // Ajout de l'élément de collection à la liste.
            songs.add(song);

            c.moveToNext();
        }

        // Fermeture du curseur et de la base de données.
        c.close();
        db.close();

        return songs;
    }


    /**
     * Fournit l'instance d'un élément de collection présent dans la base de données. Si l'élément
     * de collection n'est pas encore instancié, une instance est créée.
     *
     * @param ciId Id de l'élément de collection.
     *
     * @return L'instance de l'élément de collection.
     * @pre L'élément correspondant à l'id donné doit exister dans la base de données.
     */
    public static Song get(int ciId) {
        Song ci = Song.songSparseArray.get(ciId);
        if (ci != null) {
            return ci;
        }
        return new Song(ciId);
    }


    /**
     * Inverse l'ordre de tri actuel.
     *
     * @pre La valeur de CollectedItem.order est soit ASC soit DESC.
     * @post La valeur de CollectedItem.order a été modifiée et est soit ASC soit DESC.
     */
    public static void reverseOrder() {
        if (Song.order.equals("ASC")) {
            Song.order = "DESC";
        } else {
            Song.order = "ASC";
        }
    }
}
