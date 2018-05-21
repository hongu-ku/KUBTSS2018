package com.example.hongu.apaapa;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

//import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.*;
import java.util.jar.Manifest;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks , OnConnectionFailedListener,
        LocationListener, SensorEventListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogeleApiClient;

    //    ここはKUBTSS2017_textの内容
    private static final String TAG = MapsActivity.class.getSimpleName();
    private ReceivedDataAdapter mReceivedDataAdapter;
    private SensorAdapter mSensorAdapter;
//    private TextSensorViewThread mTextSensorViewThread;//テキスト形式のUI用スレッド
    //private Sound sound;
    private double atmLapse, atmStandard;

    private String url;
    private CloudLoggerService mCloudLoggerService = null;
    private CloudLoggerAdapter mCloudLoggerAdapter;
    private InetAddress inetAddress;
//    private CloudLoggerSendThread mCloudLoggerSendThread;


    boolean runflg = true;
    private static final int INTERVAL = 1000;
    private static final int FASTESTINTERVAL = 16;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STRAGE = 2;
    private static final int ADDRESSLOADER_ID = 0;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(INTERVAL)                      //位置情報の更新間隔をミリ秒で指定
            .setFastestInterval(FASTESTINTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);     //位置情報取得要求の優先順位
    private FusedLocationProviderApi mFusedLocationProviderApi = LocationServices.FusedLocationApi;
    private List<LatLng> mRunList = new ArrayList<LatLng>();
    private long mStartTimeMillis;
    private double mMeter = 0.0;
    private double StraightMeter = 0.0;
    private double mElapsedTime = 0.0;
    private double mSpeed = 0.0;
    //private DatabaseHelper mDbHelper;
    private boolean mStart = false;
    private boolean mFirst = false;
    private boolean mStop = false;
    private boolean mAsked = false;
    private Chronometer mChronometer;
    private int f = 0;
    private int i = 0;
    private int val = 1;

    private double Kyotolat = 35.025874;
    private double Kyotolnt = 135.780865;
//    SensorManager sensorManager;
    float[] rotationMatrix = new float[9];
    float[] gravity = new float[3];
    float[] geomagnetic = new float[3];
    float[] attitude = new float[3];
    TestView testView;
    LatLng latlng;
    LatLng Platform = new LatLng(35.294170,136.254422);
    CircleOptions circleOptions;
    CircleOptions circleOptions1;
    CircleOptions currentCircle;

    float Pitchneu = 0;
    float Rollneu = 0;
    float[] save = new float[3];

    MarkerOptions options = new MarkerOptions();


    double roll,switching,yaw,pitch,ultsonic;

//    private SensorManager mSensorManager = null;
    private SensorEventListener mSensorEventListener = null;

    public float[] getfAttitude() {
        return fAttitude;
    }

    public void setfAttitude(float[] fAttitude) {
        this.fAttitude = fAttitude;
    }

    float[] fAttitude = new float[3];
    float[] oridinalAttitude = new float[3];

    private float[] fAccell = null;
    private float[] fMagnetic = null;

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;


    //SubThreadSample[] subThreadSample = new SubThreadSample[50];

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //メンバ変数が初期化されることへの対処
        outState.putBoolean("ASKED", mAsked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAsked = savedInstanceState.getBoolean("ASKED");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mGoogeleApiClient.connect();
//        sensorManager.registerListener( this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//        sensorManager.registerListener( this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
//        String lapseStr = pref.getString(SettingPrefActivity.PREF_KEY_LAPSE, "0.12");
//        String standardStr = pref.getString(SettingPrefActivity.PREF_KEY_STANDARD, "1013.25");
//        atmLapse = Double.parseDouble(lapseStr);
//        atmStandard = Double.parseDouble(standardStr);

        mTextSensorViewThread.setPressureParam(atmStandard, atmLapse);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("start");
//        mSensorManager.registerListener(
//                mSensorEventListener,
//                mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
//                SensorManager.SENSOR_DELAY_UI );
//        mSensorManager.registerListener(
//                mSensorEventListener,
//                mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ),
//                SensorManager.SENSOR_DELAY_UI );
    }

    @Override
    protected void onStop() { // ⇔ onStart
        super.onStop();

//        mSensorManager.unregisterListener( mSensorEventListener );
    }

    @Override
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        setContentView(R.layout.page_fragment);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



//        initSensor();
        testView = (TestView) findViewById(R.id.view5);
        final DirectionView directionView = (DirectionView) findViewById(R.id.direction);
        final TextView disText = (TextView) findViewById(R.id.textview);
        final TextView straightText = (TextView) findViewById(R.id.textview1);
//        disText.setTextColor(Color.RED);
//        straightText.setTextColor(Color.RED);
        // NumberPicker 設定
//        final NumberPicker numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
//        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

// 最大、最小を設定
//        numberPicker.setMaxValue(40);
//        numberPicker.setMinValue(1);
//        numberPicker.setValue(val);

// 値を取得
        //val = numberPicker.getValue();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // スリープ抑制

//        mGoogeleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();



//        if (Build.VERSION.SDK_INT >= 19) {
//            Log.i(TAG, "getExternalFilesDirを呼び出します");
//            File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
//            File extSdDir = extDirs[extDirs.length - 1];
//            Logger.setExternalDir(extSdDir);
//            Log.i(TAG, "getExternalFilesDirが返すパス: " + extSdDir.getAbsolutePath());
//        }else{
//            Log.e(TAG, "This SDK version is under 18.");
//            finish();
//        }

       mReceivedDataAdapter = new ReceivedDataAdapter(getBaseContext());

        //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Configuration config = getResources().getConfiguration();
//        mSensorAdapter = new SensorAdapter(sensorManager, mLocationManager, mReceivedDataAdapter, config);

        //  sound = new Sound(getApplicationContext(), R.drawable.warn05);

        mTextSensorViewThread = new TextSensorViewThread(mSensorAdapter, mReceivedDataAdapter);
        mTextSensorViewThread.start();
        mReceivedDataAdapter.setReconnection(true);
//        Switch connectSwitch = (Switch) findViewById(R.id.reConnectSwitch);
//        connectSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mReceivedDataAdapter.setReconnection(isChecked);
//            }
//        });

        if (mCloudLoggerService == null) {
            url = "http://yukiku.php.xdomain.jp/controller.php";
            mCloudLoggerService = new CloudLoggerService(url);
        }
        mCloudLoggerAdapter = new CloudLoggerAdapter(mSensorAdapter,mReceivedDataAdapter,mCloudLoggerService);
//        mCloudLoggerSendThread = new CloudLoggerSendThread(mCloudLoggerService);
//        mCloudLoggerSendThread.start();



        //   subThreadSample[0] = new SubThreadSample("a", 100, 100);

//        System.out.println("ok");
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        final Button startbtn = (Button) findViewById(R.id.startbtn);
//
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                val = newVal;
//                System.out.println("debug:"+val);
//            }
//        });

//        tb.setChecked(false);

        //ボタンが押された時の動き
        //TODO: ボタンを小さくして右上に表示
//        startbtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                if (f == 0) {
//                    startChronometer();
//                    //   subThreadSample[i].start();
//                    mStart = true;
//                    mFirst = true;
//                    mStop = false;
//                    mMeter = 0.0;
//                    mRunList.clear();
//                    f++;
//                    i=val;
//                    //val = numberPicker.getValue();
//                    // TODO: NumberPickerの廃止
//                    mCloudLoggerAdapter.setCount(val);
//                    startbtn.setText("STOP");
//                    Toast.makeText(getApplicationContext(),""+val, Toast.LENGTH_SHORT).show();
//                } else if (f == 1) {
//                    stopChronometer();
//                    mStop = true;
//                    mStart = false;
//                    // [i].stopRunning();
//                    f=0;
//                    i = 0;
//                    mCloudLoggerAdapter.setCount(0);
//                    val++;
////                    numberPicker.setValue(val);
//                    startbtn.setText("START");
//                    disText.setText("Distance:");
//                    straightText.setText("Straight:");
//                }
//            }
//        });
//
//
//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE );
//
//        for (int i = 0; i < 3; i++)
//            save[i] = 0;

        mSensorEventListener = new SensorEventListener()
        {
            public void onSensorChanged (SensorEvent event) {
                // センサの取得値をそれぞれ保存しておく
//                switch( event.sensor.getType()) {
//                    case Sensor.TYPE_ACCELEROMETER:
//                        fAccell = event.values.clone();
//                        break;
//                    case Sensor.TYPE_MAGNETIC_FIELD:
//                        fMagnetic = event.values.clone();
//                        break;
//                }

                // fAccell と fMagnetic から傾きと方位角を計算する
                //if( fAccell != null && fMagnetic != null ) {
//                    // 回転行列を得る
//                    float[] inR = new float[9];
//
//                    float deg = testView.getPitch1();
//                    double rad = Math.toRadians(deg);
//
//                    float sin = (float) Math.sin(rad);
//                    float cos = (float) Math.cos(rad);
//
//                    rot[0] = 1;
//                    rot[1] = 0;
//                    rot[2] = 0;
//                    rot[3] = 0;
//                    rot[4] = cos;
//                    rot[5] = -sin;
//                    rot[6] = 0;
//                    rot[7] = sin;
//                    rot[8] = cos;
//
//
//
//                    SensorManager.getRotationMatrix(
//                            inR,
//                            null,
//                            fAccell,
//                            fMagnetic );
//
//                    float[] ininR = new float[9];
//
//                    // ワールド座標とデバイス座標のマッピングを変換する
//                    float[] outR = new float[9];
//                    SensorManager.remapCoordinateSystem(
//                            inR,
//                            SensorManager.AXIS_X,  // デバイスx軸が地球の何軸になるか
//                            SensorManager.AXIS_Z,  // デバイスy軸が地球の何軸になるか
//                            outR );
//                    // 姿勢を得る
//                    // 回転行列をoutRにかける
//                    MatrixMultiply(outR, rot, 3, ininR);
//
//                    SensorManager.getOrientation(
//                            mSensorAdapter.getIninR(),
//                            fAttitude );
//
//                   SensorManager.getOrientation(
//                            mSensorAdapter.getOutR(),
//                            oridinalAttitude );


//                    String buf =
//                            "---------- Orientation --------\n" +
//                                    String.format( "方位角\n\t%f\n", rad2deg( fAttitude[0] )) +
//                                    String.format( "前後の傾斜\n\t%f\n", rad2deg( fAttitude[1] )) +
//                                    String.format( "左右の傾斜\n\t%f\n", rad2deg( fAttitude[2] ));
//
//                    String buf2 =
//                            "---------- fAccell --------\n" +
//                                    String.format( "方位角\n\t%f\n", rad2deg( fAccell[0] )) +
//                                    String.format( "前後の傾斜\n\t%f\n", rad2deg( fAccell[1] )) +
//                                    String.format( "左右の傾斜\n\t%f\n", rad2deg( fAccell[2] ));
//
//                    String buf3 =
//                            "---------- fMagnetic --------\n" +
//                                    String.format( "方位角\n\t%f\n", rad2deg( fMagnetic[0] )) +
//                                    String.format( "前後の傾斜\n\t%f\n", rad2deg( fMagnetic[1] )) +
//                                    String.format( "左右の傾斜\n\t%f\n", rad2deg( fMagnetic[2] ));

//                    setValue(mSensorAdapter.getYaw(), mSensorAdapter.getRoll(), mSensorAdapter.getPitch());
//                    String bush =
//                              "---------- Orientation --------\n" +
//                            String.format( "Yaw:\n\t%f\n", yaw) +
//                            String.format( "Roll:\n\t%f\n", roll) +
//                            String.format( "Pitch:\n\t%f\n", pitch);


                    //TextView t = (TextView) findViewById( R.id.textview);
//                    TextView Accell = (TextView) findViewById(R.id.textview1);
//                    TextView Magnetic = (TextView) findViewById(R.id.textview2);
                    //t.setText( buf );
//                    Accell.setText(buf2);
//                    Magnetic.setText(buf3);
                    //float Yaw = fAttitude[0];

                    // 正面に置く場合
                    testView.setYaw(rad2deg( fAttitude[2] ));
                    testView.setPitch(rad2deg( fAttitude[1] ));

//                    // 左に置く場合
//                    testView.Yaw = -rad2deg( fAttitude[1] );
//                    testView.Pitch = rad2deg( fAttitude[2] );

//                    System.out.println("testviewはok?");
                    // 再描画
                    testView.invalidate();
                    directionView.setYaw(rad2deg(fAttitude[0]));
                    directionView.invalidate();
            }
            public void onAccuracyChanged (Sensor sensor, int accuracy) {}
        };


//        testView.setOnLongClickListener(new View.OnLongClickListener() {
//            public boolean onLongClick(View v) {
//                //testView.setPitch1(rad2deg(oridinalAttitude[1]));
//                Pitchneu = oridinalAttitude[1];
//                Rollneu = oridinalAttitude[2];
//                mSensorAdapter.setPitchneutral(Pitchneu);
//                // TODO:ロールのニュートラル調整がボタンでできてしまっていいのだろうか
//                mSensorAdapter.setRollneutral(Rollneu);
//                System.out.println("debug " + testView.getPitch1());
//                testView.invalidate();
//                System.out.println("oridinal = " + oridinalAttitude[2]);
//                System.out.println("f = " + fAttitude[2]);
//                return true; //trueの場合はonClickListenerを返さない？
//            }
//        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new SensorTab(), "TAB1");
        adapter.addFragment(new MapTab(), "TAB2");
        viewPager.setAdapter(adapter);
    }

//    public void MatrixMultiply(float[] R, float[] L,int sizeR/*正方行列の次元*/, float[] outM) {
//        for (int j=0; j<sizeR*sizeR; j++)
//            outM[j] = 0;
//        for (int k=0; k<sizeR; k++) {
//            for (int i = 0; i < sizeR*sizeR; i++) {
//                outM[i] +=R[(i/sizeR) * sizeR + k] *L[i%sizeR + sizeR*k] ;
//            }
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(MapsActivity.this, SettingPrefActivity.class);
//            startActivity(intent);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private class TextSensorViewThread extends Thread {
        SensorAdapter mSensorAdapter;
        ReceivedDataAdapter mReceivedDataAdapter;
        Handler handler = new Handler(Looper.getMainLooper());

        private TextView txtStatus, txtSelector;
        GraphView speed = (GraphView) findViewById(R.id.speed);
        GraphView rpm = (GraphView) findViewById(R.id.rpm);
        GraphView ult = (GraphView) findViewById(R.id.ult);
        TextView elevator = (TextView) findViewById(R.id.elevator);
        TextView rudder = (TextView) findViewById(R.id.rudder);
        TextView trim = (TextView) findViewById(R.id.trim);


        private boolean running = true;

        private double atmStandard, atmLapse;

        public TextSensorViewThread(SensorAdapter mSensorAdapter, ReceivedDataAdapter mReceivedDataAdapter) {
            this.mSensorAdapter = mSensorAdapter;
            this.mReceivedDataAdapter = mReceivedDataAdapter;

            txtStatus = (TextView) findViewById(R.id.textViewStatus);
        }

        public void start() {
            new Thread(this).start();
            System.out.println("start");
        }

        public void stopRunning() {
            running = false;
        }

        public void setPressureParam(double atmStandard, double atmLapse) {
            this.atmStandard = atmStandard;
            this.atmLapse = atmLapse;
        }

        @Override
        public void run() {
            System.out.println("TextSensorViewthread Start");
            try {
                Thread.sleep(100);
                System.out.println("1234567890");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            while (running) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        speed.setV(mReceivedDataAdapter.getAirspeed());
                        rpm.setV(mReceivedDataAdapter.getCadence());
                        ult.setV(mReceivedDataAdapter.getUltsonic());
                        speed.invalidate();
                        rpm.invalidate();
                        ult.invalidate();

                        elevator.setText("Elev: " + String.format("%.2f", mReceivedDataAdapter.getElevator()));//水平サーボの舵角
                        rudder.setText("Rud: " +String.format("%.2f", mReceivedDataAdapter.getRudder()));//垂直サーボの舵角
                        trim.setText("Trim: " +String.valueOf(mReceivedDataAdapter.getTrim()));//elevatorの舵角(ボタン)
//                〇      txtAirspeed.setText(String.format("%.2f", mReceivedDataAdapter.getAirspeed()) + "m/s");//気速
//                        txtCadence.setTextSize(100.0f);
//                〇        txtCadence.setText(String.format("%.2f", mReceivedDataAdapter.getCadence()) + "RPM");//足元回転数
//                〇        txtUltsonic.setText(String.format("%.2f", mReceivedDataAdapter.getUltsonic()));//超音波(200cmまで)
//                        txtAtmpress.setText(String.format("%.2f", mReceivedDataAdapter.getAtmpress()));//気圧(hPa)
                        double altitude = -(mReceivedDataAdapter.getAtmpress() - atmStandard) / atmLapse;
//                        txtAltitude.setText(String.format("%.2f", altitude));
                        switch (mReceivedDataAdapter.getState()) {
                            case BluetoothChatService.STATE_CONNECTED:
                                txtStatus.setText("Connected");
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                txtStatus.setText("Connecting...");
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                                txtStatus.setText("Listen");
                                break;
                            case BluetoothChatService.STATE_NONE:
                                txtStatus.setText("None");
                                break;
                        }
//                        txtSelector.setText(String.valueOf(mReceivedDataAdapter.getSelector()));
//                        txtCadencevolt.setText(String.valueOf(mReceivedDataAdapter.getCadencevolt()));
//                        txtUltsonicvolt.setText(String.valueOf(mReceivedDataAdapter.getUltsonicvolt()));
//                        txtServovolt.setText(String.valueOf(mReceivedDataAdapter.getServovolt()));


                        //sound.set(mSensorAdapter.getRoll(), 40, 60);
                    }
                });
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e(TAG, "ReConnectThread exception");
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private class CloudLoggerSendThread extends Thread{
        CloudLoggerService mCloudLoggerService;
        Handler handler = new Handler();
        private boolean running = true;
        public CloudLoggerSendThread(CloudLoggerService mCloudLoggerService){
            this.mCloudLoggerService = mCloudLoggerService;
        }
        public void start(){
            new Thread(this).start();
        }
        public void stopRunning() {
            running = false;
        }
        @Override
        public void run(){
            while(running) {
                while (i != 0) {
                    // TODO: EXEPTION !!!

                    try {
                        mCloudLoggerAdapter.setPitchneu(Pitchneu);
                        mCloudLoggerService.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "CloudLoggerSendThread exception");
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "CloudLoggerSendThread exception");
                }
            }
        }

    }

    private float rad2deg( float rad ) {
        return rad * (float) 180.0 / (float) Math.PI;
    }

//    protected void initSensor(){
//        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//    }

    @Override
    protected void onDestroy() {
        // TODO: Destroy時にアプリが落ちないようにする
        super.onDestroy();
//        System.out.println("スレッド終了");
//        mTextSensorViewThread.stopRunning();
//        System.out.println("1");
//        mCloudLoggerSendThread.stopRunning();
//        System.out.println("2");
//        //mCloudLoggerAdapter.stoplogger();
//        //sound.release();
//        System.out.println("3");
//        mReceivedDataAdapter.stop();
//        System.out.println("4");
//        mSensorAdapter.stopSensor();
//        System.out.println("5");
//        mCloudLoggerService.close();
//        System.out.println("6");
//        try{
//            wait(6000);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        System.out.println("END");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

//    private void startChronometer() {
//        mChronometer = (Chronometer) findViewById(R.id.chronometer);
//        mChronometer.setBase(SystemClock.elapsedRealtime());
//        mChronometer.start();
//        mStartTimeMillis = System.currentTimeMillis();
//    }
//
//    private void stopChronometer() {
//        mChronometer.stop();
//        //ミリ秒
//        mElapsedTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 長押しのリスナーをセット
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng longpushLocation) {
                try {
                    // TODO: 長押し時のzoom値の変更
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latlng).zoom(18).bearing(0).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (NullPointerException e) {
                    // do nothing
                    e.printStackTrace();
                }
            }
        });

        // MyLocationレイヤーを有効に
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(Platform));
        // MyLocationButtonを有効に
        UiSettings settings = mMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);

        settings.setCompassEnabled(true);
        //ズームイン・アウトボタンの有効化
        settings.setZoomControlsEnabled(true);
        //回転ジェスチャーの有効化
        settings.setRotateGesturesEnabled(true);
        //スクロールジェスチャーの有効化
        settings.setScrollGesturesEnabled(true);
        //Tlitジェスチャーの有効化
        settings.setTiltGesturesEnabled(true);
        //ズームジェスチャーの有効化
        settings.setZoomGesturesEnabled(true);

        //マップの種類
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        衛星写真
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        circleOptions = new CircleOptions()
        .center(Platform)
                .radius(10000)
                .strokeColor(Color.RED)
                .strokeWidth(4); // In meters

        circleOptions1 = new CircleOptions()
                .center(Platform)
                .radius(20000)
                .strokeColor(Color.RED)
                .strokeWidth(4); // In meters

        currentCircle = new CircleOptions()
                .center(Platform)
                .strokeColor(Color.GREEN)
                .strokeWidth(4);

// Get back the mutable Circle
        mMap.addCircle(circleOptions);
        mMap.addCircle(circleOptions1);

//             DangerousなPermissionはリクエストして許可をもらわないと使えない(Android6以降？)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // 一度拒否された時のダイアログ
//                new AlertDialog.Builder(this)
//                        .setTitle("許可が必要です")
//                        .setMessage("許可して")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
////                                showToastShort("GPS使えへんがな");
//                            }
//                        })
//                        .show();
//            } else {
//                // まだ許可を求める前の時、許可を求めるダイアログを表示する
//                //requestAccessFineLocation();
//            }
//        }
    }
//    private void requestAccessFineLocation() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // ユーザーが許可したとき
//                // 許可が必要な機能を改めて実行する
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //
//                } else {
//                    // ユーザーが許可しなかったとき
//                    // 許可されなかったため機能が実行できないことを表示する
//                    //showToastShort("GPS機能が使えないので地図は動きません");
//                    // 以下は、java.lang.RuntimeExceptionになる
//                    // mMap.setMyLocationEnabled(true)
//                }
//                return;
//            }
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STRAGE: {
//                // userが許可
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // saveConfirmDialog();
//                } else {
//                    // userが許可しない
//                    //showToastShort("外部へのファイルの保存が許可されなかったので、きろくできません");
//                }
//                return;
//            }
//        }
//    }
//
//
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mFusedLocationProviderApi.requestLocationUpdates(mGoogeleApiClient, REQUEST, (LocationListener) this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        // do nothing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // do nothing
    }

    @Override
    public void onLocationChanged(Location location) {
        float[] dista = new float[3];

        // Stop後は動かさない
        if (mStop) {
            return;
        }


        // マーカー設定
        mMap.clear();
        latlng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng Chikubu = new LatLng(35.423196, 136.144068);
        LatLng Oki = new LatLng(35.2079, 136.068244);

        options.position(latlng);
        // ランチャーアイコン
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.navi);
        options.icon(icon);
        mMap.addMarker(options
                .anchor(0.5f, 0.5f)
                .rotation(rad2deg( fAttitude[0] ) + 90));


        mMap.addCircle(circleOptions);
        mMap.addCircle(circleOptions1);

        mMap.addMarker(new MarkerOptions().position(Platform));

//        if (mStart) {
//            if (mFirst) {
//                // TODO: 初めのzoom値の変更
//                CameraPosition cameraposition = new CameraPosition.Builder()
//                        .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(18)
//                        .bearing(0).build();
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraposition));
//                Bundle args = new Bundle();
//                args.putDouble("lat", location.getLatitude());
//                args.putDouble("lon", location.getLongitude());
//                System.out.println("debug");
//                System.out.println(location.getLatitude());
//
//                //getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);
//                mFirst = !mFirst;
//            } else {
//                //移動線を描画
//                drawTrace(latlng);
//                //走行距離を累積
//                sumDistance();
//
//                // TODO: 竹生島と沖島への直線を描画
//                PolylineOptions OkiOptions = new PolylineOptions()
//                        .add(latlng)
//                        .add(Oki)
//                        .width(4);
//
//                PolylineOptions ChikubuOptions = new PolylineOptions()
//                        .add(latlng)
//                        .add(Chikubu)
//                        .width(4);
//
//                mMap.addPolyline(OkiOptions);
//                mMap.addPolyline(ChikubuOptions);
//
//                Location.distanceBetween(Platform.latitude,Platform.longitude, latlng.latitude, latlng.longitude, dista);
//                currentCircle.radius(dista[0]);
//                mMap.addCircle(currentCircle);
//
//            }
//        }
    }
//
//    private void drawTrace(LatLng latlng) {
//        mRunList.add(latlng);
//        if (mRunList.size() > 1) {
//            PolylineOptions polyOptions = new PolylineOptions();
//            for (LatLng polyLatLng : mRunList) {
//                polyOptions.add(polyLatLng);
//            }
//            polyOptions.color(Color.RED);
//            polyOptions.width(3.5f);
//            polyOptions.geodesic(false);
//            mMap.addPolyline(polyOptions);
//        }
//    }
//
//    private void sumDistance() {
//        if (mRunList.size() < 2) {
//            return;
//        }
//        // 累計距離
//        mMeter = 0.0;
//        float[] results = new float[3];
//        // Straight distance
//        StraightMeter = 0.0;
//        float[] straight = new float[3];
//
//
//        int i = 1;
//        while (i < mRunList.size()) {
//            results[0] = 0;
//            Location.distanceBetween(mRunList.get(i - 1).latitude, mRunList.get(i - 1).longitude,
//                    mRunList.get(i).latitude, mRunList.get(i).longitude, results);
//            Location.distanceBetween(mRunList.get(0).latitude, mRunList.get(0).longitude, mRunList.get(i).latitude, mRunList.get(i).longitude, straight);
//
//            mMeter += results[0];
//            StraightMeter = straight[0];
//
//            i++;
//        }
//        //distanceBetweenの距離はメートル単位
//        //double disMeter = mMeter / 1000;
//        TextView DisText = (TextView) findViewById(R.id.textview);
//        TextView StraightText = (TextView) findViewById(R.id.textview1);
////        DisText.setTextColor(Color.RED);
////        StraightText.setTextColor(Color.RED);
//        DisText.setText(String.format("Distance: " + "%.1f" + " m", mMeter));
//        StraightText.setText(String.format("Straight: " + "%.1f" + " m", StraightMeter));
//    }
//
    @Override
    public void onSensorChanged(SensorEvent event) {
//        switch(event.sensor.getType()){
//            case Sensor.TYPE_MAGNETIC_FIELD:
//                geomagnetic = event.values.clone();
//                break;
//            case Sensor.TYPE_ACCELEROMETER:
//                gravity = event.values.clone();
//                break;
//        }
//        if(geomagnetic != null && gravity != null){
//            SensorManager.getRotationMatrix( rotationMatrix, null, gravity, geomagnetic);
//            SensorManager.getOrientation( rotationMatrix, attitude);
////            YawText.setText(Integer.toString( (int)(attitude[0] * RAD2DEG)));
////            PitchText.setText(Integer.toString( (int)(attitude[1] * RAD2DEG)));
////            RollText.setText(Integer.toString( (int)(attitude[2] * RAD2DEG)));
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // do nothing
    }
}