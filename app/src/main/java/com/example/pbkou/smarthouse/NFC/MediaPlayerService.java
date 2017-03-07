package com.example.pbkou.smarthouse.NFC;

import android.app.SearchManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import com.example.pbkou.smarthouse.R;

/**
 * Created by pbkou on 03/03/2017.
 */

public class MediaPlayerService extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    public IBinder onBind(Intent arg0) {

        return null;
    }


    public Cursor getAlbumAlbumcursor(Context context, Cursor cursor)
    {
        String where = null;
        ContentResolver cr = context.getContentResolver();
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_id = MediaStore.Audio.Albums.ALBUM_ID;
        final String album_name =MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String[]columns={_id,album_name, artist};
        cursor = cr.query(uri,columns,where,null, null);
        return cursor;
    }

    public Cursor getTrackTrackcursor(Context context)
    {
        final String track_id = MediaStore.Audio.Media._ID;
        final String track_no =MediaStore.Audio.Media.TRACK;
        final String track_name =MediaStore.Audio.Media.TITLE;
        final String artist = MediaStore.Audio.Media.ARTIST;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String album = MediaStore.Audio.Media.ALBUM;
        final String composer = MediaStore.Audio.Media.COMPOSER;
        final String year = MediaStore.Audio.Media.YEAR;
        final String path = MediaStore.Audio.Media.DATA;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver cr =  context.getContentResolver();
        final String[]columns={track_id, track_no, artist, track_name,album, duration, path, year, composer};
        Cursor cursor = cr.query(uri,columns,null,null,null);
        return cursor;
    }

    public  Cursor getandroidPlaylistcursor(Context context)
    {
        ContentResolver resolver = context.getContentResolver();
        final Uri uri=MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        final String id = MediaStore.Audio.Playlists._ID;
        final String name = MediaStore.Audio.Playlists.NAME;
        final String[]columns = {id,name};
        final String criteria = MediaStore.Audio.Playlists.NAME.length() + " > 0 " ;
        final Cursor crplaylists = resolver.query(uri, columns, criteria, null,null);
        return crplaylists;
    }

    public void PlaySongsFromAPlaylist(Context context,int playListID){
        ContentResolver resolver = context.getContentResolver();

        String[] ARG_STRING = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE,android.provider.MediaStore.MediaColumns.DATA};

        Uri membersUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        System.out.println(membersUri);
        Cursor songsWithingAPlayList = resolver.query(membersUri, ARG_STRING, null, null, null);
        int theSongIDIwantToPlay = 0; // PLAYING FROM THE FIRST SONG
        if(songsWithingAPlayList != null)
        {
            songsWithingAPlayList.moveToPosition(theSongIDIwantToPlay);
            System.out.println(songsWithingAPlayList.getCount());
            System.out.println(songsWithingAPlayList.getString(1));
            System.out.println(songsWithingAPlayList.getString(2));
            System.out.println(songsWithingAPlayList.getString(3));
            String DataStream = songsWithingAPlayList.getString(4);
            PlayMusic(DataStream);
            songsWithingAPlayList.close();
        }
    }

    public static void PlayMusic(String DataStream){
        MediaPlayer mpObject = new MediaPlayer();
        if(DataStream == null)
            return;
        try {
            mpObject.setDataSource(DataStream);
            mpObject.prepare();
            mpObject.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play5(Context context,String playlistName) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE, "android.intent.extra.playlist" );
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/playlist");
        intent.putExtra("android.intent.extra.playlist", playlistName);
        intent.putExtra(SearchManager.QUERY, "playlist " + playlistName);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
