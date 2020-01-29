package com.dam.daniel.playmatch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dam.daniel.playmatch.adapters.ChatBoxAdapter;
import com.dam.daniel.playmatch.models.Chat;
import com.dam.daniel.playmatch.models.Message;
import com.dam.daniel.playmatch.services.MessageService;
import com.dam.daniel.playmatch.utils.ActivityUtils;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchChatBoxFragment extends Fragment {

    // Variables
    private Chat chat;
    private String nick;
    private String profileImage;

    private ImageView idImageViewUserProfile;
    private TextView idTextViewNick;

    // Declare Socket Object
    private Socket socket;

    public RecyclerView myRecylerView ;
    public List<Message> messageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt ;
    public ImageButton send;

    private Context mContext;
    private RelativeLayout relativeLayoutContentViewPager;

    public MatchChatBoxFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Swipe
        disableSwipeViewPager(true);
        // "getArguments()" is a method that belongs to Fragment class to get parameters sended to it
        readBundle(getArguments());

        // TODO: Attempt to invoke virtual method 'void android.app.Activity.runOnUiThread(java.lang.Runnable)' on a null object reference
        // TODO: Si cambiamos el nombre de la persona que escribe en el chat, explota al enviar un mensaje.
        // TODO: De todas maneras, debemos cerrar el Fragment de Chat Cada vez que queramos iniciar otro chat. Por lo tanto este fragment se iniciará cada vez que habramos una conversación

        // Socket IO Connection
        socketIOConnection();
        //socketIOConnectionReceived();

        // Socket IO Disconnect
        socketIOUserDisconnected();
    }

    /**
     * READ BUNDLE
     * @param bundle
     */
    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            // To Retrieve Gson
            Gson gson = new Gson();
            chat = gson.fromJson(bundle.getString("chat"), Chat.class);
            // Get Nick and Image
            nick = bundle.getString("nick");
            profileImage = bundle.getString("profileImage");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_chat_box, container, false);

        // Init Toolbar
        idImageViewUserProfile = (ImageView) view.findViewById(R.id.idImageViewUserProfile);
        idTextViewNick = (TextView) view.findViewById(R.id.idTextViewNick);

        // Init Chat
        messagetxt = (EditText) view.findViewById(R.id.message);
        send = (ImageButton) view.findViewById(R.id.send);
        messageList = new ArrayList<>();
        myRecylerView = (RecyclerView) view.findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(((MatchActivity) mContext).getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        // Load Image And Name of User To Chat
        setupToolbarData();
        // Button Close Fragment
        closeFragment(view);
        // Load Previous Messages
        getPreviousMessages();
        // Emit Messages
        socketIOMessage();
        // Listen Messages
        socketIOMessageReceived();

        return view;
    }

    /**
     * 1) Connect your socket client to the server
     */
    private void socketIOConnection(){
        try {
            //if you are using a phone device you should connect to same local network as your laptop and disable your pubic firewall as well
            socket = IO.socket(Constants.SERVER_URL);
            //create connection
            socket.connect();
            // emit the event join along side with the room name
            socket.emit("room", "room-" + chat.getId());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2) Listen for "userjoinedthechat" event emitted from Server
     */
    private void socketIOConnectionReceived(){
        socket.on("userjoinedthechat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((MatchActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        // get the extra data from the fired event and display a toast
                        ToastUtils.showToastInfo(((MatchActivity) mContext), data);
                    }
                });
            }
        });
    }

    /**
     * Listen for Previous Message Of Chat
     */
    private void getPreviousMessages(){
        MessageService messageService = new MessageService(((MatchActivity) mContext));
        messageService.getPreviousMessages(
                new VolleySupport.ServerCallbackMessageList() {
                @Override
                    public void onSuccess(List<Message> result) {
                        // Transform Message into MessageSocketIO for Adapter
                        for(Message message : result){
                            // make instance of message
                            //MessageSocketIO m = new MessageSocketIO(message.getMessage());
                            //add the message to the messageList
                            messageList.add(message);
                        }
                        // Show messages
                        updateRecyclerViewChatBox();
                    }
                },
                new VolleySupport.ServerCalbackError() {
                    @Override
                    public void onError(String errorMessage) {
                        System.out.println("Error obteniendo anteriores mensajes: " + errorMessage);
                    }
                },
                chat.getId()
        );
    }

    /**
     * 3) Emit Event "messagedetection" to Server
     */
    private void socketIOMessage(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if(!messagetxt.getText().toString().isEmpty()){
                    // Parse Chat Object to String to Sent It Through the Socket
                    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();   // This will serialize NaN and Infinity, but not as strings.
                    String chatJson = gson.toJson(chat);
                    // Emit
                    socket.emit("messageDetection", chatJson, messagetxt.getText().toString(), LocalSorage.loadDataFromSharedPreferences(((MatchActivity) mContext)).getToken());
                    messagetxt.setText("");
                }
            }
        });
    }

    /**
     * 4) Listen for "message" event emitted from Server
     */
    private void socketIOMessageReceived(){
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((MatchActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Message message = ServiceParserUtils.parseJsonMessage(data);
                        //add the message to the messageList
                        messageList.add(message);
                        // Show messages
                        updateRecyclerViewChatBox();
                    }
                });
            }
        });
    }

    /**
     * 5) Listen for "userdisconnect" event emitted from Server
     */
    private void socketIOUserDisconnected(){
        socket.on("userDisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ((MatchActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        System.out.println("LLEGAAAAAAAAAAAAAAAA: " + data);
                        ToastUtils.showToastInfo(((MatchActivity) mContext), data);
                    }
                });
            }
        });
    }

    /**
     * Update Messages for RecyclerView
     */
    private void updateRecyclerViewChatBox(){
        // add the new updated list to the adapter
        chatBoxAdapter = new ChatBoxAdapter(((MatchActivity) mContext), messageList);

        // notify the adapter to update the recycler view
        chatBoxAdapter.notifyDataSetChanged();

        //set the adapter for the recycler view
        myRecylerView.setAdapter(chatBoxAdapter);
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
     * Set info of the user that you are going to talk
     */
    private void setupToolbarData(){
        // Set Image
        Glide.with(this).load(profileImage).apply(RequestOptions.circleCropTransform()).into(idImageViewUserProfile);
        // Set Nick
        idTextViewNick.setText(nick);
    }

    /**
     * Close Fragment And go Back To Back Stack
     */
    private void closeFragment(View view){
        ImageView imageView = view.findViewById(R.id.idImageViewCloseFragment);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ((MatchActivity) mContext).onBackPressed();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                    System.out.println("EXCEPCION CERRANDO EL FRAGMENT: " + ex.getMessage());
                }
            }
        });
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

    /**
     * On Destroy Fragment, Disconnect The Opened Socket
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // When Fragment Closes We Can Swipe Again
        disableSwipeViewPager(false);
        // Cancel the Overlap of the RelativeLayout to allow the AppBarLayout Overlap again
        setFragmentOverlapAppBarLayout(false);
    }

}
