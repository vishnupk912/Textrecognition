package com.textrecognization.textrecognization

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerResultsIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer

class MainActivity : AppCompatActivity() {

    lateinit var imageveiew :ImageView;
    lateinit var uploadimage :ImageView;
    lateinit var extractedtext :TextView;
    lateinit var capture :ImageView;
    lateinit var noimagelay :LinearLayout;

    lateinit var laydata :LinearLayout;
    lateinit var close :ImageView;
    lateinit var lottie :LinearLayout;




    lateinit var recolay :LinearLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.CAMERA) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA), 1)
            }
        }

        imageveiew=findViewById(R.id.imageview);
        uploadimage=findViewById(R.id.upload);
        extractedtext=findViewById(R.id.tvid);
        recolay=findViewById(R.id.reco_text);
        capture=findViewById(R.id.capture);
        noimagelay=findViewById(R.id.noimagelay);
        close=findViewById(R.id.close);
        lottie=findViewById(R.id.lottie);


        laydata=findViewById(R.id.laydata);


        close.setOnClickListener {

            showHide(laydata)

        }

        uploadimage.setOnClickListener {
            openimage()
            extractedtext.text = ""

        }

        recolay.setOnClickListener {
            processImage(recolay)
            showHide(laydata)

        }



        capture.setOnClickListener {
            capturePhoto();
            extractedtext.text = ""


        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1){

            imageveiew.setImageURI(data?.data) // handle chosen image
            showHide(noimagelay)
        }

      else  if (resultCode == Activity.RESULT_OK && requestCode == 2 && data != null){
            imageveiew.setImageBitmap(data.extras?.get("data") as Bitmap)
            showHide(noimagelay)

        }

    }

    private fun openimage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }



    fun processImage(v: View) {
        if (imageveiew.drawable != null) {
            extractedtext.setText("")
            v.isEnabled = false
            val bitmap = (imageveiew.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    showHide(lottie)
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    showHide(lottie)
                    extractedtext.setText("Failed")
                }
        }
        else
        {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }

    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            extractedtext.setText("No Text Found")
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            extractedtext.append(blockText + "\n")
        }
    }

    fun capturePhoto() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 2)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.CAMERA) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun showHide(view:View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }

}