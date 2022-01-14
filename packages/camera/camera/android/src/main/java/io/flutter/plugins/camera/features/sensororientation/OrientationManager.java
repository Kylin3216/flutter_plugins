package io.flutter.plugins.camera.features.sensororientation;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import io.flutter.embedding.engine.systemchannels.PlatformChannel;
import io.flutter.plugins.camera.DartMessenger;

public abstract class OrientationManager {
    protected final Activity activity;
    protected final DartMessenger messenger;
    protected final boolean isFrontFacing;
    protected final int sensorOrientation;
    protected PlatformChannel.DeviceOrientation lastOrientation;

    public static OrientationManager create(
            @NonNull Activity activity,
            @NonNull DartMessenger messenger,
            boolean isFrontFacing,
            int sensorOrientation) {
        if (isFrontFacing) {
            return new DeviceOrientationManager(activity, messenger, isFrontFacing, sensorOrientation);
        } else {
            return new SensorOrientationManager(activity, messenger, isFrontFacing, sensorOrientation);
        }
    }

    protected OrientationManager(
            @NonNull Activity activity,
            @NonNull DartMessenger messenger,
            boolean isFrontFacing,
            int sensorOrientation) {
        this.activity = activity;
        this.messenger = messenger;
        this.isFrontFacing = isFrontFacing;
        this.sensorOrientation = sensorOrientation;
    }

    public abstract void start();

    /**
     * Stops listening for orientation updates.
     */
    public abstract void stop();

    /**
     * Returns the device's photo orientation in degrees based on the sensor orientation and the last
     * known UI orientation.
     *
     * <p>Returns one of 0, 90, 180 or 270.
     *
     * @return The device's photo orientation in degrees.
     */
    public int getPhotoOrientation() {
        return this.getPhotoOrientation(this.lastOrientation);
    }

    /**
     * Returns the device's photo orientation in degrees based on the sensor orientation and the
     * supplied {@link PlatformChannel.DeviceOrientation} value.
     *
     * <p>Returns one of 0, 90, 180 or 270.
     *
     * @param orientation The {@link PlatformChannel.DeviceOrientation} value that is to be converted
     *                    into degrees.
     * @return The device's photo orientation in degrees.
     */
    public int getPhotoOrientation(PlatformChannel.DeviceOrientation orientation) {
        int angle = 0;
        // Fallback to device orientation when the orientation value is null.
        if (orientation == null) {
            orientation = getUIOrientation();
        }

        switch (orientation) {
            case PORTRAIT_UP:
                angle = 90;
                break;
            case PORTRAIT_DOWN:
                angle = 270;
                break;
            case LANDSCAPE_LEFT:
                angle = isFrontFacing ? 180 : 0;
                break;
            case LANDSCAPE_RIGHT:
                angle = isFrontFacing ? 0 : 180;
                break;
        }

        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X).
        // This has to be taken into account so the JPEG is rotated properly.
        // For devices with orientation of 90, this simply returns the mapping from ORIENTATIONS.
        // For devices with orientation of 270, the JPEG is rotated 180 degrees instead.
        return (angle + sensorOrientation + 270) % 360;
    }

    /**
     * Returns the device's video orientation in degrees based on the sensor orientation and the last
     * known UI orientation.
     *
     * <p>Returns one of 0, 90, 180 or 270.
     *
     * @return The device's video orientation in degrees.
     */
    public int getVideoOrientation() {
        return this.getVideoOrientation(this.lastOrientation);
    }

    /**
     * Returns the device's video orientation in degrees based on the sensor orientation and the
     * supplied {@link PlatformChannel.DeviceOrientation} value.
     *
     * <p>Returns one of 0, 90, 180 or 270.
     *
     * @param orientation The {@link PlatformChannel.DeviceOrientation} value that is to be converted
     *                    into degrees.
     * @return The device's video orientation in degrees.
     */
    public int getVideoOrientation(PlatformChannel.DeviceOrientation orientation) {
        int angle = 0;

        // Fallback to device orientation when the orientation value is null.
        if (orientation == null) {
            orientation = getUIOrientation();
        }

        switch (orientation) {
            case PORTRAIT_UP:
                angle = 0;
                break;
            case PORTRAIT_DOWN:
                angle = 180;
                break;
            case LANDSCAPE_LEFT:
                angle = 90;
                break;
            case LANDSCAPE_RIGHT:
                angle = 270;
                break;
        }

        if (isFrontFacing) {
            angle *= -1;
        }

        return (angle + sensorOrientation + 360) % 360;
    }

    /**
     * @return the last received UI orientation.
     */
    public PlatformChannel.DeviceOrientation getLastUIOrientation() {
        return this.lastOrientation;
    }

    /**
     * Handles orientation changes based on change events triggered by the OrientationIntentFilter.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     */
    void handleUIOrientationChange() {
        PlatformChannel.DeviceOrientation orientation = getUIOrientation();
        handleOrientationChange(orientation, lastOrientation, messenger);
        lastOrientation = orientation;
    }

    /**
     * Handles orientation changes coming from either the device's sensors or the
     * OrientationIntentFilter.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     */
    static void handleOrientationChange(
            PlatformChannel.DeviceOrientation newOrientation,
            PlatformChannel.DeviceOrientation previousOrientation,
            DartMessenger messenger) {
        if (!newOrientation.equals(previousOrientation)) {
            messenger.sendDeviceOrientationChangeEvent(newOrientation);
        }
    }

    /**
     * Gets the current user interface orientation.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     *
     * @return The current user interface orientation.
     */
    @VisibleForTesting
    PlatformChannel.DeviceOrientation getUIOrientation() {
        final int rotation = getDisplay().getRotation();
        final int orientation = activity.getResources().getConfiguration().orientation;

        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    return PlatformChannel.DeviceOrientation.PORTRAIT_UP;
                } else {
                    return PlatformChannel.DeviceOrientation.PORTRAIT_DOWN;
                }
            case Configuration.ORIENTATION_LANDSCAPE:
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    return PlatformChannel.DeviceOrientation.LANDSCAPE_LEFT;
                } else {
                    return PlatformChannel.DeviceOrientation.LANDSCAPE_RIGHT;
                }
            default:
                return PlatformChannel.DeviceOrientation.PORTRAIT_UP;
        }
    }

    /**
     * Calculates the sensor orientation based on the supplied angle.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     *
     * @param angle Orientation angle.
     * @return The sensor orientation based on the supplied angle.
     */
    @VisibleForTesting
    PlatformChannel.DeviceOrientation calculateSensorOrientation(int angle) {
        final int tolerance = 45;
        angle += tolerance;

        // Orientation is 0 in the default orientation mode. This is portrait-mode for phones
        // and landscape for tablets. We have to compensate for this by calculating the default
        // orientation, and apply an offset accordingly.
        int defaultDeviceOrientation = getDeviceDefaultOrientation();
        if (defaultDeviceOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            angle += 90;
        }
        // Determine the orientation
        angle = angle % 360;
        return new PlatformChannel.DeviceOrientation[]{
                PlatformChannel.DeviceOrientation.PORTRAIT_UP,
                PlatformChannel.DeviceOrientation.LANDSCAPE_LEFT,
                PlatformChannel.DeviceOrientation.PORTRAIT_DOWN,
                PlatformChannel.DeviceOrientation.LANDSCAPE_RIGHT,
        }
                [angle / 90];
    }

    /**
     * Gets the default orientation of the device.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     *
     * @return The default orientation of the device.
     */
    @VisibleForTesting
    int getDeviceDefaultOrientation() {
        Configuration config = activity.getResources().getConfiguration();
        int rotation = getDisplay().getRotation();
        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)
                && config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                && config.orientation == Configuration.ORIENTATION_PORTRAIT)) {
            return Configuration.ORIENTATION_LANDSCAPE;
        } else {
            return Configuration.ORIENTATION_PORTRAIT;
        }
    }

    /**
     * Gets an instance of the Android {@link android.view.Display}.
     *
     * <p>This method is visible for testing purposes only and should never be used outside this
     * class.
     *
     * @return An instance of the Android {@link android.view.Display}.
     */
    @SuppressWarnings("deprecation")
    @VisibleForTesting
    Display getDisplay() {
        return ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }
}
