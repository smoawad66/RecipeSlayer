package com.example.recipeslayer.ui.recipe.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentHomeBinding
import com.example.recipeslayer.databinding.FragmentImageLabelingBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelingFragment : Fragment() {

    lateinit var binding: FragmentImageLabelingBinding

    private val REQUEST_IMAGE_CAPTURE=1000

    private var imageBitmap: Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageLabelingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            captureImage.setOnClickListener {

                takeImage()

                textView.text = ""


            }

            detectImage.setOnClickListener {

                if (imageBitmap != null) {

                    processImage()
                } else {

                    Toast.makeText(requireContext(), "Select a photo first", Toast.LENGTH_SHORT)
                        .show()

                }


            }

        }
    }
    private fun processImage() {

        val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(imageBitmap!!, 0)
        imageLabeler.process(inputImage).addOnSuccessListener { imageLabels ->
            var stringImageRecognition = ""
            for (imageLabel in imageLabels) {
                val stringLabel = imageLabel.text
                val floatConfidence = imageLabel.confidence
                stringImageRecognition += "\n $stringLabel:\n $floatConfidence "
            }
            ////////////////////////////////////////////////////////////////////////////////////////
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.ARABIC)
                .build()

            val englishArabicTranslator = Translation.getClient(options)

            englishArabicTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    englishArabicTranslator.translate(stringImageRecognition)
                        .addOnSuccessListener { translatedText ->
                            binding.textView.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            Log.i("lol", "onBindViewHolder: $exception.message")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.i("lol", "onBindViewHolder: $exception.message")
                }
            ////////////////////////////////////////////////////////////////////////////////////////

//            binding.textView.text = stringImageRecognition
        }


    }

    private fun takeImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"  // Limit to only images
        intent.putExtra("crop", "true")
        intent.putExtra("scale", true)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            val imageUri = data?.data

            if (imageUri != null) {
                binding.imageView.setImageURI(imageUri)
                imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            }
        }
    }
}