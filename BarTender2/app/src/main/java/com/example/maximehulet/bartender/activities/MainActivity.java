package com.example.maximehulet.bartender.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.maximehulet.bartender.BarTenderApp;
import com.example.maximehulet.bartender.R;
import com.example.maximehulet.bartender.models.User;

/**
 * Created by maximehulet on 27/04/15.
 */
public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(User.getConnectedUser().getTypep().equals("Waiter")){
            setContentView(R.layout.activity_main_waiter);
        }
        else if(User.getConnectedUser().getTypep().equals("Boss")){
            setContentView(R.layout.activity_main_boss);
        }
        */
        //else if(User.getConnectedUser().getTypep().equals("Client")){
        setContentView(R.layout.activity_main);
        //}





        // Affichage du message de bienvenue.
        TextView welcomeTxt = (TextView) findViewById(R.id.welcomeTxt);
        welcomeTxt.setText(getString(R.string.main_welcome) + " " + User.getConnectedUser().getLogin());
    }


    /**
     * @note Les méthodes show, search, add et logout sont appelées lors d'un clic sur les boutons
     * correspondant grâce à l'attribut onClick présent dans les fichiers de layout.
     *
     * Lire http://developer.android.com/reference/android/R.attr.html#onClick
     */

    /**
     * Lance l'activité d'affichage de la liste des éléments.
     */
    public void showMenu(View v) {
        Intent intent = new Intent(this, ShowMenuActivity.class);
        startActivity(intent);
    }

    /**
     * Lance l'activité de recherche d'un élément.
     */
    public void search(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /**
     * Déconnecte l'utilisateur actuellement connecté et retourne vers l'écran de connexion.
     */
    public void logout(View v) {
        User.logout();
        finish();
    }

    /**
     * Lance l'activité d'ajout d'un élément.
     */
    public void playlist(View v) {
        Intent intent = new Intent(this, PlayListActivity.class);
        startActivity(intent);
    }

    /**
     * Désactive le bouton de retour. Désactive le retour à l'activité précédente (donc l'écran de
     * connexion dans ce cas-ci) et affiche un message indiquant qu'il faut se déconnecter.
     */
    @Override
    public void onBackPressed() {
        // On désactive le retour (car on se trouve au menu principal) en ne faisant
        // rien dans cette méthode si ce n'est afficher un message à l'utilisateur.
        BarTenderApp.notifyShort(R.string.main_back_button_disable);
    }
}
