package com.example.traveljournal.ui.map

import android.Manifest
import android.app.Dialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.koushikdutta.ion.Ion


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    private var _binding: FragmentMapBinding? = null

    val titleList = mutableListOf<String>()
    val descriptionList = mutableListOf<String>()
    val coordinateList = mutableListOf<LatLng>()
    val imageList = mutableListOf<String>()
    val idList = mutableListOf<String>()
    val markerList = mutableListOf<MyMarker>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //permission handling
    val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result: Boolean ->
            if (result) {
                Log.i("Permission", "Location permission was granted!")
                //will try to set "isMyLocationEnabled" to true again
                handlePermissions(mMap)
            } else {
                Log.i("Permission", "Location permission was not granted!")
                Toast.makeText(
                    context,
                    "Cannot run app without location permission!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } //permissionLauncher

    fun handlePermissions(mMap: GoogleMap) {
        val fineLocPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        //if the permission is already granted
        if (fineLocPermission == PackageManager.PERMISSION_GRANTED) {
            //do Smth with permission
            Log.i("Permission", "Permission had been granted.")
            mMap.isMyLocationEnabled = true
        } else {
            //ask for permission
            Log.i("Permission", "Permission had not been granted, will ask for permission.")
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mapViewModel =
            ViewModelProvider(this).get(MapViewModel::class.java)

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        locationHelper = LocationHelper(requireContext())
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //MediaWiki API query and handling
    private fun makeRequest(mMap: GoogleMap, location: Location) {
        //it is necessary to clear markerlist, because otherwise it would just keep adding new markers with each query
        markerList.clear()
        titleList.clear()
        descriptionList.clear()
        coordinateList.clear()
        imageList.clear()
        idList.clear()


        Ion.with(this)
            //queries based on the https. the query is adapted to the current location of the user
            //max radius 500m and 10 queries is the default
            .load(
                "https://en.wikipedia.org/w/api.php?action=query&&generator=geosearch&prop=coordinates|pageimages|description|info&&pithumbsize=400&ggsradius=500&ggslimit=10&format=json&ggscoord=" + location.latitude.toString() + "|" + location.longitude.toString()
            ).asJsonObject()?.setCallback { e, result ->
                if (e != null) {
                    Log.e("QueryResult", e.toString())
                }
                if (result == null) {
                    Log.i("QueryResult", "Query ei saa tulemust kätte")
                }
                if (result.get("query") != null ) {
                    Log.i("QueryResult", result.toString())

                    //there should be 10 entries from around 500m of user location
                    val pages = result.get("query").asJsonObject.get("pages").asJsonObject

                    for (entry in pages.entrySet()) {
                        val entryJsonObject = entry.value.asJsonObject

                        //name of the point of interest
                        val title = entryJsonObject.get("title").asString
                        Log.i("title", title)
                        titleList.add(title)

                        //description of the PoI
                        var description = ""
                        if (entryJsonObject.get("description") != null) {
                            description = entryJsonObject.get("description").asString
                        }
                        descriptionList.add(description)

                        //wikipedia page id - used to open the wikipedia page from infowindow
                        val pageId = entryJsonObject.get("pageid").asString
                        idList.add(pageId)


                        //coordinates of the point of interest
                        val coordList = entryJsonObject.get("coordinates").asJsonArray
                        val lat = coordList[0].asJsonObject.get("lat").asDouble
                        val long = coordList[0].asJsonObject.get("lon").asDouble
                        val latLng = LatLng(lat, long)
                        coordinateList.add(latLng)


                        //thumbnail of the point of interest
                        var imageLink: String = ""
                        /**
                         * Enne oli NullPointerException mingitele objektidele, lisasin kiire paranduse et mul app käima ka läheks
                         * parandada kui aega/muutub probleemiks
                         */
                        try {
                            imageLink =
                                entryJsonObject.get("thumbnail").asJsonObject.get("source").asString
                            imageList.add(imageLink)
                        } catch (e: NullPointerException) {
                            Log.i("ImageException", "Image gives NullPointerException")
                        }

                        val marker =
                            MyMarker(title, description, imageLink, pageId, false)
                        markerList.add(marker)

                        //adding a marker based on the queried info
                        mMap.addMarker(MarkerOptions().position(latLng).title(title))
                    }
                } else {
                    Toast.makeText(context, "There is nothing interesting nearby!", Toast.LENGTH_LONG).show()
                }
            }

        //for checking queried info
        Log.i("Title List", titleList.toString())
        Log.i("Description List", descriptionList.toString())
        Log.i("Page Id List", idList.toString())
        Log.i("Coordinate List", coordinateList.toString())
        Log.i("Image List", imageList.toString())


    }

    //this variable is used to check if the user location has changed
    var previousLocation: LatLng = LatLng(0.0, 0.0)

    private fun updatePoIs(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        var latlng = LatLng(latitude, longitude)

        val latitudeChange: Double = Math.abs(latlng.latitude - previousLocation.latitude)
        val longitudeChange: Double = Math.abs(latlng.longitude - previousLocation.longitude)

        Log.d("FIRST $latlng", "and $latitudeChange and $longitudeChange")
        if (latitudeChange > 0.0003 || longitudeChange > 0.0003) {
            mMap.clear() //this removes all set markers, polygons etc
            makeRequest(mMap, location) //requesting new PoI-s based on new location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15F))
            previousLocation = latlng
        }
    }

    val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {
            val location = result.locations[0]
            updatePoIs(location)
        }
    }

    override fun onResume() {
        super.onResume()
        locationHelper.requestLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        locationHelper.stopLocationUpdates()
    }

    //to check if gps location is enabled
    var gpsStatus: Boolean = false
    private fun locationEnabled() {
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //making sure that user has given permission to use their fine location
        handlePermissions(mMap)

        mMap.setOnMyLocationClickListener(this)

        //checking if location service is enabled and notifying user if it's not
        mMap.setOnMyLocationButtonClickListener {
            locationEnabled()
            if (!gpsStatus) { //if location is not enabled
                Toast.makeText(
                    context,
                    "Please turn on location to use the app!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                locationHelper.requestLocationUpdates(locationCallback)
            }
            true
        }


        //searches for the clicked marker from the markerlist, if found, will open a dialog window with the needed info
        mMap.setOnMarkerClickListener {
            for (marker in markerList) {
                if (it.title.toString() == marker.title) {
                    dialogBox(marker)
                    //if current marker was the last one clicked, change icon color to green
                    marker.lastClicked = true
                    it.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    break
                }
            }
            true
        }
    }

    override fun onMyLocationClick(location: Location) {
        updatePoIs(location)
    }

    /**
     * Inspiration for this code block came from https://www.geeksforgeeks.org/how-to-create-dialog-with-custom-layout-in-android/
     */
    fun dialogBox(marker: MyMarker) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.activity_dialog)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val goBackButton: Button = dialog.findViewById(R.id.goBackButton)
        val learnMoreButton: Button = dialog.findViewById(R.id.learnMoreButton)
        val titleView: TextView = dialog.findViewById(R.id.titleView)
        val description: TextView = dialog.findViewById(R.id.descriptionView)
        val image: ImageView = dialog.findViewById(R.id.imageView)

        //PoI title and description setting
        titleView.text = marker.title
        description.text = marker.description

        //Image setting
        Ion.with(this)
            .load(marker.imageLink)
            .withBitmap()
            .intoImageView(image)

        //closes dialog box
        goBackButton.setOnClickListener { dialog.dismiss() }

        //opens the wikipedia page of the PoI based on pageId
        learnMoreButton.setOnClickListener {
            val urlIntent: Intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://en.wikipedia.org/?curid=" + marker.pageId)
            )
            startActivity(urlIntent)
            dialog.dismiss()
        }

        dialog.show()
    }
}