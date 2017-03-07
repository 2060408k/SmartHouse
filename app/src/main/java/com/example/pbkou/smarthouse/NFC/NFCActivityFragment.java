package com.example.pbkou.smarthouse.NFC;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.NotificationService;
import com.example.pbkou.smarthouse.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NFCActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NFCActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NFCActivityFragment extends Fragment {

    private static final int CONTAINER_ID = 0x2222;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HashMap<String,String> playlists = new HashMap<String,String>();
    private MediaPlayerService mediaPlayerService = new MediaPlayerService();
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> notifications = new ArrayList<String>();

    public NFCActivityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NFCActivityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NFCActivityFragment newInstance(String param1, String param2) {
        NFCActivityFragment fragment = new NFCActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String[] proj = {"*"};
        Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        // Queries the user dictionary and returns results
        Cursor playlistCursor = mediaPlayerService.getandroidPlaylistcursor(getContext());


        while (playlistCursor.moveToNext()) {
            String name =playlistCursor.getString(playlistCursor.getColumnIndex("name"));
            String id = playlistCursor.getString(playlistCursor.getColumnIndex("_id"));
            playlists.put(name,id);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get the view
        View v = inflater.inflate(R.layout.fragment_nfcactivity, container, false);

        Button musi_player_btn = (Button) v.findViewById(R.id.music_player_activity_btn);



        LinearLayout llm = (LinearLayout)v.findViewById(R.id.linear_layout_m);
        generateMPLinearLayout(llm);

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public void generateMPLinearLayout(LinearLayout llm){
        for (String playlist : playlists.keySet()){
            TextView tv = new TextView(getContext());
            tv.setText(playlist);
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(32);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView ) v;
                    Integer albumid = Integer.parseInt(playlists.get(tv.getText().toString()));
                    mediaPlayerService.PlaySongsFromAPlaylist(getContext(),albumid);
                }
            });
            llm.addView(tv);
        }
    }
}
