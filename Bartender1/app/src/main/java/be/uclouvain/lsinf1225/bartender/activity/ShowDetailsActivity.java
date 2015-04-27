package be.uclouvain.lsinf1225.bartender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import be.uclouvain.lsinf1225.bartender.BarTenderApp;
import be.uclouvain.lsinf1225.bartender.MusicPlayer;
import be.uclouvain.lsinf1225.bartender.R;
import be.uclouvain.lsinf1225.bartender.model.Song;

/**
 * Gère l'affichage des détails d'un élément ainsi que la modification de la note de celui-ci.
 *
 * @author Damien Mercier
 * @version 1
 */
public class ShowDetailsActivity extends Activity implements RatingBar.OnRatingBarChangeListener {

    private Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        // Récupération de l'id du morceau de musique ou si rien n'est trouvé, -1 est la valeur
        // par défaut.
        // Lire http://d.android.com/training/basics/firstapp/starting-activity.html#ReceiveIntent
        int id = getIntent().getIntExtra("s_id", -1);

        if (id == -1) {
            // Ne devrait jamais arriver.
            throw new RuntimeException("Aucun id de morceau n'a été spécifié.");
        }

        // Récupération du morceau de musique
        currentSong = Song.get(id);

        // Complétition des différents champs avec les donnée.
        TextView title = (TextView) findViewById(R.id.show_details_title);
        title.setText(currentSong.getTitle());

        TextView artist = (TextView) findViewById(R.id.show_details_artist);
        artist.setText(currentSong.getArtist());


        TextView filename = (TextView) findViewById(R.id.show_details_filename);
        filename.setText(currentSong.getFilename());

        RatingBar rating = (RatingBar) findViewById(R.id.show_details_rating);
        //rating.setRating(currentSong.getRating());

        // Indique que cette classe recevra les modifications de note (rating) grâce à la méthode
        // onRatingChanged.
        rating.setOnRatingBarChangeListener(this);

    }

    /**
     * Enregistre les changements de la note (rating).
     *
     * @param ratingBar La RatingBar concernée (ici il n'y en a qu'une dont l'id est
     *                  show_details_rating).
     * @param rating    La valeur de la nouvelle note (rating).
     * @param fromUser  Indique si le changement de note (rating) est effectué par l'utilisateur ou
     *                  par le programme (par exemple par appel de la méthode
     *                  ratingBar.setRating(x)).
     */
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (fromUser) {
            if (!currentSong.setRating(rating)) {
                // En cas d'erreur, il faut notifier l'utilisateur et afficher la valeur qui est
                // réellement enregistrée.
                BarTenderApp.notifyShort(R.string.show_details_rating_change_error);
                //ratingBar.setRating(currentSong.getRating());
            }
        }
    }

    public void play(View view) {
        MusicPlayer.play(currentSong);
    }
}
