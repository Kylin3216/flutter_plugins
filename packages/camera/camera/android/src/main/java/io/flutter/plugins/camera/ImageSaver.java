// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Saves a JPEG {@link Image} into the specified {@link File}.
 */
public class ImageSaver implements Runnable {
    private final boolean isFront;

    /**
     * The JPEG image
     */
    private final Image image;

    /**
     * The file we save the image into.
     */
    private final File file;

    /**
     * Used to report the status of the save action.
     */
    private final Callback callback;

    /**
     * Creates an instance of the ImageSaver runnable
     *
     * @param image    - The image to save
     * @param file     - The file to save the image to
     * @param callback - The callback that is run on completion, or when an error is encountered.
     */
    ImageSaver(boolean isFront, @NonNull Image image, @NonNull File file, @NonNull Callback callback) {
        this.isFront = isFront;
        this.image = image;
        this.file = file;
        this.callback = callback;
    }

    @Override
    public void run() {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = FileOutputStreamFactory.create(file);
            if (isFront) {
                output.write(bytes);
                output.flush();
                ExifInterface exif = new ExifInterface(file);
                int ob = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);
                int degree = 0;
                switch (ob) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 90;
                        break;
                }
                matrix.postRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                output.close();
                output = FileOutputStreamFactory.create(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            } else {
                output.write(bytes);
            }
            callback.onComplete(file.getAbsolutePath());

        } catch (IOException e) {
            callback.onError("IOError", "Failed saving image");
        } finally {
            image.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    callback.onError("cameraAccess", e.getMessage());
                }
            }
        }
    }

    /**
     * The interface for the callback that is passed to ImageSaver, for detecting completion or
     * failure of the image saving task.
     */
    public interface Callback {
        /**
         * Called when the image file has been saved successfully.
         *
         * @param absolutePath - The absolute path of the file that was saved.
         */
        void onComplete(String absolutePath);

        /**
         * Called when an error is encountered while saving the image file.
         *
         * @param errorCode    - The error code.
         * @param errorMessage - The human readable error message.
         */
        void onError(String errorCode, String errorMessage);
    }

    /**
     * Factory class that assists in creating a {@link FileOutputStream} instance.
     */
    static class FileOutputStreamFactory {
        /**
         * Creates a new instance of the {@link FileOutputStream} class.
         *
         * <p>This method is visible for testing purposes only and should never be used outside this *
         * class.
         *
         * @param file - The file to create the output stream for
         * @return new instance of the {@link FileOutputStream} class.
         * @throws FileNotFoundException when the supplied file could not be found.
         */
        @VisibleForTesting
        public static FileOutputStream create(File file) throws FileNotFoundException {
            return new FileOutputStream(file);
        }
    }
}
