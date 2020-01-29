package com.dam.daniel.playmatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.dam.daniel.playmatch.adapters.ViewPagerAdapter;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity implements MatchCardUserDetailsFragment.OnFragmentInteractionListener {

    // Adapter ViewPager
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPagerUtils pager;

    // Buttons
    private Button buttonProfile;
    private Button buttonCard;
    private Button buttonChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Init
        buttonProfile = (Button) findViewById(R.id.buttonProfile);
        buttonCard = (Button) findViewById(R.id.buttonCard);
        buttonChat = (Button) findViewById(R.id.buttonChat);

        // Setup ViewPager
        setupPager();
        // Setup Buttons
        setupButton();
    }

    /**
     * Init ViewPager
     */
    private void setupPager(){
        // Load Adapter to the ViewPage
        List<Fragment> fragments = getFragments();
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPagerUtils)findViewById(R.id.pager);                                           // Custom ViewPager
        //ViewPager pager = (ViewPager)findViewById(R.id.pager);                                    // OLD ViewPager

        // This Makes Retains Actual Data In The Specified Number Of Fragments While We Slides Between Them
        // Definition: Set the number of pages that should be retained to either side of the current page in the view hierarchy in an idle state.
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(viewPagerAdapter);

        // Listen When The Fragment Switch to Another Fragment
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                // Set the Active Image For this Fragment
                setActiveImageStyle(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        // My viewPager is loaded in the "onCreate()", but i need let him load all Fragments that I pass to it. When it load all, We can set the Current Position of FragmentList
        // Set the Default Fragment to show. Parameter is the index of the List of Fragments.
        pager.setCurrentItem(1, true);
    }

    /**
     * Manage onClick event of Buttons and set them the new image, with a color of Active one
     *
     * Store in a Local Variables the View of Each Button, because we need it for change the image when Click in other Button
     */
    private void setupButton(){
        // Button Profile
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Image Menu Active
                setActiveImageStyle(0);
                // Move To The Current Page
                pager.setCurrentItem(0);
            }
        });
        // Button Card
        buttonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveImageStyle(1);
                pager.setCurrentItem(1);
            }
        });
        // Button Chat
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveImageStyle(2);
                pager.setCurrentItem(2);
            }
        });
    }

    /**
     * Set The Image Active to the Actual Fragment loaded in ViewPager
     * @param i
     */
    private void setActiveImageStyle(int i){
        switch (i) {
            case 0: // PROFILE
                // Set The Rest Of Buttons With The Default Image(no active)
                try{
                    buttonCard.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_whatshot_grey_24dp));
                    buttonChat.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_textsms_grey_24dp));
                }catch (Exception e){
                    e.printStackTrace();
                }
                // Change to the Active image
                buttonProfile.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_account_circle_red_24dp));
                break;
            case 1: // CARD
                try{
                    buttonProfile.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_account_circle_grey_24dp));
                    buttonChat.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_textsms_grey_24dp));
                }catch (Exception e){
                    e.printStackTrace();
                }
                buttonCard.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_whatshot_red_24dp));
                break;
            case 2: // CHAT
                try{
                    buttonCard.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_whatshot_grey_24dp));
                    buttonProfile.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_account_circle_grey_24dp));
                }catch (Exception e){
                    e.printStackTrace();
                }
                buttonChat.setBackground(ContextCompat.getDrawable(MatchActivity.this, R.drawable.ic_textsms_red_24dp));
                break;
        }
    }

    /**
     * List Of Fragments To Show Inside The ViewPager
     * @return
     */
    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<>();
        fList.add(new MatchProfileFragment());
        fList.add(new MatchCardFragment());
        fList.add(new MatchChatFragment());
        return fList;
    }

//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();
//
//        FragmentManager fragmentManager = this.getSupportFragmentManager();
//        if(fragmentManager.getBackStackEntryCount()-1 >= 1){
//            System.out.println("------------------> " + fragmentManager.getBackStackEntryCount());
//            fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getId(), 0);  //, FragmentManager.POP_BACK_STACK_INCLUSIVE
//        }
//    }

    /**
     * Get Shot Of Camera in "onActivityResult" That is only called in the Activity, and then pass the 'data' to the next fragments calling super
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * The interface "implements MatchCardUserDetailsFragment.OnFragmentInteractionListener" forces us to implement method "onFragmentInteraction"
     * We need it to comunnicate the ACTIVITY with the FRAGMENTS, and allow communicate between FRAGMENTS allowed in an ACTIVITY.
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri){
    }

}