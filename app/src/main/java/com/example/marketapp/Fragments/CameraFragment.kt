import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

class CameraFragment : Fragment() {

    private val CAMERA_REQUEST_CODE = 101
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
                // TODO: Handle the captured photo
                // You can retrieve the photo from result.data?.extras?.get("data") as Bitmap
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.capturedImage.setImageBitmap(imageBitmap)
                val category = arguments?.getString("category")
                // Send the photo to your backend along with any additional information
                if (category != null) {
                    sendImageToBackend(category)
                }


            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val imageId = arguments?.getInt(ARG_IMAGE_ID, -1) ?: -1
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val category = arguments?.getString("category")
        // Check and request camera permissions if needed
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE
            )
        } else {
            openCamera(category)
        }
    }

    private fun openCamera(category: String?) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(cameraIntent)
        category?.let { sendImageToBackend(it) }
    }
    private fun sendImageToBackend(category: String) {
        // Retrieve the captured image from the ImageView
        val imageBitmap = (binding.capturedImage.drawable as BitmapDrawable).bitmap
        Log.i("backend", "category sent is:$category")
        // Convert the Bitmap to a byte array
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageByteArray = outputStream.toByteArray()

        // Create a RequestBody from the byte array
        val requestFile = imageByteArray.toRequestBody("image/jpeg".toMediaType())
        val imageBody = MultipartBody.Part.createFormData("image", "image.jpg", requestFile)

        // Convert the category to RequestBody
        val categoryRequestBody = category.toRequestBody("text/plain".toMediaType())

        // Call your backend API to upload the image along with the category
        val call = backendService.uploadImage(imageBody, categoryRequestBody)

        // Enqueue the call to execute it asynchronously
        call.enqueue(object : retrofit2.Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                // Handle the backend response
                if (response.isSuccessful) {
                    Log.i("backend","Image sent successfully")
                    //buraya yeni sayfa oluştur, dönen recipeleri gösterecek bir fragment daha
                    val recipesFromBackend = response.body()
                    // Navigate to RecipeListFragment and pass the recipes
                    val fragmentManager = requireActivity().supportFragmentManager
                    val recipeListFragment = recipesFromBackend?.let {
                        RecipeResultFragment.newInstance(
                            it
                        )
                    }
                    if (recipeListFragment != null) {
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, recipeListFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                } else {
                    Log.i("backend","backend failed")
                    Log.i("backend",response.message())
                    Log.i("backend", response.code().toString())
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                t.message?.let { Log.i("backend", it) }
            }
        })
    }
}