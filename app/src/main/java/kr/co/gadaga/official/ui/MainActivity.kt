package kr.co.gadaga.official.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.View
import android.view.animation.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.page_drawer.*
import kr.co.gadaga.official.R
import kr.co.gadaga.official.internal.addFragment
import kr.co.gadaga.official.ui.drawer.cart.CartFragment
import kr.co.gadaga.official.ui.drawer.liked.LikedFragment
import kr.co.gadaga.official.ui.drawer.order.OrderFragment
import kr.co.gadaga.official.ui.drawer.review.ReviewsFragment
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mNaverMap: NaverMap

    private var mCurrentPosition = 0

    private val handler = Handler()
    private val update = object: Runnable {
        override fun run() {
            if(mCurrentPosition >= MAIN_TEXT_LIST.size-1) mCurrentPosition = 0
            else mCurrentPosition += 1

            textSwitcherMain.setText(MAIN_TEXT_LIST[mCurrentPosition])

            handler.postDelayed(this, 3000)
        }
    }

    private var isExpanded = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar
        setSupportActionBar(toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // naver map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentMain) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.mapFragmentMain, it).commit()
            }
        mapFragment.getMapAsync(this)

        // views
        init()
    }

    /* 네이버 맵 */
    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap.also {
            it.uiSettings.apply {
                isCompassEnabled = false
                isScaleBarEnabled = false
                isRotateGesturesEnabled = false
                isZoomControlEnabled = false
                /* 현재위치 */
                isLocationButtonEnabled = false
            }

            /* 현재 위치 */
            if(checkLocationPermission().not()) requestLocationPermission()
            else requestLocationUpdates()
        }
    }

    /* 뷰 */
    private fun init() {
        // title text switcher
        textSwitcherMain.setFactory {
            val textView = TextView(this@MainActivity, null, 0, R.style.TitleStyle)
            // style
            textView
        }

        textSwitcherMain.setText(MAIN_TEXT_LIST[mCurrentPosition])

        textSwitcherMain.inAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_in)
        textSwitcherMain.outAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_slide_out)

        handler.postDelayed(update, 500)

        // 드로어 버튼 클릭
        drawerMenuButtonMain.setOnClickListener {
            drawerForegroundMain.visibility = View.VISIBLE

            rootLayoutMain.apply {
                ConstraintSet().also {
                    it.clone(this)
                    it.setGuidelinePercent(drawerStartGuidelineMain.id, 0f)
                    it.setGuidelinePercent(drawerEndGuidelineMain.id, .8f)
                    it.applyTo(this)

                    TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                        this.interpolator = interpolator
                        duration = 500
                    })
                }
            }

            drawerForegroundMain.animate().alpha(1f).setDuration(500).start()
        }

        drawerForegroundMain.setOnClickListener {
            rootLayoutMain.apply {
                ConstraintSet().also {
                    it.clone(this)
                    it.setGuidelinePercent(drawerStartGuidelineMain.id, -.8f)
                    it.setGuidelinePercent(drawerEndGuidelineMain.id, 0f)
                    it.applyTo(this)

                    TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                        this.interpolator = interpolator
                        duration = 500
                    })
                }
            }

            drawerForegroundMain.animate().alpha(0f).setDuration(500).withEndAction { drawerForegroundMain.visibility = View.GONE }.start()
        }

        drawerPageMain.setOnClickListener {
            Toast.makeText(this, "드로어 페이지 클릭", Toast.LENGTH_SHORT).show()
        }

        // 주변 매장 목록
        listContainerMain.setOnClickListener {
            isExpanded = if(isExpanded) {
                collapse()
                false
            } else {
                expand()
                true
            }
        }

        // 검색 버튼
        listSearchMain.setOnClickListener {
            if(isExpanded) Toast.makeText(this, "검색 버튼 클릭", Toast.LENGTH_SHORT).show()
            else {
                expand()
                isExpanded = true
            }
        }

        // 필터 버튼
        listFilterMain.setOnClickListener {
            if(isExpanded) Toast.makeText(this, "필터 버튼 클릭", Toast.LENGTH_SHORT).show()
            else {
                expand()
                isExpanded = true
            }
        }

        // 메뉴
        val drawerMenuOnClickListener = View.OnClickListener { view ->
            when(view.id) {
                R.id.cart -> addFragment(R.id.pageFragmentContainerMain, CartFragment.newInstance())
                R.id.order -> addFragment(R.id.pageFragmentContainerMain, OrderFragment.newInstance())
                R.id.liked -> addFragment(R.id.pageFragmentContainerMain, LikedFragment.newInstance())
                R.id.reviews -> addFragment(R.id.pageFragmentContainerMain, ReviewsFragment.newInstance())
            }

            rootLayoutMain.apply {
                ConstraintSet().also {
                    it.clone(this)
                    it.setGuidelinePercent(drawerStartGuidelineMain.id, -.8f)
                    it.setGuidelinePercent(drawerEndGuidelineMain.id, 0f)
                    it.setGuidelinePercent(pageStartGuidelineMain.id, 0f)
                    it.setGuidelinePercent(pageEndGuidelineMain.id, 1f)
                    it.applyTo(this)

                    TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                        this.interpolator = AccelerateDecelerateInterpolator()
                        this.duration = 500L
                    })
                }
            }
        }

        cart.setOnClickListener(drawerMenuOnClickListener)
        order.setOnClickListener(drawerMenuOnClickListener)
        liked.setOnClickListener(drawerMenuOnClickListener)
        reviews.setOnClickListener(drawerMenuOnClickListener)
    }

    /* 리스트 확장 */
    private fun expand() {
        /* 확장 */
        val interpolator = AccelerateDecelerateInterpolator()
        val duration = 500L

        /* 화살표 회전, 검색&필터 아이콘 나타남 */
        ObjectAnimator.ofFloat(listArrowMain, "rotation", 180f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(listFilterMain, "alpha", 1f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(listSearchMain, "alpha", 1f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 리스트 올라오기 */
        rootLayoutMain.apply {
            /* map */
            ConstraintSet().also {
                it.clone(this)
                it.setGuidelinePercent(listGuidelineTopMain.id, .5f)
                it.setGuidelinePercent(listGuidelineBottomMain.id, 1f)
                it.applyTo(this)

                TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                    this.interpolator = interpolator
                    this.duration = duration
                })
            }
        }

        /* 드로어 메뉴 버튼 상단으로 일부 이동 */
        val y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics)
        ObjectAnimator.ofFloat(drawerMenuButtonMain, "translationY", -y).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 검색바 숨기기 */
        AnimationSet(true).also { set ->
            set.interpolator = interpolator
            set.isFillEnabled = true
            set.fillAfter = true

            set.addAnimation(
                TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.RELATIVE_TO_PARENT, -2f, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, -y)
                    .also { it.duration = duration }
            )

            set.addAnimation(
                AlphaAnimation(1f, 0f).also { it.duration = 300 }
            )

            searchBarContainerMain.startAnimation(set)
        }

        /* 툴바, 타이틀 상단으로 이동 */
        val heightToolbar = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72f, resources.displayMetrics)
        ObjectAnimator.ofFloat(toolbarMain, "translationY", -heightToolbar).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(titleContainerMain, "translationY", -heightToolbar).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 타이틀 애니메이션 일시정지 */
        handler.removeCallbacks(update)
    }

    /* 리스트 축소 */
    private fun collapse() {
        /* 확장 */
        val interpolator = AccelerateDecelerateInterpolator()
        val duration = 500L

        /* 화살표 재회전, 검색&필터 아이콘 사라짐 */
        ObjectAnimator.ofFloat(listArrowMain, "rotation", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(listFilterMain, "alpha", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(listSearchMain, "alpha", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 리스트 내려가기 */
        rootLayoutMain.apply {
            /* map */
            ConstraintSet().also {
                it.clone(this)
                it.setGuidelinePercent(listGuidelineTopMain.id, 1f)
                it.setGuidelinePercent(listGuidelineBottomMain.id, 1.5f)
                it.applyTo(this)

                TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                    this.interpolator = interpolator
                    this.duration = duration
                })
            }
        }

        /* 검색바 원래 위치로 나타남 */
        val y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics)
        AnimationSet(true).also { set ->
            set.interpolator = interpolator
            set.isFillEnabled = true
            set.fillAfter = true

            set.addAnimation(
                TranslateAnimation(Animation.RELATIVE_TO_PARENT, -2f, Animation.RELATIVE_TO_PARENT, 0f, Animation.ABSOLUTE, -y, Animation.ABSOLUTE, 0f)
                    .also {
                        it.duration = duration
                        it.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {}

                            override fun onAnimationEnd(animation: Animation?) {}

                            override fun onAnimationStart(animation: Animation?) {
                                Handler().postDelayed({ searchBarContainerMain.animate().alpha(1f).start() }, 300)
                            }
                        })
                    }
            )

            searchBarContainerMain.startAnimation(set)
        }

        /* 드로어 메뉴 버튼 원래 위치로 이동 */
        ObjectAnimator.ofFloat(drawerMenuButtonMain, "translationY", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 툴바, 타이틀 하단으로 다시 이동 */
        ObjectAnimator.ofFloat(toolbarMain, "translationY", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        ObjectAnimator.ofFloat(titleContainerMain, "translationY", 0f).also {
            it.interpolator = interpolator
            it.duration = duration
        }.start()

        /* 타이틀 애니메이션 재시작 */
        handler.postDelayed(update, duration)
    }

    /* 위치 */
    private fun checkLocationPermission(): Boolean = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(android.R.string.ok),
                Snackbar.LENGTH_INDEFINITE
            ).also {
                it.setAction(0) { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE) }
            }.show()
        }
        else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            when {
                // 취소
                grantResults.isEmpty() -> Any()

                // 승인
                // approved -> map ready -> isApproved -> getLastLocation()
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> requestLocationUpdates()

                // 거부
                else -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "위치 정보 사용 거부, 종료 또는 허가 요청",
                        Snackbar.LENGTH_INDEFINITE
                    ).also {
                        it.setAction(0) {
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", "kr.co.gadaga.official", null)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }.let { startActivity(it) }
                        }
                    }.show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest().also {
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                locationResult?.lastLocation?.apply {
                    val currentLocation = LatLng(latitude, longitude)

                    /* 마커 추가 */
                    Marker(currentLocation).also {
                        it.iconTintColor = Color.LTGRAY
                        it.map = mNaverMap
                    }

                    /* 카메라 이동 */
                    mNaverMap.moveCamera(CameraUpdate.scrollTo(currentLocation).animate(CameraAnimation.Easing))

                    /* 좌표 주소 반환 */
                    val maxResults = 1

                    var address = ""
                    Geocoder(this@MainActivity, Locale.getDefault())
                        .getFromLocation(latitude, longitude, maxResults)[0]
                        .getAddressLine(0)
                        .toString()
                        .split(" ")
                        .drop(3)
                        .forEach { address += "$it " }

                    departureTextViewMain.text = address
                }
                    ?: getLastLocation()
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                /* 마커 추가 */
                val currentLocation = LatLng(location.latitude, location.longitude)
                val marker = Marker(currentLocation)
                marker.map = mNaverMap

                /* 카메라 이동 */
                mNaverMap.moveCamera(CameraUpdate.scrollTo(currentLocation).animate(CameraAnimation.Easing))

                /* 좌표 주소 반환 */
                val maxResults = 7

                var address = ""
                Geocoder(this, Locale.getDefault())
                    .getFromLocation(location.latitude, location.longitude, maxResults)[0]
                    .getAddressLine(0)
                    .toString()
                    .split(" ")
                    .drop(3)
                    .forEach { address += "$it " }

                departureTextViewMain.text = address

            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag("TAG")?.let {
            rootLayoutMain.apply {
                ConstraintSet().also {
                    it.clone(this)
                    it.setGuidelinePercent(pageStartGuidelineMain.id, 1f)
                    it.setGuidelinePercent(pageEndGuidelineMain.id, 2f)
                    it.applyTo(this)

                    TransitionManager.beginDelayedTransition(this, ChangeBounds().apply {
                        interpolator = AccelerateDecelerateInterpolator()
                        duration = 500L
                        layoutAnimationListener = object: Animation.AnimationListener {
                            override fun onAnimationRepeat(animation: Animation?) {}

                            override fun onAnimationEnd(animation: Animation?) { supportFragmentManager.popBackStack() }

                            override fun onAnimationStart(animation: Animation?) {}
                        }
                    })
                }
            }
        }
            ?: super.onBackPressed()
    }



    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 343
        val MAIN_TEXT_LIST = listOf("회사로", "집으로", "학교로", "어디든")
    }
}
