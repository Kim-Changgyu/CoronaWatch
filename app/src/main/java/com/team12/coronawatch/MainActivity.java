package com.team12.coronawatch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private long backPressedTime = 0;
    private Fragment mFragment, sFragment, gFragment;
    int beforeId;

    private void init() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nv);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.statFragment);
        sFragment = new StatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment,
                sFragment).commit();
    }

    private void AppFinish() {
        moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
        finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid());    // 앱 프로세스 종료
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        long FINISH_INTERVAL_TIME = 3000;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            AppFinish();
        } else {
            backPressedTime = tempTime;
            Snackbar.make(findViewById(R.id.layout_fragment), R.string.sbar_again_back_press_msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo wimax = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX); //태블릿 네트워크
        boolean bwimax = false;

        if (wimax != null)
            bwimax = wimax.isConnected();
        if (mobile != null) {
            return !mobile.isConnected() && !wifi.isConnected() && !bwimax;
        } else {
            return !wifi.isConnected() && !bwimax;
        }
    }

    private void networkCheck() {
        if (isNetworkConnected(getApplicationContext())) {
            Log.i("Network connection: ", "disconnected");
            Toast.makeText(getApplicationContext(), R.string.toast_check_network_msg,
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkCheck(); //사용자에게 네트워크 연결을 요청 -> 미 연결시 앱 종료
        init();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    // FragmentManager에 선택한 Fragment가 없으면 add 해주고, 해당 Fragment를 show, 다른 Fragment는 hide
                    if (id == R.id.mapsFragment) {
                        if (mFragment == null) {
                            mFragment = new MapsFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,
                                    mFragment).commit();
                        }
                        if (mFragment != null)
                            getSupportFragmentManager().beginTransaction().show(mFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        if (sFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(sFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
                        if (gFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(gFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
                    } else if (id == R.id.statFragment) {
                        if (sFragment == null) {
                            sFragment = new StatFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,
                                    sFragment).commit();
                        }
                        if (mFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(mFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        if (sFragment != null)
                            getSupportFragmentManager().beginTransaction().show(sFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
                        if (gFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(gFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
                    } else if (id == R.id.graphFragment) {
                        if (gFragment == null) {
                            gFragment = new GraphFragment();
                            getSupportFragmentManager().beginTransaction().add(R.id.layout_fragment,
                                    gFragment).commit();
                        }
                        if (mFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(mFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        if (sFragment != null)
                            getSupportFragmentManager().beginTransaction().hide(sFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_NONE).commit();
                        if (gFragment != null)
                            getSupportFragmentManager().beginTransaction().show(gFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    }
                    if (id != beforeId)
                        beforeId = id;
                    else
                        return false;

                    return true;
                }
            };
}
