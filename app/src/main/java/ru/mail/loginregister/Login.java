package ru.mail.loginregister;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends ActionBarActivity implements View.OnClickListener{

    Button bLogin;
    EditText etUsername,etPassword;
    TextView tvRegisterLink;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userLocalStore=new UserLocalStore(this);

        etUsername=(EditText)findViewById(R.id.etKullanici_adi);
        etPassword=(EditText)findViewById(R.id.etParola1);
        bLogin=(Button)findViewById(R.id.bLogin);
        tvRegisterLink=(TextView)findViewById(R.id.tvRegisterLink);

        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){//this is a if statement like if(v.getId()==R.id.bLogin)
            case R.id.bLogin:
                String username=etUsername.getText().toString().trim();
                String password=etPassword.getText().toString().trim();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(this,"Tüm alanların doldurulması gereklidir", Toast.LENGTH_LONG).show();
                }else {
                    User user=new User(username,password);
                    authenticate(user);
                }
                break;
            case R.id.tvRegisterLink:
                startActivity(new Intent(this,Register.class));
                break;
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequests=new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if(returnedUser==null){
                    showErrorMessage();
                }else{
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage(){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(Login.this);
        dialogBuilder.setMessage("Yanlış kullanıcı bilgilerini");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        if(returnedUser.getId()==1){
            userLocalStore.storeUserData(returnedUser);
            userLocalStore.setUserLoggedIn(true);
            startActivity(new Intent(this,MainActivity.class));
        }else{
            userLocalStore.storeUserData(returnedUser);
            userLocalStore.setUserLoggedIn(true);
            startActivity(new Intent(this,MainForCompany.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
