// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.camera.features.sensororientation;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

import android.app.Activity;
import android.view.OrientationEventListener;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.systemchannels.PlatformChannel.DeviceOrientation;
import io.flutter.plugins.camera.DartMessenger;

/**
 * Support class to help to determine the media orientation based on the orientation of the device.
 */
public class SensorOrientationManager extends OrientationManager {
    private OrientationEventListener orientationEventListener;
    private int currentOrientation = SCREEN_ORIENTATION_UNSPECIFIED;

    protected SensorOrientationManager(
            @NonNull Activity activity,
            @NonNull DartMessenger messenger,
            boolean isFrontFacing,
            int sensorOrientation) {
        super(activity, messenger, isFrontFacing, sensorOrientation);
    }

    private void handleSensorOrientationChange(DeviceOrientation orientation) {
        handleOrientationChange(orientation, lastOrientation, messenger);
        if (!orientation.equals(lastOrientation)) {
            messenger.sendDeviceOrientationChangeEvent(orientation);
        }
        lastOrientation = orientation;
    }

    private DeviceOrientation convertAngle(int angle) {
        if ((currentOrientation == 0 && (angle >= 300 || angle <= 60)) ||
                (currentOrientation == 1 && (angle >= 30 && angle <= 150)) ||
                (currentOrientation == 2 && (angle >= 120 && angle <= 240)) ||
                (currentOrientation == 3 && (angle >= 210 && angle <= 330))) {
        } else {
            currentOrientation = (angle + 45) % 360 / 90;
        }

        if (currentOrientation == 0) {
            return DeviceOrientation.PORTRAIT_UP;
        } else if (currentOrientation == 1) {
            return DeviceOrientation.LANDSCAPE_RIGHT;
        } else if (currentOrientation == 2) {
            return DeviceOrientation.PORTRAIT_DOWN;
        } else if (currentOrientation == 3) {
            return DeviceOrientation.LANDSCAPE_LEFT;
        } else {
            return DeviceOrientation.PORTRAIT_UP;
        }
    }

    public void start() {
        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int angle) {
                handleSensorOrientationChange(convertAngle(angle));
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        }
    }

    /**
     * Stops listening for orientation updates.
     */
    public void stop() {
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            orientationEventListener = null;
        }
    }
}