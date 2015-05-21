package com.facerecog.shahmalav.facerecognitionapp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements HttpAsyncResponse {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private static Uri mImageCaptureUri1;
	private Bitmap imgBitmap;
	HttpPostAsync asyncTask =new HttpPostAsync();
	ImageView iv;
	String postEx = null;
	List<String> IdentifiedPerson;
	List<Integer> Confidence;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // force landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		asyncTask.responseInterface = this;

		// fire camera
		iv = (ImageView) findViewById(R.id.imageView1);
	}

	// private void dispatchTakePictureIntent() {
	// Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	// startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	// }
	//
	// }

	public String getBase64FromImage(Bitmap bmp){
		Bitmap immagex=bmp;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

		Log.e("Lookhere", imageEncoded);
		//Log.e("LOOK", imageEncoded);
		return imageEncoded;
	}

	public void getPix(View v) {
		if (imgBitmap != null) {
			final FaceGrabber fg = new FaceGrabber(imgBitmap);

			// get # faces
			//tv = (TextView) findViewById(R.id.faceNums);
			//tv.setText("faces=" + fg.getFaceNum());
			fg.getFaceNum();

			// test
            try{
                iv.setImageBitmap(fg.getFaceBitmap()[0]);
                asyncTask.execute(new String[]{getBase64FromImage(fg.getFaceBitmap()[0])});
            } catch (Exception e){
                e.getMessage();
                Toast.makeText(getApplicationContext(), "Cannot capture image. Try Again!", Toast.LENGTH_LONG).show();
            }

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			// Bundle extras = data.getExtras();
			// imgBitmap = (Bitmap) extras.get("data");

			try {
				imgBitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), mImageCaptureUri1);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public void takePic(View v) {
		// dispatchTakePictureIntent();
		openCamera();
	}

	public void openCamera() {

		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		mImageCaptureUri1 = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "tmp_avatar_"
				+ String.valueOf(System.currentTimeMillis()) + ".jpg"));

		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri1);
		cameraIntent.putExtra("return-data", true);
		startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
	}
    @Override
	public void processFinish(String output) {
		//Log.e("processFinishResult : ", output);
		Bundle faceRegResults = new Bundle();
		faceRegResults.putString("results", output);
		Intent i = new Intent().setClass(MainActivity.this, ResultMatch.class);
		i.putExtras(faceRegResults);
		Log.e("intent main", i.toString());
		startActivity(i);

	}
}