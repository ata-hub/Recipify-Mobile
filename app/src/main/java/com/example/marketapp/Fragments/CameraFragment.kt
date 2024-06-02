import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.marketapp.Fragments.LoadingDialogFragment
import com.example.marketapp.Fragments.NoRecipesFoundFragment
import com.example.marketapp.Fragments.RecipeResultFragment
import com.example.marketapp.Interfaces.BackendService
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R
import com.example.marketapp.databinding.FragmentCameraBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException

class CameraFragment : Fragment() {

    private val ARG_IMAGE_ID = "imageId"
    private lateinit var binding: FragmentCameraBinding
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:8080") //TODO Replace with your actual backend base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val backendService = retrofit.create(BackendService::class.java)

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.capturedImage.setImageBitmap(imageBitmap)
                updateFindRecipesButtonVisibility()
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                if (imageUri != null) {
                    try {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                        binding.capturedImage.setImageBitmap(imageBitmap)
                        updateFindRecipesButtonVisibility()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("CameraFragment", "Image URI is null")
                }
            } else {
                Log.e("CameraFragment", "Failed to pick image from gallery")
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Log.i("CameraFragment", "Camera permission denied")
                showPermissionDeniedDialog("camera")
            }
        }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Log.i("CameraFragment", "Storage permission denied")
                openGallery()
            }
        }

    private var loadingDialog: LoadingDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val imageId = arguments?.getInt(ARG_IMAGE_ID, -1) ?: -1
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set OnClickListener to the back button
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.buttonOpenCamera.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.buttonOpenGallery.setOnClickListener {
            checkStoragePermissionAndOpenGallery()
        }

        binding.buttonFindRecipes.setOnClickListener {
            val category = arguments?.getString("category")
            if (category != null) {
                sendImageToBackend(category)
            }
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun checkStoragePermissionAndOpenGallery() {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), storagePermission) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissionLauncher.launch(storagePermission)
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun sendImageToBackend(category: String) {
        Log.i("backend", "Backend call started...")
        val imageDrawable = binding.capturedImage.drawable as? BitmapDrawable
        if (imageDrawable == null) {
            Log.e("backend", "No image to send")
            return
        }

        // Show the loading dialog
        loadingDialog = LoadingDialogFragment()
        loadingDialog?.isCancelable = false
        loadingDialog?.show(parentFragmentManager, "loadingDialog")

        val imageBitmap = imageDrawable.bitmap
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageByteArray = outputStream.toByteArray()

        val requestFile = imageByteArray.toRequestBody("image/jpeg".toMediaType())
        val imageBody = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)
        val categoryRequestBody = category.toRequestBody("text/plain".toMediaType())

        val call = backendService.uploadImage(imageBody, categoryRequestBody)
        call.enqueue(object : retrofit2.Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                // Hide the loading dialog
                loadingDialog?.dismiss()

                if (response.isSuccessful) {
                    Log.i("backend", "Backend call successful")
                    val recipesFromBackend = response.body()
                    Log.i("backend", "Response body: $recipesFromBackend")
                    recipesFromBackend?.forEach { recipe ->
                        Log.i("backend", "Recipe: ${recipe.toString()}")
                    }
                    if (recipesFromBackend.isNullOrEmpty()) {
                        showNoRecipesFoundFragment()
                    } else {
                        val fragmentManager = requireActivity().supportFragmentManager
                        val recipeListFragment = recipesFromBackend?.let { RecipeResultFragment.newInstance(it) }
                        if (recipeListFragment != null) {
                            fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, recipeListFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                } else {
                    Log.e("backend", "Backend call failed with response code: ${response.code()} and message: ${response.message()}")
                    showNoRecipesFoundFragment()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Hide the loading dialog
                loadingDialog?.dismiss()

                Log.e("backend", "Backend call failed: ${t.message}")
                showNoRecipesFoundFragment()
            }
        })
    }

    private fun showNoRecipesFoundFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val noRecipesFoundFragment = NoRecipesFoundFragment()
        fragmentManager.beginTransaction()
            .replace(R.id.frameLayout, noRecipesFoundFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showPermissionDeniedDialog(permission: String) {
        val message = when (permission) {
            "camera" -> "Camera permission is needed to take pictures. Please enable it in the app settings."
            "storage" -> "Storage permission is needed to pick images from the gallery. Please enable it in the app settings."
            else -> "Permission is needed. Please enable it in the app settings."
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.fromParts("package", requireContext().packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun updateFindRecipesButtonVisibility() {
        binding.buttonFindRecipes.visibility = if (binding.capturedImage.drawable != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
