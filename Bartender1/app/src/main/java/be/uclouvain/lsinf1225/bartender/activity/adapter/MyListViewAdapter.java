package be.uclouvain.lsinf1225.bartender.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import be.uclouvain.lsinf1225.musicplayer.R;
import be.uclouvain.lsinf1225.bartender.model.Song;

/**
 * Gère l'affichage personnalisé de notre liste.
 *
 * Cette classe permet de créer un Adapter personnalisé pour notre liste d'éléments de collection.
 * De cette manière il nous est possible d'utiliser un layout particulier (ici
 * collected_item_row.xml) pour chaque ligne reprenant le nom de l'élément et sa note (rating).
 *
 * @author Damien Mercier
 * @version 1
 * @see <a href="http://d.android.com/reference/android/widget/Adapter.html">Adapter</a>
 * @see <a href="http://d.android.com/reference/android/widget/BaseAdapter.html">BaseAdapter</a>
 */
public class MyListViewAdapter extends BaseAdapter {
    /**
     * Permet d'instancier un fichier xml de layout dans une vue.
     */
    private final LayoutInflater mInflater;

    /**
     * Liste des éléments de collection à mettre dans la liste.
     */
    private ArrayList<Song> songs;

    /**
     * Constructeur.
     *
     * @param context        Contexte de l'application.
     * @param songs Liste des éléments de collection à placer dans la liste.
     */
    public MyListViewAdapter(Context context, ArrayList<Song> songs) {
        mInflater = LayoutInflater.from(context);
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).getId();
    }

    /**
     * Remplit chaque ligne de la liste avec un layout particulier.
     *
     * Cette méthode est appelée par Android pour construire la vue de la liste (lors de la
     * construction de listview).
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Si la vue n'a pas encore été créé (typiquement lors du première affichage de la liste).
        // Android recycle en effet les layout déjà chargés des éléments de la liste (par exemple
        // lors du changement de l'ordre dans la liste.)

        if (convertView == null) {
            // Création d'un nouvelle vue avec le layout correspondant au fichier xml
            convertView = mInflater.inflate(R.layout.collected_item_row, parent, false);
        }

        // Récupération des deux éléments de notre vue dans le but d'y placer les données.
        TextView nameTextView = (TextView) convertView.findViewById(R.id.show_row_name);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.show_row_rating);

        // Récupération et placement des données.
        Song song = songs.get(position);
        nameTextView.setText(song.getTitle());
        //ratingBar.setRating(song.getRating());

        return convertView;
    }

    /**
     * Change la liste des éléments de collection affichée.
     *
     * Permet de changer complètement la liste des éléments affichés dans la liste.
     *
     * @param newSongs La nouvelle liste des éléments de collection à afficher.
     *
     * @post Les éléments de la liste ont été remplacés par les éléments passés en argument.
     */
    public void setSongs(ArrayList<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }
}
