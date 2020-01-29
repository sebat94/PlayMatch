package com.dam.daniel.playmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.AuthService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

/**
 * Implements interface(xxxxx.OnFragmentInteractionListener) of each Fragment that we need to communicate with this Activity.
 * Implements interface OTPReceiveListener Of SMS Retriever
 */
public class MainActivity extends AppCompatActivity {   //implements CountryCode.OnFragmentInteractionListener

    Button idButtonLoginTelephone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check If User Has Already Been Logged
        if(LocalSorage.loadDataFromSharedPreferences(this).getUser() != null &&
                LocalSorage.loadDataFromSharedPreferences(this).getUser().isActive() &&
                LocalSorage.loadDataFromSharedPreferences(this).getUser().getDisabled().equals("null"))
            isUserLogged(LocalSorage.loadDataFromSharedPreferences(this).getUser());

        ////////////////////////////////
        // TODO: Últimas Pruebas - EL LocalStorage Falla, el Token o El Usuario No se Machacan como debería
        // TODO: AL mismo tiempo ver si conseguimos pegar el número de teléfono del usuario automáticamente una vez recogido en el EditText

//        DANI
//        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NzcsInRlbGVwaG9uZU51bWJlciI6IiszNDY4MDczMTExNyIsImlhdCI6MTU1MjQwOTY0NX0.j_fc7GwPNp7AGg-4IUHNeegeYr9txmtflnGb9zOFsBc";
//
//        User validUser = new User();
//        validUser.setId(77);
//        validUser.setTelephoneNumber("+34680731117");
//        validUser.setNick("Danieloru94");
//        validUser.setBirthdate("1994-08-16");
//        validUser.setGender(1);
//        validUser.setEmail("danieloru94@gmail.com");
//        validUser.setLat(0.0);
//        validUser.setLng(0.0);
//        validUser.setActive(true);
//        validUser.setDisabled("null");
//        System.out.println(validUser.toString());
//        LocalSorage.saveDataInSharedPreferences(this, validToken, validUser);
//
//        User user = LocalSorage.loadDataFromSharedPreferences(this).getUser();
//        if(user != null && user.isActive() && user.getDisabled().equals("null")) isUserLogged(user); // TODO: Mirar porque los Null llegan como string

//        Otro
//        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NzgsInRlbGVwaG9uZU51bWJlciI6IiszNDYxOTgwMjA1MSIsImlhdCI6MTU1MjY5MDQxM30.oi6A3mRjhTXXSVuKEc0G9YlxrN0PqTD8rYscFCpLIaA";
//
//        User validUser = new User();
//        validUser.setId(78);
//        validUser.setTelephoneNumber("+34619802051");
//        validUser.setNick("Pepi");
//        validUser.setBirthdate("1966-12-31T23:00:00.000Z");
//        validUser.setGender(2);
//        validUser.setEmail("pepi.ruano@hotmail.com");
//        validUser.setLat(0.0);
//        validUser.setLng(0.0);
//        validUser.setActive(true);
//        validUser.setDisabled("null");
//        System.out.println(validUser.toString());
//        LocalSorage.saveDataInSharedPreferences(this, validToken, validUser);
//
//        User user = LocalSorage.loadDataFromSharedPreferences(this).getUser();
//        if(user != null && user.isActive() && user.isDisabled().equals("null")) isUserLogged(user); // TODO: Mirar porque los Null llegan como string

        // TODO: NO USAR A MENOS QUE NECESITEMOS GENERAR TOKEN PORQUE ES DE PAGO
//        LocalSorage.deleteDataFromSharedPreferences(this);

        // Init
        idButtonLoginTelephone = (Button) findViewById(R.id.idButtonLoginTelephone);

        // Event Handler
        eventHandlers();
    }

    /**
     * API CALL - Check if user is logged yet
     */
    private void isUserLogged(User user){
        AuthService authService = new AuthService(this);
        authService.isUserLogged(
            new VolleySupport.ServerCallbackEmpty() {
                @Override
                public void onSuccess() {
                    // Go Into App
                    try{
                        ActivityUtils.setRoot(MainActivity.this, MatchActivity.class);
                    }catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            },
            new VolleySupport.ServerCalbackError() {
                @Override
                public void onError(String errorMessage) {
                    ToastUtils.showToastError(MainActivity.this, errorMessage);
                }
            },
            user
        );
    }

    /**
     * Event Handlers
     */
    private void eventHandlers() {
        // OnClick on EditText of Country and Iso Codes
        idButtonLoginTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainLoginFragment mainLoginFragment = new MainLoginFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container_main_login, mainLoginFragment);   // ( idFrameLayout, fragmentToShow, "tag" )
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    // PARA EL BUTTON LOGIN
//    if(!userLogged.isActive()){
//        MainRequestUserDataFragment requestUserDataFragment = new MainRequestUserDataFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container_ActivityMain, requestUserDataFragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
//    }else{
//        // Go Into App
//        Intent intent = new Intent(MainActivity.this, MatchActivity.class);
//        getApplicationContext().startActivity(intent);
//    }

    /**
     * The interface "implements CountryCode.OnFragmentInteractionListener" forces us to implement method "onFragmentInteraction"
     * We need it to comunnicate the ACTIVITY with the FRAGMENTS, and allow communicate between FRAGMENTS allowed in an ACTIVITY.
     *
     * @param uri
     */
//    @Override
//    public void onFragmentInteraction(Uri uri){
//    }

}
