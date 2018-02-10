package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
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


    private final static double EYE_OPEN_THRESHOLD = 0.5;
    private final static double SMILING_THRESHOLD = .2;


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
                whichEmoji(faces.get(i));
            }
        }
    }

    static void whichEmoji(Face face) {
        boolean smiling = face.getIsSmilingProbability() > SMILING_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_THRESHOLD;

        // which emoji should be used
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }
    }

    // Enum for all possible Emojis
    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }
}
