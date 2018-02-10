package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    private static final float EMOJI_SCALE_FACTOR = .9f;
    private final static double EYE_OPEN_THRESHOLD = 0.5;
    private final static double SMILING_THRESHOLD = .2;


    static FaceDetector faceDetector = null;

    private static Bitmap emojiBitmap = null;

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

    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        Bitmap resultBitmap = bitmap;

        if(faceDetector.isOperational()) {
            SparseArray<Face> faces = faceDetector.detect(frame);
            Toast.makeText(context, "Faces: " + faces.size() , Toast.LENGTH_SHORT).show();
            for(int i = 0; i < faces.size(); i++) {
                Emoji emoji = whichEmoji(faces.get(i));
                if(emoji == Emoji.LEFT_WINK) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwink);
                }
                if(emoji == Emoji.RIGHT_WINK) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwink);
                }
                if(emoji == Emoji.CLOSED_EYE_SMILE) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_smile);
                }
                if(emoji == Emoji.SMILE) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
                }
                if(emoji == Emoji.LEFT_WINK_FROWN) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwinkfrown);
                }
                if(emoji == Emoji.RIGHT_WINK_FROWN) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwinkfrown);
                }
                if(emoji == Emoji.CLOSED_EYE_FROWN) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
                }
                if(emoji == Emoji.FROWN) {
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frown);
                }

                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, faces.get(i));
            }
        }

        return resultBitmap;
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

    static Emoji whichEmoji(Face face) {
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

        return emoji;
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
