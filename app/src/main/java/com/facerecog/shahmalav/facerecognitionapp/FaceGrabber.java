package com.facerecog.shahmalav.facerecognitionapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.AsyncTask;

public class FaceGrabber {
    private Bitmap myBitmap;
    private int width, height;
    private FaceDetector.Face[] detectedFaces;
    private int NUMBER_OF_FACES = 6;
    private FaceDetector faceDetector;
    private int NUMBER_OF_FACE_DETECTED;
    private float eyeDistance;

    // scale 92 x 112
    final int NORMALIZED_WIDTH = 92;
    final int NORMALIZED_HEIGHT = 112;

    public FaceGrabber(Bitmap b) {

        myBitmap = oddBitmapFixer(b);
        System.out.println("Face Grabber (original): width= "
                + myBitmap.getWidth() + " height = " + myBitmap.getHeight());
        // scale the image down first
        myBitmap = scaleDown(myBitmap, 1000, true);
        System.out.println("Face Grabber (after-scale): width= "
                + myBitmap.getWidth() + " height = " + myBitmap.getHeight());

    }

    // image needs to be even width for the RGB 565
    public Bitmap oddBitmapFixer(Bitmap imgBitmap) {
        if (imgBitmap.getWidth() % 2 == 1) {
            imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0,
                    imgBitmap.getWidth() - 1, imgBitmap.getHeight());
        }
        return imgBitmap;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min((float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height,
                filter);
        return newBitmap;
    }

    public int getFaceNum() {
        myBitmap = convert(myBitmap, Bitmap.Config.RGB_565);
        width = myBitmap.getWidth();
        height = myBitmap.getHeight();

        detectedFaces = new FaceDetector.Face[NUMBER_OF_FACES];
        faceDetector = new FaceDetector(width, height, NUMBER_OF_FACES);
        NUMBER_OF_FACE_DETECTED = faceDetector.findFaces(myBitmap,
                detectedFaces);

        return NUMBER_OF_FACE_DETECTED;
    }

    private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), config);
        Canvas canvas = new Canvas(convertedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }

    public Bitmap[] getFaceBitmap() {
        // canvas.drawBitmap(myBitmap, 0, 0, null);
        Bitmap[] bmArr = new Bitmap[NUMBER_OF_FACE_DETECTED];
        // Paint myPaint = new Paint();
        // myPaint.setColor(Color.GREEN);
        // myPaint.setStyle(Paint.Style.STROKE);
        // myPaint.setStrokeWidth(3);

        for (int count = 0; count < NUMBER_OF_FACE_DETECTED; count++) {
            Face face = detectedFaces[count];
            PointF midPoint = new PointF();
            face.getMidPoint(midPoint);

            eyeDistance = face.eyesDistance();

            int x = Math.round(midPoint.x);
            int y = Math.round(midPoint.y);
            int eye = Math.round(eyeDistance);
            System.out.println("x=" + x + " y=" + y + " eye=" + eye);
            bmArr[count] = Bitmap.createBitmap(myBitmap, x - eye, y - eye,
                    eye * 2, eye * 3);
            // bmArr[count] = Bitmap.createBitmap(myBitmap, x - eye, y - eye, x
            // + (eye / 2), x + (eye / 2));

            // convert to black and white
            bmArr[count] = toGrayscale(bmArr[count]);
            System.out.println("Face Grabber (after-scale): width= "
                    + bmArr[count].getWidth() + " height = "
                    + bmArr[count].getHeight());
            // scale 92
            bmArr[count] = normalize(bmArr[count]);
            System.out.println("Face Grabber (after-normalize-width): width= "
                    + bmArr[count].getWidth() + " height = "
                    + bmArr[count].getHeight());
            // crop112
            bmArr[count] = Bitmap.createBitmap(bmArr[count], 0, 0,
                    NORMALIZED_WIDTH, NORMALIZED_HEIGHT);
            System.out.println("Face Grabber (NORMALIZED): width= "
                    + bmArr[count].getWidth() + " height = "
                    + bmArr[count].getHeight());
        }
        return bmArr;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap normalize(Bitmap b) {
        System.out.println("w=" + b.getWidth() + " h=" + b.getHeight());
        height = b.getHeight() / (b.getWidth() / NORMALIZED_WIDTH);
        // height = (NORMALIZED_WIDTH / b.getWidth()) * b.getHeight();
        return Bitmap.createScaledBitmap(b, NORMALIZED_WIDTH, height, false);
    }
}