package com.example.hongu.apaapa;

/**
 * Created by hongu on 2018/05/17.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hongu on 2018/05/15.
 */

public class SensorTab extends Fragment{

    private GraphView speed, rpm, ultsonic, power;

    private TextSensorViewThread mTextSensorViewThread;//テキスト形式のUI用スレッド

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_tab,container,false);

        speed = (GraphView) view.findViewById(R.id.speed);
        rpm = (GraphView) view.findViewById(R.id.rpm);
        ultsonic = (GraphView) view.findViewById(R.id.ult);
        power = (GraphView) view.findViewById(R.id.power);

        return view;
    }


}

public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

    @Override
    protected void onPreExecute() {
        //バックグラウンド処理開始前にUIスレッドで実行される。
        //ダイアログの生成などを行う。
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        //バックグラウンドで処理させる内容をここで記述。
        //AsyncTaskを使うにあたって、このメソッドの中身は必ず記述しなければいけない。
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //doInBackgroundの実行中にUIスレッドで実行される。
        //引数のvaluesを使ってプログレスバーの更新などをする際は、ここに記述する。
    }

    @Override
    protected void onPostExecute(Integer result) {
        //doInBackgroundが終了するとUIスレッドで実行される。
        //ダイアログの消去などを行う。
        //doInBackgroundの結果を画面表示に反映させる処理もここに記述。
    }
}

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