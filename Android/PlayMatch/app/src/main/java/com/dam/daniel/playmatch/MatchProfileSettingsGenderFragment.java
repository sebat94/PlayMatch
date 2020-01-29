package com.dam.daniel.playmatch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.enums.Direction;
import com.dam.daniel.playmatch.enums.Gender;
import com.dam.daniel.playmatch.models.GenderPreference;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.GenderPreferenceService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchProfileSettingsGenderFragment extends Fragment {

    private TextView idTextViewFragmentName;
//    private ImageView idImageViewCloseFragment;

    private Button idButtonMan;
    private Button idButtonWoman;
    private Button idButtonOther;
    private Drawable imgOk;

    private Context mContext;
    private FrameLayout frameLayoutGenderPreferences;

    private User user;
    private JSONObject jsonObjectGenderPreferences;
    private boolean genderPreferencesChanged;

    private boolean genderMan;
    private boolean genderWoman;
    private boolean genderOther;

    public MatchProfileSettingsGenderFragment() {
        // Required empty public constructor
        genderMan = false;
        genderWoman = false;
        genderOther = false;
        jsonObjectGenderPreferences = new JSONObject();
        genderPreferencesChanged = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(true);
        // Get Image Resource
        imgOk = mContext.getResources().getDrawable(R.drawable.ic_done_red_24dp);
        // Get User Info
        user = LocalSorage.loadDataFromSharedPreferences(mContext).getUser();
        // Init Gender Preferences
        genderMan = false;
        genderWoman = false;
        genderOther = false;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_profile_settings_gender, container, false);

        // Toolbar
        idTextViewFragmentName = (TextView) view.findViewById(R.id.idTextViewFragmentName);
//        idImageViewCloseFragment = (ImageView) view.findViewById(R.id.idImageViewCloseFragment);

        // Gender Buttons
        idButtonMan = view.findViewById(R.id.idButtonMan);
        idButtonWoman = view.findViewById(R.id.idButtonWoman);
        idButtonOther = view.findViewById(R.id.idButtonOther);

        // Set Up Toolbar Info
        setupToolbarData();
        // Button Close Fragment
//        closeFragment(view);
        // Load User Data
        loadUserData();

        // Event Handler
        eventHandlers();

        return view;
    }

    /**
     * Fill Gender Preferences With Values Of User
     */
    private void loadUserData(){
        for (int i = 0; i < user.getGenderPreferences().size(); i++){
            if(user.getGenderPreferences().get(i).getGender() == Gender.MALE.getValue()){
                genderMan = true;
                setImgOkAndPosition(idButtonMan, 1);
            }else if(user.getGenderPreferences().get(i).getGender() == Gender.FEMALE.getValue()){
                genderWoman = true;
                setImgOkAndPosition(idButtonWoman, 1);
            }else if(user.getGenderPreferences().get(i).getGender() == Gender.OTHER.getValue()){
                genderOther = true;
                setImgOkAndPosition(idButtonOther, 1);
            }
        }
    }

    /**
     * Set Position To Drawable
     * @param button
     * @param pos
     */
    private void setImgOkAndPosition(Button button, int pos){
        if(pos == Direction.TOP.ordinal()){
            button.setCompoundDrawablesWithIntrinsicBounds(null, imgOk, null, null);
        }else if(pos == Direction.RIGHT.ordinal()){
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, imgOk, null);
        }else if(pos == Direction.BOTTOM.ordinal()){
            button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, imgOk);
        }else if(pos == Direction.LEFT.ordinal()){
            button.setCompoundDrawablesWithIntrinsicBounds(imgOk, null, null, null);
        }
    }

    /**
     * Delete Image
     * @param button
     */
    private void setImgOkToNull(Button button){
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /**
     * Event Handlers
     */
    private void eventHandlers() {
        // Man
        selectGenderPreference(idButtonMan);
        // Woman
        selectGenderPreference(idButtonWoman);
        // Other
        selectGenderPreference(idButtonOther);
    }

    /**
     * OnClick Button, add image and set these value in jsonObject to be updated
     * @param button
     */
    private void selectGenderPreference(final Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Man
                if(button.getId() == idButtonMan.getId()){
                    // If Drawable exists, then uncheck the selected gender
                    if(genderMan){
                        // Delete Gender Preference
                        deleteGenderPreferences(1);
                        // Delete Image
                        setImgOkToNull(button);
                        genderMan = false;
                    }else{
                        // Add Gender Preference
                        fillJsonObjectGenderPreferences(1);
                        postGenderPreferences();
                        // Set Image
                        setImgOkAndPosition(button, 1);
                        genderMan = true;
                    }
                // Woman
                }else if(button.getId() == idButtonWoman.getId()){
                    if(genderWoman){
                        // Delete Gender Preference
                        deleteGenderPreferences(2);
                        // Delete Image
                        setImgOkToNull(button);
                        genderWoman = false;
                    }else{
                        fillJsonObjectGenderPreferences(2);
                        postGenderPreferences();
                        // Set Image
                        setImgOkAndPosition(button, 1);
                        genderWoman = true;
                    }
                // Other
                }else if(button.getId() == idButtonOther.getId()){
                    if(genderOther){
                        // Delete Gender Preference
                        deleteGenderPreferences(3);
                        // Delete Image
                        setImgOkToNull(button);
                        genderOther = false;
                    }else{
                        fillJsonObjectGenderPreferences(3);
                        postGenderPreferences();
                        // Set Image
                        setImgOkAndPosition(button, 1);
                        genderOther = true;
                    }
                }
            }
        });
    }

    /**
     * Assign To The JsonObject The Passed Value
     *      Values Of DDBB:
     *          1) Man
     *          2) Woman
     *          3) Other
     * @param genderId
     */
    private void fillJsonObjectGenderPreferences(int genderId){
        try{
            jsonObjectGenderPreferences.put("genderPreferences", new JSONArray().put(new JSONObject().put("gender", genderId)));
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add Gender Preferences
     *
     * Add Gender Preferences to DDBB when finish. Also build manually a GenderPreference object and set it to the User object to be updated.
     * We don't take a complete User from response, because this service is running on "MainRequestUserDataFragment" and response need to be null, because there we are creating/updating a user with a stepper
     */
    private void postGenderPreferences(){
        GenderPreferenceService genderService = new GenderPreferenceService(((MatchActivity) mContext));
        genderService.addGenderPreference(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        addGenderPreferencesToUser();
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                jsonObjectGenderPreferences
        );
    }

    /**
     * Add GenderPreferences to User And Update It In LocalStorage
     */
    private void addGenderPreferencesToUser(){
        // Get The Gender Preference Selected
        JSONArray jsonArrayGP = new JSONArray();
        try{
            jsonArrayGP = jsonObjectGenderPreferences.getJSONArray("genderPreferences");
        }catch (JSONException e) {
            e.printStackTrace();
        }
        // Get Value Of Each Item Of JSONArray() But Is Only One Occurrence Because We Update GenderPreferences Each Time That One Is Pressed
        // We Maintain The For Loop Because: If there aren't any GenderPreference and get index "0" will return a NullPointerException
        for (int i = 0; i < jsonArrayGP.length(); i++){
            try{
                JSONObject jsonObjectGP = jsonArrayGP.getJSONObject(i);
                int gender = jsonObjectGP.getInt("gender");
                // Update Local User Object
                GenderPreference gp = new GenderPreference(user.getId(), gender);
                user.getGenderPreferences().add(gp);
                // Update User Of LocalStorage With Added GenderPreferences
                LocalSorage.saveDataInSharedPreferences(mContext, LocalSorage.loadDataFromSharedPreferences(mContext).getToken(), user);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // Put True When Changes Has Occurred
        genderPreferencesChanged = true;
    }

    /**
     * Delete Gender Preference
     * @param genderId
     */
    private void deleteGenderPreferences(final int genderId){
        GenderPreferenceService genderService = new GenderPreferenceService(((MatchActivity) mContext));
        genderService.deleteGenderPreference(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        removeGenderPreferencesOfUser(genderId);
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                genderId
        );
    }

    /**
     * Remove GenderPreferences of User And Update It In LocalStorage
     * @param genderId
     */
    private void removeGenderPreferencesOfUser(int genderId){
        // Get Index Of Unselected Object
        int indexOfGenderPreference = -1;
        for (int i = 0; i < user.getGenderPreferences().size(); i++){
            GenderPreference gp = user.getGenderPreferences().get(i);
            if(gp.getGender() == genderId){
                indexOfGenderPreference = i;
            }
        }
        // Remove Unselected Object From User
        if(indexOfGenderPreference != -1){
            user.getGenderPreferences().remove(indexOfGenderPreference);
            // Update User Of LocalStorage With Removed GenderPreferences
            LocalSorage.saveDataInSharedPreferences(mContext, LocalSorage.loadDataFromSharedPreferences(mContext).getToken(), user);
        }
        // Put True When Changes Has Occurred
        genderPreferencesChanged = true;
    }

    /**
     * Go Back To Parent Fragment And Send The Choosed Option (Camera/Gallery)
     */
    private void goBackToParentFragment(){
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(false);


        Intent intent = new Intent();

        if(genderPreferencesChanged) intent.putExtra("option", 1);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        // How We Can't move To "R.id...." of "MatchProfileSettingsFragment", Then We Move To The Actual Fragment, Indicating With The Flag That This Fragment Also Need To Be Closed
        //((MatchActivity) mContext).getFragmentManager().popBackStack(R.id.fragment_container_MatchProfileSettingsGender, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(MatchProfileSettingsGenderFragment.this);
        fragmentTransaction.commit();

//        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getId(), 0);  //, FragmentManager.POP_BACK_STACK_INCLUSIVE

//        ((MatchActivity) mContext).getFragmentManager().popBackStack("PROFILE_SETTINGS_FRAGMENT", FragmentManager.POP_BACK_STACK_INCLUSIVE); // "FragmentManager.POP_BACK_STACK_INCLUSIVE" pops all the fragments including the one whose id passed as argument
//        ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(MatchProfileSettingsGenderFragment.this).commitAllowingStateLoss();

        // If you're targeting sdk 24 and above you can use "commitNow()" instead of "commit()"
        //FragmentTransaction.commitNow()
        // Or If you're targeting older versions, try calling
        //FragmentManager.executePendingTransactions()
    }

    /**
     * Set Title Fragment
     */
    private void setupToolbarData(){
        // Set Fragment name
        idTextViewFragmentName.setText("Gender Settings");
    }

    /**
     * Close Fragment And go Back To Back Stack
     */
    /**
     * Close Fragment And go Back To Back Stack
     */
//    private void closeFragment(View view){
//        ImageView imageView = view.findViewById(R.id.idImageViewCloseFragment);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goBackToParentFragment();
//            }
//        });
//    }

    /**
     * This method Overlaps the fragment above the global AppBarLayout(toolbar)
     *
     * @param overlap true overlaps, false undo overlap
     */
    private void setFragmentOverlapAppBarLayout(Boolean overlap){
        ActivityUtils.setFragmentOverlapAppBarLayout(frameLayoutGenderPreferences, overlap);
    }

    /**
     * The onAttach method is used to pass the CONTEXT of Activity through fragments, we can also pass to nested fragments
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        // Get RelativeLayout that we use to overlaps this content above the global AppBarLayout
        frameLayoutGenderPreferences = ((MatchActivity) mContext).findViewById(R.id.fragment_container_MatchProfileSettingsGender);
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(true);
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     * This is called after "onDestroy()". [Have Exceptions when use onStop()]
     */
    @Override
    public void onDetach() {
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        //setFragmentOverlapAppBarLayout(false);
        // Returns To The Previous Fragment And Send The Info To Be Updated
        goBackToParentFragment();
        super.onDetach();
    }

}
