package be.uclouvain.lsinf1225.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import be.uclouvain.lsinf1225.musicplayer.R;

/**
 * Gère l'affichage de la fonction de recherche d'éléments.
 *
 * @author Damien Mercier
 * @version 1
 */
public class SearchActivity extends Activity implements SearchView.OnQueryTextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        /**
         * Lorsqu'une requête est soumise, on ouvre l'activité d'affichage de la liste qui affichera
         * les résultats. (voir ShowListActivity.java pour la suite de la recherche).
         */
        Intent intent = new Intent(this, ShowListActivity.class);
        intent.putExtra("searchQuery", query);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
