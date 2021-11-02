package com.team12.coronawatch;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

//AVD GSP 사용 방법
//AVD 실행후 ctl+shift+L
//원하는 위치 선택후 set location 버튼 클릭

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    //지도 관련 변수
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationSource locationSource;
    private MapView covidMap;
//    private pushData pushData;
    //임시마크
    private Marker covidArea1 = new Marker();
    private Marker covidArea2 = new Marker();
    private Marker covidArea3 = new Marker();
    private Context context = getContext();
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    private Button isDanger;
    NotificationCompat.Builder mBuilder;
//    Criteria criteria = new Criteria();
//    private long startTime = -1;
//    private Location beforeLocation;
//    private Location curLocation;


    private void init(View v) {
    }

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Fragment mapFragment = getParentFragmentManager().findFragmentById(R.id.mapsFragment);
        if (mapFragment != null) {
            getParentFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializing maps object
        NaverMapSdk.getInstance(requireActivity()).setClient(new NaverMapSdk.NaverCloudPlatformClient(
                "cwouczl691"));
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        mBuilder = new NotificationCompat.Builder(getActivity()).setDefaults(Notification.DEFAULT_SOUND).setContentText("위험지역입니다, 벗어나세요").setAutoCancel(true).setSmallIcon(R.drawable.icon_maps);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //뷰 생성
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initializing maps object
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        NaverMapSdk.getInstance(getActivity()).setClient(new NaverMapSdk.NaverCloudPlatformClient(
                "cwouczl691"));
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // Inflate the layout for this fragment
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_maps, container, false);
//        init(v);

        covidMap = v.findViewById(R.id.map_view);
        covidMap.onCreate(savedInstanceState);
        covidMap.getMapAsync(this);

        isDanger = v.findViewById(R.id.button);
        isDanger.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                //사용자의 위치 수신을 위한 세팅
                locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
                //사용자의 현재 위치
                Location userLocation = getMyLocation();
                if( userLocation != null ) {
                    double latitude = userLocation.getLatitude();
                    double longitude = userLocation.getLongitude();
                    System.out.println("////////////현재 내 위치값 : "+latitude+","+longitude);
                    if(getDistance(latitude, longitude, covidArea1.getPosition().latitude, covidArea1.getPosition().longitude)<500){
                        Toast.makeText(getActivity(),"위험지역 토스트메세지",Toast.LENGTH_SHORT).show();
                        //알림부분
                        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1, mBuilder.build());

                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        //카메라 위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(37.65, 126.97),   // 위치 지정(제주도)
                9,                                     // 줌 레벨
                0,                                       // 기울임 각도
                0                                     // 방향
        );
        naverMap.setCameraPosition(cameraPosition);
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
//                CameraUpdate.zoomTo(5.0);
            }
        });

        // 줌 범위 제한
        naverMap.setMinZoom(6.0);   //최소
        naverMap.setMaxZoom(18.0);  //최대용

        //setMark 함수 이용 확진자 방문 좌표를 알 경우 마크 생성 가능
        setMark(covidArea1, 37.38, 126.94, naverMap);
        setMark(covidArea2, 37.5895, 126.99, naverMap);
        setMark(covidArea3, 37.48, 126.84, naverMap);

//        covidArea1.setOnClickListener(new Overlay.OnClickListener() {
//            @Override
//            public boolean onClick(@NonNull Overlay overlay) {
//                InfoWindow infoWindow = new InfoWindow();
//                infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(context) {
//                    @NonNull
//                    @Override
//                    public CharSequence getText(@NonNull InfoWindow infoWindow) {
//                        return "정보창내용";
//                    }
//                });
//                infoWindow.open(covidArea1);
//                return false;
//            }
//        });

    }


    //마커 생성 함수
    private void setMark(Marker marker, double lat, double lng, NaverMap naverMap){
        marker.setPosition(new LatLng(lat, lng));
        marker.setMap(naverMap);
    }

    /*사용자의 위치를 수신*/
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한 요청이 필요함");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
            getMyLocation(); //이건 써도되고 안써도 되지만 권한 승인하면 즉시 위치값 받아옴
        }
        else {
            System.out.println("////////////사용자에게 권한 요청이 필요하지않음");

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }

        return currentLocation;
    }

    //두 좌표사이 거리
    static double getDistance(double x, double y, double x1, double y1) {
        double d;
        int xd, yd;
        yd = (int) Math.pow((y1-y),2);
        xd = (int) Math.pow((x1-x),2);
        d = Math.sqrt(yd+xd);
        return d;
    }

//    public interface  pushData{
//        void positionSet(double lat, double lng);
//    }
//
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if(context instanceof pushData){
//            pushData = (pushData) context;
//        }
//        else{
//            throw new RuntimeException(context.toString() + "@@@");
//        }
//    }
//
//    public void onDetach(){
//        super.onDetach();
//        pushData = null;
//    }
}