package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Emojifier class - works with Mobile Vision library
 */
public class Emojifier {

    private final static String TAG = Emojifier.class.getSimpleName();

    static FaceDetector faceDetector = null;

    public static void init(Context context) {
        if(faceDetector == null) {
            faceDetector = new FaceDetector
                    .Builder(context)
                    .setTrackingEnabled(false)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build();
        }
    }

    public static void release() {
        if(faceDetector != null) {
            faceDetector.release();
            faceDetector = null;
        }
    }

    public static void detectFaces(Context context, Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        if(faceDetector.isOperational()) {
            SparseArray<Face> faces = faceDetector.detect(frame);
            Toast.makeText(context, "Faces: " + faces.size() , Toast.LENGTH_SHORT).show();
            for(int i = 0; i < faces.size(); i++) {
                getClasification(faces.get(i));
            }
        }
    }

    static void getClasification(Face face) {
        Log.v(TAG, "Clasification " + face.getIsLeftEyeOpenProbability() + " " + face.getIsRightEyeOpenProbability() + " " + face.getIsSmilingProbability());
    }
}
