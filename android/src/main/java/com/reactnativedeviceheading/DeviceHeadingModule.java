package com.reactnativedeviceheading;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

//import com.facebook.react.bridge.Callback;

public class DeviceHeadingModule extends SimpleViewManager<View> {

  public static final String REACT_CLASS = "RCTDeviceHeading";
  ReactApplicationContext mCallerContext;

  private int mAzimuth = 0; // degree
  private int mFilter = 5;
  private SensorManager mSensorManager;
  private Sensor mSensor;
  private float[] orientation = new float[3];
  private float[] rMat = new float[9];

  public DeviceHeadingModule(ReactApplicationContext reactContext) {
    mCallerContext = reactContext;
  }

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  public View createViewInstance(@NonNull ThemedReactContext context) {
    View v = new View(context);
    v.setBackgroundColor(Color.RED);
    onReceiveNativeEvent(context, v);
    return v;
  }

  public void onReceiveNativeEvent(final ThemedReactContext reactContext, final View view) {
    if (mSensorManager == null) {
      mSensorManager = (SensorManager) mCallerContext.getSystemService(Context.SENSOR_SERVICE);
    }

    if (mSensor == null) {
      mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    boolean started = mSensorManager.registerListener(new SensorEventListener() {
      @Override
      public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
          return;
        }

        // calculate th rotation matrix
        SensorManager.getRotationMatrixFromVector(rMat, event.values);
        // get the azimuth value (orientation[0]) in degree
        // degree
        int newAzimuth = (int) ((((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360) -
          (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[2]))) + 360) % 360;
        // don't react to changes smaller than the filter value
        if (Math.abs(mAzimuth - newAzimuth) < mFilter) {
          return;
        }

        WritableMap rEvent = Arguments.createMap();
        rEvent.putInt("heading", newAzimuth);
        reactContext
          .getJSModule(RCTEventEmitter.class)
          .receiveEvent(view.getId(), "topChange", rEvent);

        mAzimuth = newAzimuth;
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {

      }
    }, mSensor, SensorManager.SENSOR_DELAY_UI);
  }
}
