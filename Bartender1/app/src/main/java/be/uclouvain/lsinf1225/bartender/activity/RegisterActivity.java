package be.uclouvain.lsinf1225.bartender.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;

import be.uclouvain.lsinf1225.bartender.BarTenderApp;
import be.uclouvain.lsinf1225.musicplayer.R;
import be.uclouvain.lsinf1225.bartender.model.User;



import java.util.ArrayList;

/**
 * Created by maximehulet on 22/04/15.
 */
public class RegisterActivity extends Activity implements TextView.OnEditorActionListener{
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle(R.string.register_activity_title);

        EditText userEditText = (EditText) findViewById(R.id.register_username);
        userEditText.setText("");

        EditText passwordEditText = (EditText) findViewById(R.id.register_password);
        passwordEditText.setText("");

        EditText passwordBisEditText =(EditText) findViewById(R.id.register_password_bis);
        passwordBisEditText.setText("");

        typeSpinner = (Spinner) findViewById(R.id.register_typep);

        ArrayList<String> string = new ArrayList<String>();
        string.add("Client");
        string.add("Serveur");
        string.add("Patron");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, string);
        typeSpinner.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText passwordEditText = (EditText) findViewById(R.id.register_password);
        passwordEditText.setText("");

        EditText passwordBisEditText = (EditText) findViewById(R.id.register_password_bis);
        passwordBisEditText.setText("");

        EditText userEditText = (EditText) findViewById(R.id.register_username);
        userEditText.setText("");

    }


    /**
     * Vérifie que le login demandé n'est pas déjà dans la base de données et redirige l'utilisateur vers
     * la page de connexion.
     *
     * Cette méthode va rechercher dans la base de données si le login demandé existe déjà. Si non ajoute l'utilisateur
     * dans la base de données et un message le prévient et l'utilisateur est redirigé vers la page de connexion.
     * Sinon un message prévient l'utilisateur qu'il doit choisir un autre login.
     *
     *
     * @param v non utilisé mais demandé par onClick
     */
    public void putInDb(View v){
        EditText userEditText = (EditText) findViewById(R.id.register_username);
        String username = userEditText.getText().toString();

        EditText passwordEditText =(EditText) findViewById(R.id.register_password);
        String password = passwordEditText.getText().toString();

        EditText passwordBisEditText = (EditText) findViewById(R.id.register_password_bis);
        String passwordBis = passwordBisEditText.getText().toString();

        String typep = (String) typeSpinner.getSelectedItem();

        User user = User.getUser(username);

        if (!password.equals(passwordBis)){
            BarTenderApp.notifyShort(R.string.register_wrong_password);
        }
        else if (user!=null){
            BarTenderApp.notifyShort(R.string.register_wrong_username);
        }
        else {

            User.addUser(username,password,typep);


            BarTenderApp.notifyShort(R.string.register_done);
            Intent intent = new Intent (this,LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Récupère les actions faites depuis le clavier.
     *
     * Récupère les actions faites depuis le clavier lors de l'édition du champ du mot de passe afin
     * de permettre de se connecter depuis le bouton "Terminer" du clavier. (Cela évite à
     * l'utilisateur de devoir fermer le clavier et de cliquer sur le bouton se connecter).
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // L'attribut android:imeOptions="actionNext" est défini dans le fichier xml de layout
        // (activity_login.xml), L'actionId attendue est donc IME_ACTION_NEXT.
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            putInDb(v);
            return true;
        }
        return false;
    }
}

