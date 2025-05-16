package com.example.gympip;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
public class LocationHelper {

    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public interface LocationCallback {
        void onLocationResult(Location location);
    }

    public LocationHelper(Activity activity) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void getUserLocation(LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, location -> {
                        if (location != null) {
                            saveCurrentUserLocationToFirestore(location); // ✅ auto-save
                            callback.onLocationResult(location);
                        } else {
                            Toast.makeText(activity, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public int getPermissionRequestCode() {
        return LOCATION_PERMISSION_REQUEST_CODE;
    }
    public interface MatchCallback {
        void onMatchResult(boolean matched, float distanceKm);
    }
    public void saveCurrentUserLocationToFirestore(Location location) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", location.getLatitude());
        locationData.put("longitude", location.getLongitude());
        locationData.put("timestamp", System.currentTimeMillis());

        db.collection("user_locations").document(userId)
                .set(locationData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(activity, "Location saved to Firestore", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(activity, "Failed to save location", Toast.LENGTH_SHORT).show()
                );
    }
    public void checkIfUsersAreClose(String userId1, String userId2, float maxDistanceKm, MatchCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user_locations").document(userId1).get().addOnSuccessListener(doc1 -> {
            db.collection("user_locations").document(userId2).get().addOnSuccessListener(doc2 -> {
                if (doc1.exists() && doc2.exists()) {
                    double lat1 = doc1.getDouble("latitude");
                    double lon1 = doc1.getDouble("longitude");

                    double lat2 = doc2.getDouble("latitude");
                    double lon2 = doc2.getDouble("longitude");

                    Location loc1 = new Location("");
                    loc1.setLatitude(lat1);
                    loc1.setLongitude(lon1);

                    Location loc2 = new Location("");
                    loc2.setLatitude(lat2);
                    loc2.setLongitude(lon2);

                    float distance = loc1.distanceTo(loc2) / 1000f; // in kilometers

                    callback.onMatchResult(distance <= maxDistanceKm, distance);
                } else {
                    Toast.makeText(activity, "Location data missing for one or both users", Toast.LENGTH_SHORT).show();
                    callback.onMatchResult(false, -1);
                }
            });
        });
    }
    public void matchWithUser(
            String otherUserId,
            float maxDistanceKm,
            MatchCallback callback
    ) {
        // (A) Get & auto-save our own location
        getUserLocation(location -> {
            if (location == null) {
                Toast.makeText(activity, "Unable to get your location", Toast.LENGTH_SHORT).show();
                callback.onMatchResult(false, -1);
                return;
            }
            // (B) Now compare to the other user
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            checkIfUsersAreClose(
                    currentUserId,
                    otherUserId,
                    maxDistanceKm,
                    callback
            );
        });
    }
    public interface NearbyCallback {
        void onNearbyFound(List<String> userIdsWithinRange);
    }

    /**
     * 1. Grabs & auto-saves current user's location
     * 2. Fetches _all_ user_locations docs
     * 3. Filters to those within maxDistanceKm of current user
     * 4. Returns their UIDs (excluding current user) via callback
     */
    public void findNearbyUsers(
            float maxDistanceKm,
            NearbyCallback callback
    ) {
        getUserLocation(myLocation -> {
            if (myLocation == null) {
                Toast.makeText(activity, "Cannot get your location", Toast.LENGTH_SHORT).show();
                callback.onNearbyFound(new ArrayList<>());
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user_locations")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<String> nearby = new ArrayList<>();
                        String me = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String otherId = doc.getId();
                            if (otherId.equals(me)) continue;

                            Double lat = doc.getDouble("latitude");
                            Double lon = doc.getDouble("longitude");
                            if (lat == null || lon == null) continue;

                            Location otherLoc = new Location("");
                            otherLoc.setLatitude(lat);
                            otherLoc.setLongitude(lon);

                            float distanceKm = myLocation.distanceTo(otherLoc) / 1000f;
                            if (distanceKm <= maxDistanceKm) {
                                nearby.add(otherId);
                            }
                        }

                        callback.onNearbyFound(nearby);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(activity, "Error fetching locations", Toast.LENGTH_SHORT).show();
                        callback.onNearbyFound(new ArrayList<>());
                    });
        });
    }

}
