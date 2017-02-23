//package com.example.pbkou.smarthouse;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.NetworkInfo;
//import android.os.Bundle;
//import android.support.design.widget.TextInputEditText;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.widget.ThemedSpinnerAdapter;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.sendbird.android.SendBird;
//import com.sendbird.android.SendBirdException;
//import com.sendbird.android.User;
//
///**
// * Created by Alexiah on 20/02/2017.
// */
//
//public class SendBird_main extends FragmentActivity {
//    private static final String appId = "D2A996CB-A75D-4CC2-85F6-E8C1CF136CBA"; /* Sample SendBird Application */
//
//    public static String sUserId;
//    private String mNickname;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        sUserId = "alexiaharalambous";
//        mNickname = "alex";
//
//        SendBird.init(appId, this);
//
////        ((EditText) findViewById(R.id.etxt_user_id)).setText(sUserId);
////        ((EditText) findViewById(R.id.etxt_user_id)).addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////            }
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////            }
////
////            @Override
////            public void afterTextChanged(Editable s) {
////                sUserId = s.toString();
////            }
////        });
////
////        ((TextInputEditText) findViewById(R.id.etxt_nickname)).setText(mNickname);
////        ((TextInputEditText) findViewById(R.id.etxt_nickname)).addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////            }
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////            }
////
////            @Override
////            public void afterTextChanged(Editable s) {
////                mNickname = s.toString();
////            }
////        });
//
//        //This should be the user login button's onclick
//        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Button btn = (Button) view;
//                if (btn.getText().equals("Connect")) {
//                    connect();
//                } else {
//                    disconnect();
//                }
//
//                ThemedSpinnerAdapter.Helper.hideKeyboard(SendBird_main.this);
//            }
//        });
//
//        //View OpenChannels (current chats) List
//        findViewById(R.id.btn_open_channel_list).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SendBird_main.this, SendBirdOpenChannelListActivity.class);
//                startActivity(intent);
//            }
//        });
//
////        //We are not going to use GroupChannels
////        //View GroupChannels
////        findViewById(R.id.btn_group_channel_list).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(SendBird_main.this, SendBirdGroupChannelListActivity.class);
////                startActivity(intent);
////            }
////        });
//    }
//
//    private void connect() {
//        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//                if (e != null) {
//                    Toast.makeText(SendBird_main.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String nickname = mNickname;
//
//                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
//                    @Override
//                    public void onUpdated(SendBirdException e) {
//                        if (e != null) {
//                            Toast.makeText(SendBird_main.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
//                        editor.putString("user_id", sUserId);
//                        editor.putString("nickname", mNickname);
//                        editor.commit();
//                    }
//                });
//
//                if (FirebaseInstanceId.getInstance().getToken() == null) return;
//
//                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), true, new SendBird.RegisterPushTokenWithStatusHandler() {
//                    @Override
//                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
//                        if (e != null) {
//                            Toast.makeText(SendBird_main.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
//                });
//            }
//        });
//
//    }
//
//    private void disconnect() {
//        SendBird.disconnect(new SendBird.DisconnectHandler() {
//            @Override
//            public void onDisconnected() {
//                // You are disconnected from SendBird.
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SendBird.disconnect(null);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        /**
//         * If the minimum SDK version you support is under Android 4.0,
//         * you MUST uncomment the below code to receive push notifications.
//         */
////        SendBird.notifyActivityResumedForOldAndroids();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        /**
//         * If the minimum SDK version you support is under Android 4.0,
//         * you MUST uncomment the below code to receive push notifications.
//         */
////        SendBird.notifyActivityPausedForOldAndroids();
//    }
//
//}
