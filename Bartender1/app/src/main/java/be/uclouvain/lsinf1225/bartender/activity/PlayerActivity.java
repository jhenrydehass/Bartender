package be.uclouvain.lsinf1225.bartender.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import be.uclouvain.lsinf1225.bartender.MusicPlayer;
import be.uclouvain.lsinf1225.bartender.R;
import be.uclouvain.lsinf1225.bartender.model.Song;

/**
 * Gère l'affichage de la liste de lecture, des boutons précédent, suivant, play, pause et aléatoire
 * ainsi que de la durée de lecture (temps courant et durée totale du morceau en cours).
 *
 * @author Damien Mercier
 * @version 1
 */
public class PlayerActivity extends Activity implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {

    ImageButton prevBtn;
    ImageButton shuffleBtn;
    SeekBar seekbar;
    private Song currentSong;
    private Handler myHandler = new Handler();
    private TextView totalTimeTextView;
    private TextView currentTimeTextView;
    private ListView playlistListView;
    private ArrayAdapter<Song> playlistAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        prevBtn = (ImageButton) findViewById(R.id.player_prev);
        shuffleBtn = (ImageButton) findViewById(R.id.player_shuffle);

        seekbar = (SeekBar) findViewById(R.id.player_seek_bar);
        currentTimeTextView = (TextView) findViewById(R.id.player_current_time);
        totalTimeTextView = (TextView) findViewById(R.id.player_total_time);

        // Affichage de la liste de lecture.

	/*
	 * TODO : 
	 * - Afficher la liste des musiques dans la ListView (id : player_playlist) grâce à un ArrayAdapter<Song>.
	 * La liste des musique peut être obtenue grace à MusicPlayer.getSongs(). 
	 * Nous utiliserons le fichier de layout défini par android : android.R.layout.simple_list_item_single_choice
	 * De plus, il faut définir le "choice mode" sur ListView.CHOICE_MODE_SINGLE
	 *
	 * - Ensuite, faire en sorte que lorsqu'on clic sur un morceau, la lecture de celui-ci commence. (indice : OnItemClickListener, voir ShowListActivity)
	 */ 

        // La modification de la barre de progression devra être notifié à cette classe.
        seekbar.setOnSeekBarChangeListener(this);

        // Début de la mise à jour des informations.
        UpdateInfo.run();
    }


    /**
     * Permet la mise à jour de l'affichage des informations à propos du morceau en lecture. Les
     * informations mises à jour sont : - La position (affiché sous forme de texte et de barre de
     * progression) - Le temps total - La chanson joué (marquée comme "checked" dans la liste)
     */
    private Runnable UpdateInfo = new Runnable() {
        public void run() {

            // Lorsque la chanson a changée.
            if (currentSong != MusicPlayer.getCurrentSong()) {
                currentSong = MusicPlayer.getCurrentSong();
                int finalTime = MusicPlayer.getDuration();
                seekbar.setMax(finalTime);

                long totalTimeMin = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
                long totalTimeSec = TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(totalTimeMin);
                totalTimeTextView.setText(String.format("%02d:%02d", totalTimeMin, totalTimeSec));
                playlistListView.setItemChecked(playlistAdapter.getPosition(currentSong), true);
            }

            // Dans tout les cas.
            int currentTime = MusicPlayer.getCurrentPosition();

            long currentTimeMin = TimeUnit.MILLISECONDS.toMinutes((long) currentTime);
            long currentTimeSec = TimeUnit.MILLISECONDS.toSeconds((long) currentTime) - TimeUnit.MINUTES.toSeconds(currentTimeMin);
            currentTimeTextView.setText(String.format("%02d:%02d", currentTimeMin, currentTimeSec));

            seekbar.setProgress(currentTime);

            // La mise à jour suivante est programmée pour dans 100 milisecondes.
            myHandler.postDelayed(this, 100);
        }
    };

    /**
     * Passe à la musique précédente.
     */
    public void prev(View view) {
        MusicPlayer.prev();
    }

    /**
     * Joue le morceau courant.
     */
    public void play(View view) {
        MusicPlayer.play();
    }

    /**
     * Met la lecture en pause.
     */
    public void pause(View view) {
        MusicPlayer.pause();
    }

    /**
     * Inverse l'état du mode aléatoire.
     */
    public void shuffle(View view) {
        boolean shuffleMode = !MusicPlayer.getShuffleMode();
        MusicPlayer.setShuffleMode(shuffleMode);
        updateShuffleButton();
    }

    /**
     * Met à jour l'icone du bouton "aléatoire" afin de correspondre à l'état du MusicPlayer.
     * Le bouton pause est également désactivé si le mode est aléatoire.
     */
    public void updateShuffleButton() {
        if (MusicPlayer.getShuffleMode()) {
            shuffleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle_on));
            prevBtn.setEnabled(false);
        } else {
            shuffleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_shuffle));
            prevBtn.setEnabled(true);
        }
    }

    /**
     * Passe au morceau suivant.
     */
    public void next(View view) {
        MusicPlayer.next();
    }


    /**
     * Lorsque l'avancement de la barre de progression est changée. Nécessaire pour implémenter
     * OnSeekBarChangeListener. N'est pas utilisé ici,
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    /**
     * Lorsque l'utilisateur commence à modifier la barre de progression. Nécessaire pour
     * implémenter OnSeekBarChangeListener. N'est pas utilisé ici.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Lorsque l'utilisateur a fini de modifier l'avancement de la barre de progression, dans ce
     * cas, on transmet le nouveau temps au MusicPlayer afin que la lecture soit continué depuis le
     * nouveau temps.
     *
     * @see MusicPlayer#seekTo(int)
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MusicPlayer.seekTo(seekBar.getProgress());
    }

    /**
     * Lors du clic sur un élément de la liste de lecture, Le morceau correspondant doit être joué.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicPlayer.play(position);
    }

    /**
     * Lors du retour au menu principal, il faut arrêter la mise à jour de l'affichage.
     */
    @Override
    public void onBackPressed() {
        myHandler.removeCallbacks(UpdateInfo);
        finish();
    }
}

