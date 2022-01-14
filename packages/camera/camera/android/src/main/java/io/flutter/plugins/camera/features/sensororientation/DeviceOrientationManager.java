// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.camera.features.sensororientation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import io.flutter.embedding.engine.systemchannels.PlatformChannel;
import io.flutter.embedding.engine.systemchannels.PlatformChannel.DeviceOrientation;
import io.flutter.plugins.camera.DartMessenger;

/**
 * Support class to help to determine the media orientation based on the orientation of the device.
 */
public class DeviceOrientationManager extends OrientationManager {

    private static final IntentFilter orientationIntentFilter =
            new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);

    private PlatformChannel.DeviceOrientation lastOrientation;
    private BroadcastReceiver broadcastReceiver;

    protected DeviceOrientationManager(
            @NonNull Activity activity,
            @NonNull DartMessenger messenger,
            boolean isFrontFacing,
            int sensorOrientation) {
        super(activity, messenger, isFrontFacing, sensorOrientation);
    }

    /**
     * Starts listening to the device's sensors or UI for orientation updates.
     *
     * <p>When orientation information is updated the new orientation is send to the client using the
     * {@link DartMessenger}. This latest value can also be retrieved through the {@link
     * #getVideoOrientation()} accessor.
     *
     * <p>If the device's ACCELEROMETER_ROTATION setting is enabled the {@link
     * DeviceOrientationManager} will report orientation updates based on the sensor information. If
     * the ACCELEROMETER_ROTATION is disabled the {@link DeviceOrientationManager} will fallback to
     * the deliver orientation updates based on the UI orientation.
     */
    public void start() {
        if (broadcastReceiver != null) {
            return;
        }
        broadcastReceiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        handleUIOrientationChange();
                    }
                };
        activity.registerReceiver(broadcastReceiver, orientationIntentFilter);
        broadcastReceiver.onReceive(activity, null);
    }

    /**
     * Stops listening for orientation updates.
     */
    public void stop() {
        if (broadcastReceiver == null) {
            return;
        }
        activity.unregisterReceiver(broadcastReceiver);
        broadcastReceiver = null;
    }
}
