package com.dam.daniel.playmatch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dam.daniel.playmatch.adapters.ChatListAdapter;
import com.dam.daniel.playmatch.models.Chat;
import com.dam.daniel.playmatch.models.ChatItem;
import com.dam.daniel.playmatch.models.ChatResponse;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.services.ChatService;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.ToastUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;
import com.dam.daniel.playmatch.utils.customXML.ViewPagerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.dam.daniel.playmatch.utils.Constants.IMAGES_URL;
import static com.dam.daniel.playmatch.utils.Constants.SERVER_URL;

public class MatchChatFragment extends Fragment {

    private String nick;
    private String profileImage;

    // Views
//    private Button btn;
//    private EditText nickname;
    private ListView idListChat;
    private ArrayList<ChatResponse> dataArrayList;
    private ArrayList<ChatItem> dataArrayListAdapter;
    private ChatListAdapter chatListAdapter;

    private Context mContext;

    // Declare Socket Object
//    private Socket socket;

    public MatchChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_chat, container, false);

        // Init
//        btn = (Button) view.findViewById(R.id.enterchat);
//        nickname = (EditText) view.findViewById(R.id.nickname);
        idListChat = (ListView) view.findViewById(R.id.idListChat);
        dataArrayList = new ArrayList<>();
        dataArrayListAdapter = new ArrayList<>();
        // Get All My Chats And Print It
        getAllChats();

        // Event Handlers
        eventHandlers();

        return view;
    }

    /**
     * Event handlers
     */
    private void eventHandlers(){
        // OnClick Item List
        idListChat.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatItem chatItem = (ChatItem) parent.getItemAtPosition(position);                  // Casteo Implícito

                ChatResponse chatResponse = dataArrayList.get((int) id);                            // There is a data lost parsing long to int, but is quite difficult that the user has more tan "2147483647" matches (impossible)
                Chat chat = chatResponse.getChat();
                // Open Fragment ChatBox
                MatchChatBoxFragment matchChatBoxFragment = new MatchChatBoxFragment();
                // Parse ChatResponse to String
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();       // This will serialize NaN and Infinity, but not as strings.
                String chatJson = gson.toJson(chat);
                // Put Extra Data
                Bundle bundle = new Bundle();
                bundle.putString("chat", chatJson);                                                 // Chat Object
                bundle.putString("nick", chatItem.getName());                                       // Nick Opposite
                bundle.putString("profileImage", chatItem.getImage());                              // ProfileImage Opposite
                matchChatBoxFragment.setArguments(bundle);
                // Load Fragment
                MatchProfileEditFragment matchProfileEditFragment = new MatchProfileEditFragment();
                FragmentManager fragmentManager = ((MatchActivity) mContext).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                fragmentTransaction.replace(R.id.fragment_container_match_chat, matchChatBoxFragment,"MATCH_CHAT_BOX_FRAGMENT");   // ( idFrameLayout, fragmentToShow, "tag" )
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    /**
     * API CALL - Get All Chats
     */
    private void getAllChats(){
        ChatService chatService = new ChatService(((MatchActivity) mContext));
        chatService.getAllChats(
                new VolleySupport.ServerCallbackChatList() {
                    @Override
                    public void onSuccess(List<ChatResponse> result) {
                        // Save Result in ArrayList To Can Be Able To Get Each One Through Index Row
                        dataArrayList.addAll(result);
                        // Parse List<ChatResponse> To a List<ChatItem> Object For Adapter
                        dataArrayListAdapter.addAll( parseChatsForAdapter(result) );
                        // Set Data Into Adapter And Bind It To My List
                        chatListAdapter = new ChatListAdapter(((MatchActivity) mContext), dataArrayListAdapter);
                        idListChat.setAdapter(chatListAdapter);


                        // Socket IO Connection - Create A ROOM For Each Match
                        //socketIOConnection();
                        // Socket IO Disconnect
                        //socketIOUserDisconnected();
                    }
                },
                new VolleySupport.ServerCalbackError(){
                    @Override
                    public void onError(String errorMessage) {

                    }
                }
        );
    }

    /**
     * Parse All Chats From DDBB To "ChatItem" for Adapter
     */
    private List<ChatItem> parseChatsForAdapter(List<ChatResponse> chats){
        List<ChatItem> parsedChats = new ArrayList<>();

        for(ChatResponse chat: chats){
            // Get Profile Image To Show or By Default The First Image In The Array, in another case, we set a default image
            List<Image> imagesArray = chat.getUser().getImages();
            String profileImage = ServiceParserUtils.getProfileImage(imagesArray);

            // If There Are No Messages, Then We Send Null
            String lastMessage = (chat.getMessage() != null) ? chat.getMessage().getMessage() : null;

            parsedChats.add( new ChatItem(chat.getUser().getNick(), lastMessage, profileImage) );
        }
        return parsedChats;
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

//    /**
//     * 1) Connect your socket client to the server
//     */
//    private void socketIOConnection(){
//        try {
//            //if you are using a phone device you should connect to same local network as your laptop and disable your pubic firewall as well
//            socket = IO.socket(Constants.SERVER_URL);
//            //create connection
//            socket.connect();
//            // emit the event join along side with the room name
//            for(ChatResponse c : dataArrayList){
//                socket.emit("room", "room-" + c.getChat().getId());
//            }
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * 5) Listen for "userdisconnect" event emitted from Server
//     */
//    private void socketIOUserDisconnected(){
//        socket.on("userdisconnect", new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                ((MatchActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String data = (String) args[0];
//                        ToastUtils.showToastInfo(((MatchActivity) mContext), data);
//                    }
//                });
//            }
//        });
//    }

//    /**
//     * On Destroy Fragment, Disconnect The Opened Socket
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        socket.disconnect();
//    }

}
