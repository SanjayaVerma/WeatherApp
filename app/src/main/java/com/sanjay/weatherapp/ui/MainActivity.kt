package com.sanjay.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sanjay.weatherapp.BuildConfig
import com.sanjay.weatherapp.R
import com.sanjay.weatherapp.data.model.DataList
import com.sanjay.weatherapp.utils.Constants
import com.sanjay.weatherapp.utils.Status
import com.sanjay.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope {
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var adapter: WeatherAdapter
    var layoutManager: LinearLayoutManager? = null
    var recyclerView: RecyclerView? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var job: Job
    private lateinit var arrayList: List<DataList>
    private var mLastLocation: Location? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLat by Delegates.notNull<Int>()
    private var mLong by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        initView()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //** Set the colors of the Pull To Refresh View
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        swipeRefreshLayout.setColorSchemeColors(Color.WHITE)

        swipeRefreshLayout.setOnRefreshListener {
            arrayList = emptyList()
            setupObserver(mLat, mLong)
        }

        if (this::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            Constants.REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {

            showSnackBar(R.string.permission_rationale, android.R.string.ok,
                View.OnClickListener {
                    // Request permission
                    startLocationPermissionRequest()
                })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackBar(R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     *
     *
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result
                    mLat = (mLastLocation)!!.latitude.toInt()
                    mLong = (mLastLocation)!!.longitude.toInt()

                    CoroutineScope(coroutineContext).launch {
                        setupObserver(
                            mLat,
                            mLong
                        )
                    }

                } else {
                    showMessage(getString(R.string.no_location_detected))
                }
            }
    }

    private fun initView() {
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun showMessage(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }

    private fun showSnackBar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

        Toast.makeText(this@MainActivity, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    private fun setupObserver(
        lat: Int, lon: Int
    ) {
        viewModel.getWeatherData(lat, lon).observe(this, {

            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.body()?.list?.let { it1 ->
                        arrayList = it1
                        setAdapterData(arrayList)
                    }
                    recyclerView?.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this,
                        it.message!!, Toast.LENGTH_LONG
                    ).show()
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE

                }
            }
        })
    }

    private fun setAdapterData(dataList: List<DataList>) {
        adapter = WeatherAdapter(dataList)
        recyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = adapter
    }


    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
}