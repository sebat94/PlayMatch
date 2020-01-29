package com.dam.daniel.playmatch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.models.UserCard;
import com.dam.daniel.playmatch.services.FavouriteService;
import com.dam.daniel.playmatch.services.MatchService;
import com.dam.daniel.playmatch.services.UserService;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.adapters.UserCardAdapter;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.internal.CardStackState;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchCardFragment extends Fragment implements MatchCardUserDetailsFragment.OnFragmentInteractionListener, CardStackListener {   // TODO: El interaction, no se si solo en la Activity o en ambos lados por el hecho de que se llama desde aqui a lo mejor tambien necesita tenerlo

    // Variables
    private static List<User> users = new ArrayList<>();
    // Cards
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private UserCardAdapter adapter;
    private CardStackState state;
    // ViewPager
    ViewPagerUtils customViewPager;
    // Card Swiped
    boolean saveAsFavourite = false;    // Indicates If Action To Do Is Save As Favourite

    private Context mContext;

    public MatchCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init
        customViewPager = (ViewPagerUtils) ((MatchActivity) mContext).findViewById(R.id.pager);
    }

    /**
     * We Start Process Of Cards inside "onCreateView" because we need to take an element of .xml through the "id",
     * In activity, all .xml is loaded, but in a Fragment must create the view and then we can get the .xml element
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_card, container, false);

        // Initzialize Cards
        cardStackView = view.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(((MatchActivity) mContext), this);
        adapter = new UserCardAdapter(getFragmentManager(), new ArrayList<UserCard>());
        state = new CardStackState();

//        /**
//         * Detect if we Touch the Card, and if s true, then Disable Scroll of Custom ViewPager
//         */
//        cardStackView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//
////                    Rect cardYuyakaido = new Rect();
////                    cardStackView.getHitRect(cardYuyakaido);
//
//                    ViewPagerUtils customViewPager = (ViewPagerUtils) ((MatchActivity) mContext).findViewById(R.id.pager);
//                    customViewPager.disableScroll(true);    // DISABLE
////                    if (cardYuyakaido.contains((int) event.getX(), (int) event.getY())) {
////                        System.out.println("CONTAINS CARD : " + event.getX() + " - "  + event.getY());
////                        customViewPager.disableScroll(true);    // DISABLE
////
////                    }else{
////                        System.out.println("NO CONTAINS CARD : " + event.getX() + " - "  + event.getY());
////                        customViewPager.disableScroll(false);    // ENABLE
////                    }
//                }
//                return true;
//            }
//        });

        // Get All Users And Configure Cards
        getAllUsers(view);

        return view;
    }

    /**
     * API CALL - GET ALL USERS
     *      1) SET GETTED USERS IN THE ADAPTER
     *      2) Build Array of "UserCard" And Send It To Adapter
     *      3) Initialize CardStackView
     *      4) Setup Action Buttons of CardStackView
     * @param view
     */
    private void getAllUsers(final View view){
        // GET ALL USERS
        UserService userService = new UserService(((MatchActivity) mContext));
        userService.getAllUsers(
                new VolleySupport.ServerCallbackUserList() {
                    @Override
                    public void onSuccess(List<User> usersList) {
                        // Add to ArrayList the users returned by the callback
                        users.addAll(usersList);
                        // Set Adapter With Users Data
                        adapter = new UserCardAdapter(getFragmentManager(), userCardList());
                        //adapter.notifyDataSetChanged();
                        // Init Cards
                        setupCardStackView();
                        setupButton(view);
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                }
        );
    }

    /**
     * Initialize Card Stack View
     */
    private void setupCardStackView() {
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
    }

    /**
     * Build Array Of UserCard To Send To Card Adapter
     * @return
     */
    private List<UserCard> userCardList() {
        List<UserCard> userCards = new ArrayList<>();
        for(User user: users){
            UserCard userCard = new UserCard();
            userCard.id = user.getId();
            userCard.name = (user.getNick().length() > 0) ? user.getNick() : "Nuevo Usuario";
            userCard.city = (user.getCity().length() > 0) ? user.getCity() : "";
            // Get Profile Image To Show or By Default The First Image In The Array, in another case, we set a default image
            List<Image> imagesArray = user.getImages();
            userCard.url = ServiceParserUtils.getProfileImage(imagesArray);
            // Add To UserCards
            userCards.add(userCard);
        }
        return userCards;
    }

    /**
     * Action Buttons
     * @param view
     */
    private void setupButton(View view) {
        FloatingActionButton skip = (FloatingActionButton) view.findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Left)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
        FloatingActionButton rewind = (FloatingActionButton) view.findViewById(R.id.rewind_button);
        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(200)
                        .setInterpolator(new DecelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.rewind();
            }
        });
        FloatingActionButton like = (FloatingActionButton) view.findViewById(R.id.like_button);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();
            }
        });
        FloatingActionButton favorite = (FloatingActionButton) view.findViewById(R.id.favourite_button);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                        .setDirection(Direction.Right)
                        .setDuration(200)
                        .setInterpolator(new AccelerateInterpolator())
                        .build();
                manager.setSwipeAnimationSetting(setting);
                cardStackView.swipe();

                // Tell to "onCardSwiped()" If It Have to Save As Favourite or not
                saveAsFavourite = true;
            }
        });
    }

    /**
     * API CALL - ADD LIKE
     * @param userId
     */
    private void addLike(int userId){                                                               // TODO: Ver Porque llegando bien el userID inserta el siguiente
        MatchService matchService = new MatchService(((MatchActivity) mContext));

        // ADD LIKE
        JSONObject postParams = new JSONObject();
        try {
            postParams.put("user", new JSONObject().put("id", userId));
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error Generating JSONObject With Post Params");
        }

        matchService.addMatch(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        // Nothing to do when add a like
                        //ToastUtils.showToastInfo(MatchActivity.this, "LIKE");
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                postParams
        );
    }

    /**
     * API CALL - ADD FAVOURITE
     * @param userId
     */
    private void addFavourite(int userId){
        FavouriteService favouriteService = new FavouriteService(((MatchActivity) mContext));

        // ADD LIKE
        JSONObject postParams = new JSONObject();
        try {
            postParams.put("user", new JSONObject().put("id", userId));
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error Generating JSONObject With Post Params");
        }

        favouriteService.addFavourite(
                new VolleySupport.ServerCallbackEmpty() {
                    @Override
                    public void onSuccess() {
                        // Nothing to do when add a favourite
                        //ToastUtils.showToastInfo(MatchActivity.this, "FAVOURITE");
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        ToastUtils.showToastError(((MatchActivity) mContext), errorMessage);
                    }
                },
                postParams
        );
    }

    /**
     * Card Event Handlers
     */
    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        int cardIndex = manager.getTopPosition() - 1;
        if(direction.equals(Direction.Right)){
            // Add Favourite
            if(saveAsFavourite){
                addFavourite(users.get(cardIndex).getId());
                saveAsFavourite = false;
            }
            // Add Like
            else{
                addLike(users.get(cardIndex).getId());
            }
        }
    }

    @Override
    public void onCardRewound() {
        // TODO: Rewind only rejected matches
    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    /**
     * The onAttach method is used to pass the CONTEXT of Activity through fragments, we can also pass to nested fragments
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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