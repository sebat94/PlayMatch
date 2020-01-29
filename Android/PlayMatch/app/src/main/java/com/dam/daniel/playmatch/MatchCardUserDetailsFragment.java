package com.dam.daniel.playmatch;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;

import java.util.List;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link MatchCardUserDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchCardUserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchCardUserDetailsFragment extends Fragment {

    private int userId = 0;
    private User userDetails = null;

    private Context mContext;
    private OnFragmentInteractionListener mListener;
    private RelativeLayout relativeLayoutContentViewPager;

    public MatchCardUserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MatchCardUserDetailsFragment.
     */
    public static MatchCardUserDetailsFragment newInstance(int userId) {
        MatchCardUserDetailsFragment fragment = new MatchCardUserDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Swipe
        disableSwipeViewPager(true);
        // "getArguments()" is a method that belongs to Fragment class to get parameters sended to it
        readBundle(getArguments());
    }

    /**
     * READ BUNDLE
     * @param bundle
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            userId = bundle.getInt("userId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_card_user_details, container, false);

        // Button Close Fragment
        closeFragment(view);

        // GET USER BY ID
        getUserById(view, userId);

        // Set Event Handlers To Buttons
        setUpButton(view);

        return view;
    }

    /**
     * API CALL - GET USER BY ID
     * @param userId
     */
    private void getUserById(final View view, int userId){
        UserService userService = new UserService(getActivity());
        userService.getUserById(
                new VolleySupport.ServerCallbackUser() {
                    @Override
                    public void onSuccess(User user) {
                        userDetails = user;
                        // When Activity has been passed to onAttach() then we can be able to call fragment view without a possibility of get NullPointerException
                        if(mListener != null){
                            // Set User Details
                            setUpUserDetails(view);
                        }
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        ToastUtils.showToastError(getActivity(), errorMessage);
                    }
                },
                userId
        );
    }

    private void setUpUserDetails(View view){
        // Set User Details in View
        // Set Image
        List<Image> imagesArray = userDetails.getImages();
        String profileImage = ServiceParserUtils.getProfileImage(imagesArray);

        // Glide Set Image
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.gradient)
                .error(R.drawable.ic_not_interested_black_24dp);
        Glide.with(this).load(profileImage).apply(options).into((ImageView) view.findViewById(R.id.idImageViewUserDetails));

        // Set Details
        ((TextView) view.findViewById(R.id.idTextViewNick)).setText(userDetails.getNick()); // TODO: Nick And Age
        ((TextView) view.findViewById(R.id.idTextViewInfo)).setText(userDetails.getJob());  // TODO: Job, Company and School
        ((TextView) view.findViewById(R.id.idTextViewDescription)).setText(userDetails.getDescription());
    }

    /**
     * Close This Fragment
     * @param view
     */
    private void setUpButton(View view){
        // BUTTON CLOSE FRAGMENT
        FloatingActionButton closeFragment = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButtonCloseFragment);
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Fragment fragment = ((MatchActivity) mContext).getSupportFragmentManager().findFragmentByTag("USER_DETAILS_FRAGMENT");
                    if(fragment != null)
                        ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                    System.out.println("EXCEPCION CERRANDO EL FRAGMENT: " + ex.getMessage());
                }
            }
        });
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
     * Close Fragment And go Back To Back Stack
     */
    private void closeFragment(View view){
        // BUTTON CLOSE FRAGMENT
        FloatingActionButton closeFragment = (FloatingActionButton) view.findViewById(R.id.idFloatingActionButtonCloseFragment);
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Fragment fragment = ((MatchActivity) mContext).getSupportFragmentManager().findFragmentByTag("USER_DETAILS_FRAGMENT");
                    if(fragment != null)
                        ((MatchActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                    System.out.println("EXCEPCION CERRANDO EL FRAGMENT: " + ex.getMessage());
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * The onAttach method is used to pass the CONTEXT of Activity through fragments, we can also pass to nested fragments
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        // Get RelativeLayout that we use to overlaps this content above the global AppBarLayout
        relativeLayoutContentViewPager = ((MatchActivity) mContext).findViewById(R.id.contentViewPager);
        // Overlap the RelativeLayout above the global AppBarLayout(toolbar)
        setFragmentOverlapAppBarLayout(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        // When Fragment Closes We Can Swipe Again
        disableSwipeViewPager(false);
        // Cancel the Overlap of the RelativeLayout to allow the AppBarLayout Overlap again
        setFragmentOverlapAppBarLayout(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}