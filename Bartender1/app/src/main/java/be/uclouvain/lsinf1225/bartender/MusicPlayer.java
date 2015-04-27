package be.uclouvain.lsinf1225.bartender;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import be.uclouvain.lsinf1225.bartender.model.Song;

/**
 * Classe utilitaire permettant de gérer la lecture des fichiers musiques facilement.
 *
 * @author Damien Mercier
 * @version 1
 * @see <a href="http://d.android.com/guide/topics/media/mediaplayer.html">Media Playback</a>
 */
public class MusicPlayer {

    /**
     * Liste des morceaux de musique (Song) à jouer.
     */
    private static ArrayList<Song> songs = new ArrayList<Song>();

    /**
     * Position actuelle dans la liste de lecture.
     */
    private static int index = -1;

    /**
     * Instance du lecteur média utilisé.
     */
    private static MediaPlayer currentMediaPlayer = null;

    /**
     * Mode de lecture aléatoire. Vrai (true) si activé.
     */
    private static boolean shuffleMode = false;


    /**
     * Indique si un morceau est actuellement joué.
     *
     * @return Vrai (true) si un morceau est actuellement joué, false sinon.
     */
    public static boolean isPlaying() {
        return currentMediaPlayer != null && currentMediaPlayer.isPlaying();
    }

    /**
     * Fourni le morceau de musique actuellement joué / en pause.
     *
     * @return Morceau de musique.
     */
    public static Song getCurrentSong() {
        if (songs == null || index < 0 || index >= songs.size()) {
            return null;
        }
        return songs.get(index);
    }

    /**
     * Joue le morceau courant.
     */
    public static void play() {
        if (currentMediaPlayer == null) {
            play(index);
        } else {
            currentMediaPlayer.start();
        }
    }


    /**
     * Met le média courant en pause.
     */
    public static void pause() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.pause();
        }
    }

    /**
     * Arrête le media courant et ré-initialise sa position de lecture à 0s.
     */
    public static void stop() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.seekTo(0);
            currentMediaPlayer.pause();
        }
    }

    /**
     * Fournit la durée du morceau courant.
     *
     * @return Durée en milisecondes (0 si aucun morceau n'est initialisé).
     */
    public static int getDuration() {
        if (currentMediaPlayer == null) {
            return 0;
        }
        return currentMediaPlayer.getDuration();
    }


    /**
     * Fournit la position de lecture du morceau courant.
     *
     * @return Durée en milisecondes.
     */
    public static int getCurrentPosition() {
        if (currentMediaPlayer == null) {
            return 0;
        }
        return currentMediaPlayer.getCurrentPosition();
    }

    /**
     * Place la lecture du média courant à la position demandé.
     *
     * @param msec Position en milisecondes.
     */
    public static void seekTo(int msec) {
        if (currentMediaPlayer == null) {
            return;
        }
        currentMediaPlayer.seekTo(msec);

    }

    /**
     * Joue le morceau suivant. Si le mode de lecture aléatoire est activé choisit un morceau
     * aléatoirement dans la liste. Si le dernier morceau de la liste de lecture est atteint. met la
     * lecture sur stop.
     */
    public static void next() {
        if (shuffleMode) {
            playShuffle();
        } else if (isLast()) {
            stop();
        } else {
            index++;
            play(index);
        }
    }

    /**
     * Joue le morceau précédent. Si le mode de lecture aléatoire est actif, ou que le morceau
     * actuel est le premier de la liste de lecture, la lecture est mise sur "stop".
     */

    public static void prev() {
        if (isFirst() || shuffleMode) {
            stop();
        } else {
            index--;
            play(index);
        }
    }

    /**
     * Joue une musique aléatoirement.
     *
     * @note Une amélioration possible pour cette méthode est de prendre en compte l'historique (ou
     * du moins la musique actuellement joué) afin de ne pas répéter la même musique trop souvent.
     */
    public static void playShuffle() {
        Random generator = new Random();
        index = generator.nextInt((songs.size()));
        play(index);
    }


    /**
     * Indique si le morceau en cours est le premier de la liste de lecture.
     *
     * @return Vrai (true) si le morceau est le premier de la liste de lecture, faux (false) sinon.
     */
    public static boolean isFirst() {
        return index == 0;
    }

    /**
     * Indique si le morceau en cours est le dernier de la liste de lecture.
     *
     * @return Vrai (true) si le morceau est le dernier de la liste de lecture, faux (false) sinon.
     */
    public static boolean isLast() {
        return index == songs.size() - 1;
    }


    /**
     * Commence la lecture du morceau placé en position "index" dans la liste de lecture.
     *
     * @param index Position du morceau à jouer dans la liste de lecture.
     *
     * @pre index doit être compris entre 0 et songs.size()-1
     * @post La lecture du morceau à la position "index" est lancée.
     */
    public static void play(int index) {
        // Vérification de la pré-condition
        if (index < 0 || index >= songs.size()) {
            // Si la pré-condition n'est pas remplie, rien ne se passe.
            return;
        }

        Song song = songs.get(index);

        play(song);
    }

    /**
     * Commence la lecture du morceau passé en argument.
     *
     * @param song Morceau à jouer.
     *
     * @pre Un morceau valide (différent de null) est donné.
     * @post La lecture du morceau passé en argument est lancée.
     */
    public static void play(Song song) {
        if (song == null) {
            return;
        }
        if (songs != null) {
            MusicPlayer.index = songs.indexOf(song);
        }

        if (currentMediaPlayer != null) {
            // Si un morceau est en cours de lecture, il faut libérer le media player.
            currentMediaPlayer.release();
        }

        // Création d'un nouveau MediaPlayer
        currentMediaPlayer = new MediaPlayer();

        // Type de media = musique (les autres types possibles sont STREAM_NOTIFICATION, STREAM_ALARM,...)
        currentMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // L'action a faire à la fin de la lecture est de passer au morceau suivant (méthode next)
        currentMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MusicPlayer.next();
            }
        });


        // Chargement du fichier (si pas d'erreur d'IO)
        try {
            AssetFileDescriptor descriptor = BarTenderApp.getContext().getAssets().openFd(song.getFilename());
            currentMediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            currentMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentMediaPlayer.start();
    }

    /**
     * (Dés)active le mode de lecture aléatoire.
     *
     * @param shuffleMode Vrai (true) si le mode de lecture aléatoire doit être activé, faux (false)
     *                    sinon.
     */
    public static void setShuffleMode(boolean shuffleMode) {
        MusicPlayer.shuffleMode = shuffleMode;
    }

    /**
     * Indique si le mode de lecture aléatoire est activé ou non.
     *
     * @return Vrai (true) si le mode de lecture aléatoire est activé, faux (false) sinon.
     */
    public static boolean getShuffleMode() {
        return shuffleMode;
    }

    /**
     * Indique la liste des morceaux à placer dans la liste de lecture.
     *
     * Cette opération remplace l'ancienne liste de lecture et arrête la lecture en cours (s'il y en
     * a une).
     *
     * @param songs Liste des morceaux de musique à placer dans la liste de lecture.
     *
     * @post La liste de lecture est celle passée en argument (songs). Aucune musique n'est en cours
     * de lecture.
     */
    public static void setSongs(ArrayList<Song> songs) {
        MusicPlayer.songs = songs;
    }

    /**
     * Fourni la liste de lecture actuelle.
     *
     * @return Liste de morceaux de musique.
     */
    public static ArrayList<Song> getSongs() {
        return songs;
    }
}
