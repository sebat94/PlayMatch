package com.dam.daniel.playmatch;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.ImageService;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.AnimationUtils;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.ImageUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MatchProfileEditFragment extends Fragment {

    private static final int FRAGMENT_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_IMAGE_REQUEST_GALLERY = 2;
    private static final int MAX_USER_IMAGES = 9;
    // Variable That Indicates If Is Camera or Gallery Option, by default is Camera
    int imagePicker = 1;

    // Views
    private ViewPagerUtils pager;

//    private ImageView idImageViewCloseFragment;
    private TextView idTextViewFragmentName;

    private List<ImageView> listImageView;
    private List<FloatingActionButton> listFloatingActionButton;
    private EditText idEditTextAbout;
    private EditText idEditTextJob;
    private EditText idEditTextCompany;
    private EditText idEditTextSchool;

    private JSONObject jsonObjectUpdateUser;

    // User
    private User user;
    private List<Image> images;

    private Context mContext;
    private RelativeLayout relativeLayoutContentViewPager;

    public MatchProfileEditFragment() {
        // Required empty public constructor
        listImageView = new ArrayList<>();
        listFloatingActionButton = new ArrayList<>();
        List<Image> images = new ArrayList<>();
        jsonObjectUpdateUser = new JSONObject();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewPager
        pager = (ViewPagerUtils) ((MatchActivity) mContext).findViewById(R.id.pager);

        // Disable Swipe
        disableSwipeViewPager(true);
        // Get User Info
        user = LocalSorage.loadDataFromSharedPreferences(((MatchActivity) mContext)).getUser();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_profile_edit, container, false);

        // Views
        // Toolbar
//        idImageViewCloseFragment = (ImageView) view.findViewById(R.id.idImageViewCloseFragment);
        idTextViewFragmentName = (TextView) view.findViewById(R.id.idTextViewFragmentName);
        // ImageView
        ImageView idImageView1 = (ImageView) view.findViewById(R.id.idImageView1);
        ImageView idImageView2 = (ImageView) view.findViewById(R.id.idImageView2);
        ImageView idImageView3 = (ImageView) view.findViewById(R.id.idImageView3);
        ImageView idImageView4 = (ImageView) view.findViewById(R.id.idImageView4);
        ImageView idImageView5 = (ImageView) view.findViewById(R.id.idImageView5);
        ImageView idImageView6 = (ImageView) view.findViewById(R.id.idImageView6);
        ImageView idImageView7 = (ImageView) view.findViewById(R.id.idImageView7);
        ImageView idImageView8 = (ImageView) view.findViewById(R.id.idImageView8);
        ImageView idImageView9 = (ImageView) view.findViewById(R.id.idImageView9);
        listImageView.addAll(Arrays.asList(idImageView1, idImageView2, idImageView3, idImageView4, idImageView5, idImageView6, idImageView7, idImageView8, idImageView9));
        // FloatingActionButton
        FloatingActionButton idFloatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton1);
        FloatingActionButton idFloatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton2);
        FloatingActionButton idFloatingActionButton3 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton3);
        FloatingActionButton idFloatingActionButton4 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton4);
        FloatingActionButton idFloatingActionButton5 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton5);
        FloatingActionButton idFloatingActionButton6 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton6);
        FloatingActionButton idFloatingActionButton7 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton7);
        FloatingActionButton idFloatingActionButton8 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton8);
        FloatingActionButton idFloatingActionButton9 = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButton9);
        listFloatingActionButton.addAll(Arrays.asList(idFloatingActionButton1, idFloatingActionButton2, idFloatingActionButton3, idFloatingActionButton4, idFloatingActionButton5, idFloatingActionButton6, idFloatingActionButton7, idFloatingActionButton8, idFloatingActionButton9));
        // Info User
        idEditTextAbout = (EditText) view.findViewById(R.id.idEditTextAbout);
        idEditTextJob = (EditText) view.findViewById(R.id.idEditTextJob);
        idEditTextCompany = (EditText) view.findViewById(R.id.idEditTextCompany);
        idEditTextSchool = (EditText) view.findViewById(R.id.idEditTextSchool);

        // Load Fragment Name
        setupToolbarData();
        // Button Close Fragment
//        closeFragment(view);

        // Load Images
        if(user.getImages() != null){
            images = user.getImages();
            printImagesInEmptyHoles(images);
        }
        // Load User Data
        loadUserData();

        // Events Handler
        eventHandlers();

        return view;
    }

    /**
     * Fill Fields With Values Of User And Default Configuration Of SeekBars
     */
    private void loadUserData(){
        idEditTextAbout.setText(user.getDescription());
        idEditTextJob.setText(user.getJob());
        idEditTextCompany.setText(user.getCompany());
        idEditTextSchool.setText(user.getSchool());
    }

    /**
     * Generate OnClick Event For Each Element Inside The Lists
     *
     * Event Handlers
     */
    private void eventHandlers() {
        for (int i = 0; i < MAX_USER_IMAGES; i++){
            // ImageView
            generateEventClickForEachImage(listImageView.get(i));
            // FloatingActionButton
            generateEventClickForEachFab(listFloatingActionButton.get(i));
        }
        // About Me
        detectTextChangeAndUpdate(idEditTextAbout);
        // Job
        detectTextChangeAndUpdate(idEditTextJob);
        // Company
        detectTextChangeAndUpdate(idEditTextCompany);
        // School
        detectTextChangeAndUpdate(idEditTextSchool);

    }

    /**
     * Get the text entered into a EditText after 500 milliseconds
     * @param editText
     */
    private void detectTextChangeAndUpdate(final EditText editText){
        RxTextView.textChangeEvents(editText)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TextViewTextChangeEvent>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                        // Get the written result here when 500 milliseconds pass
                        String text = onTextChangeEvent.text().toString();
                        int editTextId = editText.getId();
                        if(editTextId == idEditTextAbout.getId()){
                            try {
                                jsonObjectUpdateUser.put("user", new JSONObject().put("description", text));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(editTextId == idEditTextJob.getId()){
                            try {
                                jsonObjectUpdateUser.put("user", new JSONObject().put("job", text));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(editTextId == idEditTextCompany.getId()){
                            try {
                                jsonObjectUpdateUser.put("user", new JSONObject().put("company", text));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(editTextId == idEditTextSchool.getId()){
                            try {
                                jsonObjectUpdateUser.put("user", new JSONObject().put("school", text));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        updateUser(jsonObjectUpdateUser);
                    }
                });
    }

    /**
     * Update User
     * @param jsonObject
     */
    private void updateUser(JSONObject jsonObject){
        UserService userService = new UserService(((MatchActivity) mContext));
        userService.updateUser(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User newUser) {
                        try{
                            // Set New User
                            user = newUser;

                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                jsonObject
        );
    }

    /**
     * Generate Click Event For Each Hole Image
     * @param imageView
     */
    private void generateEventClickForEachImage(final ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Position Of Selected ImageView
                int posImageView = listImageView.indexOf(imageView);
                // Add Event To Add Or Convert Into Profile Image
                if(imageView.getDrawable() != null){
                    if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dotted).getConstantState()) {       // TODO: Refactor Repeated Condition
                        openModalChooseOptionToTakePicture();
                    }else{
                        setProfileImage(posImageView);
                    }
                }
            }
        });
    }

    /**
     * Generate Click Event For Each Fab Button
     * @param floatingActionButton
     */
    private void generateEventClickForEachFab(final FloatingActionButton floatingActionButton){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posFabButton = listFloatingActionButton.indexOf(floatingActionButton);
                // If There Is An Image in this Position, then Fab Button Change from "openModalChooseOptionToTakePicture()" to "deleteImage()"
                // If getDrawable is null, It means that the Default "@drawable dotted" was replaced by an URL Image
                // If Drawable is "dotted" then choose Modal Option, in other case it means we have a photo, then choose Delete Option
                ImageView imageView = listImageView.get(posFabButton);
                if(imageView.getDrawable() != null){
                    if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dotted).getConstantState()) {       // TODO: Refactor Repeated Condition
                        openModalChooseOptionToTakePicture();
                    }else{
                        deleteImage(posFabButton);
                    }
                }
            }
        });
    }

    /**
     * Open Modal That Returns an integer with the value associated to "REQUEST_IMAGE_CAPTURE" or "SELECT_IMAGE_REQUEST_GALLERY" to can be able to display the correct Intent
     */
    private void openModalChooseOptionToTakePicture(){
        MatchProfileEditModalFragment matchProfileEditModalFragment = new MatchProfileEditModalFragment();
        matchProfileEditModalFragment.setTargetFragment(this, FRAGMENT_CODE);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_matchProfileEditFragment, matchProfileEditModalFragment, "fragment_container_MatchProfileEditGender");
        transaction.addToBackStack(null);
        transaction.commit();

//        MatchProfileEditModalFragment matchProfileEditModalFragment = new MatchProfileEditModalFragment();
//        matchProfileEditModalFragment.setTargetFragment(this, FRAGMENT_CODE);
//        FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
//        fragmentTransaction.add(R.id.fragment_container_matchProfileEditFragment, matchProfileEditModalFragment, "fragment_container_MatchProfileEditGender");   // ( idFrameLayout, fragmentToShow, "tag" )
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
    }

    /**
     * Delete Image From ImageView Where Position Is The Same As Selected
     * Example: If I Click "idFloatingActionButton3", Then I Delete Image From "idImageView3"
     * @param pos
     */
    private void deleteImage(final int pos){
        ImageService imageService = new ImageService(((MatchActivity) mContext));
        imageService.deleteImage(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User newUser) {
                        try{
                            // Set New User
                            user = newUser;
                            // Reset All Images, before print it all again
                            resetImagesWithEmptyHoles();
                            // Prints The Image From Recently Inserted Image Getter By The Service
                            images = newUser.getImages();
                            printImagesInEmptyHoles(images);

                            // Check If The Hole Of Deleted Image Have Now Another Image And Maintain Styles Of Delete In Fab Button Or Change To Add
                            ImageView imageView = listImageView.get(pos);
                            if(imageView.getDrawable() != null){
                                if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dotted).getConstantState()) {   // TODO: Refactor Repeated Condition
                                    animFabButton(listFloatingActionButton.get(pos), 1000, true);
                                }else{
                                    animFabButton(listFloatingActionButton.get(pos), 1000, false);
                                }
                            }

                            // If we delete an image that is not the last in the array, then we need to reorganize de images.
                            // When we do this, the last hole where there was an image retains the "delete" styles on Fab Button.
                            // To change styles of Fab button to "add", check if the image of position deleted was before than the last image, and check that is not the limit of images.
                            // Then we get the previous last position and set the "add" styles to Fab button
                            int numImagesBeforeDelete = images.size();
                            if(pos < MAX_USER_IMAGES && pos < numImagesBeforeDelete + 1){
                                FloatingActionButton floatingActionButton = listFloatingActionButton.get(numImagesBeforeDelete);
                                animFabButton(floatingActionButton, 1000, true);
                            }

                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                images.get(pos).getId()
        );
    }

    /**
     * Set Profile Image Wherre Position Is The Same As Selected
     * @param pos
     */
    private void setProfileImage(int pos){
        JSONObject jsonObject = new JSONObject();
        JSONObject imageObject = new JSONObject();

        try {
            imageObject.put("id", images.get(pos).getId());
            jsonObject.put("image", imageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateProfileImage(jsonObject);
    }

    /**
     * Update Profile Image
     * @param patchParams
     */
    private void updateProfileImage(JSONObject patchParams){
        ImageService imageService = new ImageService(((MatchActivity) mContext));
        imageService.updateProfileImage(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User newUser) {
                        try{
                            // Set New User
                            user = newUser;
                            // Info
                            ToastUtils.showToastInfo(((MatchActivity) mContext), "Profile Image Changed");

                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                patchParams
        );
    }

    /**
     * Function that Invokes an Intent to Capture a Photo.
     */
    private void dispatchTakePictureIntent(int option) {
        switch (option) {
            case REQUEST_IMAGE_CAPTURE:
                // Take Image from Camera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(((MatchActivity) mContext).getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case SELECT_IMAGE_REQUEST_GALLERY:
                // Pick 1 or more Images from Gallery
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE_REQUEST_GALLERY);
                }catch(Exception e){
                    ToastUtils.showToastError(((MatchActivity) mContext), "Can't open gallery");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Prepare JsonObject With An Array of Images And Insert It In DataBase
     */
    private void saveAndPrintImages(List<Bitmap> images){
        boolean canInsert = checkIfEnoughFreeHoles(images.size());
        // If There Are Empty Holes To Insert The New Images
        if(canInsert){
            JSONObject jsonObject = new JSONObject();
            JSONArray imageArray = new JSONArray();

            for (int i = 0; i < images.size(); i++){
                Bitmap image = images.get(i);
                // Resize And Recycle Bitmap to prevent memory leak
                Bitmap imageResized = ImageUtils.getScaledDownBitmap(image, 640, false);
                String base64 = ImageUtils.bitmapToBase64(imageResized);

                imageArray.put(base64);
            }

            try {
                jsonObject.put("images", imageArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            postUserImages(jsonObject);
        }
        // If There Are Not Enough Empty Holes To Insert The New Images
        else{
            ToastUtils.showToastWarning(((MatchActivity) mContext), "Limit of images exceeded - " + (user.getImages().size() + images.size()) + "/" + MAX_USER_IMAGES);
        }

    }

    /**
     * Check If User Can Insert All The Selected Images, If Not Return False
     * Check If User Don't Have Images, The "user.getImages()" Is Null
     * @param amountNewImages
     */
    private boolean checkIfEnoughFreeHoles(int amountNewImages){
        boolean canInsert = true;

        int amountDefaultImages = 0;
        if(user.getImages() != null){
            amountDefaultImages = user.getImages().size();
        }
        int sum = amountDefaultImages + amountNewImages;
        if(sum > MAX_USER_IMAGES){
            canInsert = false;
        }

        return canInsert;
    }

    /**
     * Add New Images   -   JSONObject Pass an Array of Strings
     *
     * @param postParams
     */
    private void postUserImages(JSONObject postParams){
        ImageService imageService = new ImageService(((MatchActivity) mContext));
        imageService.postImages(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User newUser) {
                        try{
                            // Set New User
                            user = newUser;
                            // Reset All Images, before print it all again
                            resetImagesWithEmptyHoles();
                            // Prints The Image From Recently Inserted Image Getter By The Service
                            images = newUser.getImages();
                            printImagesInEmptyHoles(images);

                        }catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("ErrorMessage: " + errorMessage);
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                postParams
        );
    }

    /**
     * Print All Images Of A List
     *
     * @param images
     */
    private void printImagesInEmptyHoles(List<Image> images){
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            printImagesInEmptyHoles(image.getUrl());
        }
    }

    /**
     * Check If The Hole Is Empty And If It Is, Then Prints The Image
     * Every Time That Call This Function, Search In Every ImageView To Check If Don't Have An Image There, And Then Puts Them
     *
     * @param url
     */
    private void printImagesInEmptyHoles(String url){
        // Complete Name With URL Of The Server To The Images Folder
        String completeUrl = Constants.SERVER_URL + Constants.IMAGES_URL + url;
        // Check Each Image Hole To See The Empty Places To Fill
        for (int i = 0; i < MAX_USER_IMAGES; i++){
            ImageView imageView = listImageView.get(i);
            // When The First URL Image was setted, The Drawable Is Setted To Null, That Happens Because Every Time We Call This Function, The For Loop It Goes Through Previous Positions
            // Where We Have Already Added The URL Image, Then, When We Try To Check "imageView.getDrawable()...." But Drawable Was Replaced By An URL Image, Throws An NullPointerException
            if(imageView.getDrawable() != null){
                if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dotted).getConstantState()) {       // TODO: Refactor Repeated Condition
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .transform(new RoundedCorners(10));
                    //.placeholder(R.drawable.gradient)
                    //.error(R.drawable.ic_not_interested_black_24dp);
                    Glide.with(this).load(completeUrl).apply(options).into(imageView);
                    // FloatingActionButton Animation And Styles
                    animFabButton(listFloatingActionButton.get(i), 1000, false);
                    // Exit When Print The Image Because, If It Is In A For Loop, All Holes It Will be Printed
                    return;
                }
            }
        }
    }

    /**
     * Reset All ImageView With The Default src "@drawable dotted"
     */
    private void resetImagesWithEmptyHoles(){
        Drawable dotted = getResources().getDrawable(R.drawable.dotted);
        for (int i = 0; i < MAX_USER_IMAGES; i++){
            ImageView imageView = listImageView.get(i);
            imageView.setImageDrawable(dotted);
        }
    }

    /**
     * Animation Fab Button And Change Styles consequently
     *
     * @param floatingActionButton
     * @param duration  Transition duration (milliseconds)
     * @param action    True to Add, False to Delete
     */
    private void animFabButton(FloatingActionButton floatingActionButton, int duration, boolean action){
        // Animation
        AnimationUtils.rotate(floatingActionButton, "rotationX", duration);
        setTimeout(floatingActionButton, duration/2, action);
    }

    /**
     * Delay Method
     *
     * @param floatingActionButton
     * @param duration
     * @param action
     */
    private void setTimeout(final FloatingActionButton floatingActionButton, final int duration, final  boolean action){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(action){
                    stylesFloatingActionButtonAdd(floatingActionButton);
                }else{
                    stylesFloatingActionButtonDelete(floatingActionButton);
                }
            }
        }, duration);
    }

    /**
     * Change The Default Styles Of FloatingActionButton To Indicate That It Will Add Image/s
     */
    private void stylesFloatingActionButtonAdd(FloatingActionButton floatingActionButton){
        // Change Background Color
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        // Background Of Touched Button
        floatingActionButton.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        // Rotate -45º To Show "+"
        floatingActionButton.setRotation(0f);
    }

    /**
     * Change The Default Styles Of FloatingActionButton To Indicate That It Will Delete The Image
     */
    private void stylesFloatingActionButtonDelete(FloatingActionButton floatingActionButton){
        // Change Background Color
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        // Background Of Touched Button
        floatingActionButton.setRippleColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        // Rotate 45º To Show An "X"
        floatingActionButton.setRotation(45f);
    }

    /**
     * Set Title Fragment
     */
    private void setupToolbarData(){
        // Set Fragment name
        idTextViewFragmentName.setText("Edit Profile");
    }

    /**
     * Close Fragment And go Back To Back Stack
     */
    private void closeFragment(View view){
        ImageView imageView = view.findViewById(R.id.idImageViewCloseFragment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToParentFragment();
            }
        });
    }

    /**
     * Go Back To Parent Fragment And Send The Choosed Option (Camera/Gallery)
     */
    private void goBackToParentFragment(){
        Fragment fragment = ((MatchActivity) mContext).getSupportFragmentManager().findFragmentByTag("PROFILE_EDIT_FRAGMENT");
        if(fragment != null){
            ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * "onActivityResult" is listening for determined actions like get returned data from another fragment, the result of take pictures...
     *
     * The modal returns back the value to can be able to choose the action that we want (Open Camera or Open Gallery).
     * We Have 3 options:
     *      1) Get A Single Image From Camera
     *      2) Get A Single Image From Gallery
     *      3) Get Multiple Images From Gallery
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Check ResultCode was successful
        if(resultCode == Activity.RESULT_OK){

            // Check If The Fragment Opened is the Modal Chooser, and Get the Selected Action (Photo/Gallery)
            if(requestCode == FRAGMENT_CODE) {
                if(data != null) {
                    imagePicker = data.getIntExtra("option", 1);
                    dispatchTakePictureIntent(imagePicker);
                    return;
                }
            }
            // When We Choose An Image In This Fragment From Camera or Gallery, Then Print and Save It
            // data.getClipData();  --------> Get Array of images(Uris) of gallery
            // data.getData();      --------> Get a Single Image(Uri) of gallery
            if(data != null) {
                switch (imagePicker){
                    case REQUEST_IMAGE_CAPTURE:
                        Bundle extrasCamera = data.getExtras();
                        Bitmap imageBitmapCamera = (Bitmap) extrasCamera.get("data");
                        // Create List With The Image To Be Sent And Save And Print It
                        List<Bitmap> imagesCamera =  Arrays.asList(imageBitmapCamera);
                        saveAndPrintImages(imagesCamera);
                        break;
                    case SELECT_IMAGE_REQUEST_GALLERY:
                        if(data.getData() != null){
                            // Get the content URI for the selected Image (Single)
                            Uri selectedImageGallery = data.getData();
                            Bitmap imageBitmapGallerySingle = ImageUtils.uriToBitmap(((MatchActivity) mContext), selectedImageGallery);
                            // Create List With The Image To Be Sent And Save And Print It
                            List<Bitmap> imagesGallerySingle =  Arrays.asList(imageBitmapGallerySingle);
                            saveAndPrintImages(imagesGallerySingle);
                        }
                        else if(data.getClipData() != null){
                            // Get the content Uri for the selected Images (multiple)
                            List<Bitmap> imagesGalleryMultiple = new ArrayList<>();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++){
                                // Get Uri
                                ClipData.Item item =  data.getClipData().getItemAt(i);
                                Uri uri = item.getUri();
                                // Convert Uri To Bitmap And Add To Bitmap List
                                Bitmap bitmap = ImageUtils.uriToBitmap(((MatchActivity) mContext), uri);
                                imagesGalleryMultiple.add(bitmap);
                            }
                            // Save And Print Images
                            saveAndPrintImages(imagesGalleryMultiple);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * If True Then The <ViewPager> Can't Swipe.
     * If False We Can Swipe The <ViewPager>
     *     TODO: Refactor - This functions is repeated in many sites
     *
     * @param value
     */
    private void disableSwipeViewPager(boolean value){
        ViewPagerUtils customViewPager = (ViewPagerUtils) ((MatchActivity) mContext).findViewById(R.id.pager);
        customViewPager.disableScroll(value);    // DISABLE SWIPE LEFT/RIGHT
    }

    /**
     * This method Overlaps the fragment above the global AppBarLayout(toolbar)
     *
     * @param overlap true overlaps, false undo overlap
     */
    private void setFragmentOverlapAppBarLayout(Boolean overlap){
        ActivityUtils.setFragmentOverlapAppBarLayout(relativeLayoutContentViewPager, overlap);
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
        relativeLayoutContentViewPager = ((MatchActivity) mContext).findViewById(R.id.contentViewPager);
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(true);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        // When Fragment Closes We Can Swipe Again
        disableSwipeViewPager(false);
        // Cancel the Overlap of the RelativeLayout to allow the AppBarLayout Overlap again
        setFragmentOverlapAppBarLayout(false);
        // Go Back To Parent Fragment
        goBackToParentFragment();
    }

}
