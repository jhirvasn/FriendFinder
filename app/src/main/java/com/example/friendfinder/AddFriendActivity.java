package com.example.friendfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.friendfinder.data.model.Friend;
import com.example.friendfinder.data.model.User;
import com.example.friendfinder.data.remote.ApiService;
import com.example.friendfinder.data.remote.ApiUtils;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddFriendActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";
    private ApiService mApiService;
    private CompositeDisposable mCompositeDisposable;
    private String myDeviceId;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        this.mCompositeDisposable = new CompositeDisposable();

        myDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mApiService = ApiUtils.getApiService();

        Button addFriendButton = findViewById(R.id.myAddFriendButton);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText myPairingNumberEditText = findViewById(R.id.myPairingNumberEditText);
                String pairingNumber = myPairingNumberEditText.getText().toString();
                getUserWithPairingNumber(pairingNumber);
            }
        });
    }

    private void getUserWithPairingNumber(final String pairingNumber) {

        Single<List<User>> user = mApiService.getUserWithPairingNumber(pairingNumber);

        user.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<User> users) {

                        if (users.size() > 0) {

                            Log.d(TAG, users.get(0).getDeviceId());
                            Log.d(TAG, users.get(0).getPairingNumber());

                            mUser = users.get(0);
                            Toast.makeText(AddFriendActivity.this, "User found, adding friend", Toast.LENGTH_LONG).show();

                            saveFriendPair(new Friend(myDeviceId, mUser.getDeviceId()));
                            saveFriendPair(new Friend(mUser.getDeviceId(), myDeviceId));

                        }
                        else {
                            Toast.makeText(AddFriendActivity.this, "User not found, pairing number doesn't exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "getUser onError");
                        e.printStackTrace();
                    }
                });
    }

    private void saveFriendPair(Friend friend) {

        Single<Friend> friendPair = mApiService.createFriendPair(friend);

        friendPair.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Friend>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Friend friend) {

                        if (friend == null) {
                            Log.d(TAG, "onSuccess: friend == null");
                        }
                        else {
                            Log.d(TAG, friend.toString());
                        }

                        Toast.makeText(AddFriendActivity.this, "Friend pair saved", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "saveUser onError");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
