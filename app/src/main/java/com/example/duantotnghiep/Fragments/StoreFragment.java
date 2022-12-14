package com.example.duantotnghiep.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duantotnghiep.Activities.ChooseStoreActivity;
import com.example.duantotnghiep.Activities.LoginActivity;
import com.example.duantotnghiep.Activities.MainActivity;
import com.example.duantotnghiep.Activities.StoreInfoActivity;
import com.example.duantotnghiep.Adapter.StoreAdapter;
import com.example.duantotnghiep.Contract.StoreContact;
import com.example.duantotnghiep.Model.Store;
import com.example.duantotnghiep.Presenter.StorePresenter;
import com.example.duantotnghiep.R;
import com.example.duantotnghiep.Utilities.AppConstants;
import com.example.duantotnghiep.Utilities.TranslateAnimation;
import com.facebook.CallbackManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;


public class StoreFragment extends Fragment implements StoreContact.View {
    private SupportMapFragment supportMapFragment;
    private final StorePresenter storePresenter = new StorePresenter(this);
    private RecyclerView rcvStore;
    private SharedPreferences.Editor mEditor;
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            storePresenter.getProduct();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcvStore = view.findViewById(R.id.rcvStore);

        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationMain);
            rcvStore.setOnTouchListener(new TranslateAnimation(getActivity(), bottomNavigationView));

        }
        SharedPreferences mSharePrefer = getContext().getSharedPreferences(AppConstants.CHECK_PERMISSION, 0);
        boolean isPermissionGranted = mSharePrefer.getBoolean(AppConstants.iSLocationPermissionRequest, false);
        boolean isPermissionGrantedOnetime = mSharePrefer.getBoolean(AppConstants.iSLocationPermissionRequestOnetime, false);
        mEditor = mSharePrefer.edit();


        if (isPermissionGranted || isPermissionGrantedOnetime) {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getContext(), "PLease enable your location ", Toast.LENGTH_SHORT).show();
                return;
            }
            storePresenter.getProduct();
        } else {
            checkMyPermission();
        }
    }

    private void checkMyPermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getContext(), "PLease enable your location ", Toast.LENGTH_SHORT).show();
                    return;
                }
                storePresenter.getProduct();
                mEditor.putBoolean(AppConstants.iSLocationPermissionRequest, true);
                mEditor.putBoolean(AppConstants.iSLocationPermissionRequestOnetime, true);
                mEditor.apply();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "Please Granted Permission in your Setting", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    @Override
    public void setStore(List<Store> mListStore) {

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mvStore);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                        StoreAdapter storeAdapter = new StoreAdapter(getContext(), mListStore, new StoreAdapter.OnClickListener() {
                            @Override
                            public void onCLickChooseStore(String storeName, String storeAddress) {
                                Intent i = new Intent(getContext(), StoreInfoActivity.class);

                                i.putExtra("isChooseStore", false);
                                i.putExtra("storeName", storeName);
                                i.putExtra("storeAddress", storeAddress);
                                startActivity(i);
                                getActivity().overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
                            }

                            @Override
                            public void onClickListener(double latitude, double longitude) {
                                LatLng latLng = new LatLng(latitude, longitude);
                                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                                googleMap.animateCamera(cameraUpdate);
                            }

                            @Override
                            public void onGetDistance(TextView textView, double latitude, double longitude) {
                                if (location == null){
                                    return;
                                }
                                float[] result = new float[10];
                                Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, result);
                                String s = String.format("%.1f", result[0] / 1000);
                                textView.setText(s + "km away from you");
                            }
                        });

                        rcvStore.setLayoutManager(new LinearLayoutManager(getContext()));
                        rcvStore.setAdapter(storeAdapter);
                        for (int i = 0; i < mListStore.size(); i++) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(mListStore.get(i).getLatitude(), mListStore.get(i).getLongitude())));
                        }
                        if (location == null){
                            return;
                        }
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        googleMap.animateCamera(cameraUpdate);


                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEditor.putBoolean(AppConstants.iSLocationPermissionRequestOnetime, false);
        mEditor.apply();
        getContext().unregisterReceiver(locationReceiver);
    }

    @Override
    public void onResponseFail(Throwable t) {
        Toast.makeText(getContext(), "Unknown Error. Please check your location", Toast.LENGTH_SHORT).show();
        Log.d("StoreFragment", t.getMessage());
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        getContext().registerReceiver(locationReceiver, intentFilter);
    }

}