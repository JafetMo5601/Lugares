package com.lugares.ui.lugar

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.R
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.model.Lugar
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles
import com.lugares.view_models.LugarViewModel


class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel
    private lateinit var audioUtiles: AudioUtiles
    private lateinit var imageUtiles: ImagenUtiles
    private lateinit var takePictureActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.btAdd.setOnClickListener {
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.visibility = TextView.VISIBLE
            uploadAudiCloud()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioUtiles = AudioUtiles(requireActivity(),
                requireContext(),
                binding.btAccion,
                binding.btPlay,
                binding.btDelete,
                getString(R.string.msg_graba_audio),
                getString(R.string.msg_detener_audio))
        }

        takePictureActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result -> if (result.resultCode == Activity.RESULT_OK) {
                    imageUtiles.actualizaFoto()
                }
            imageUtiles = ImagenUtiles(requireContext(),
            binding.btPhoto, binding.btRotaL, binding.btRotaR, binding.imagen, takePictureActivity)
            }

        locateGPS()

        return binding.root
    }

    private var withPermission = true;
    private fun locateGPS() {
        val fusedLocationProviderClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&

            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), 105)
        }

        if (withPermission)  {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                location: Location? ->
                if (location != null) {
                    binding.tvLatitud.text = "${location.latitude}"
                    binding.tvLongitud.text = "${location.longitude}"
                    binding.tvAltura.text = "${location.altitude}"
                } else {
                    binding.tvLatitud.text = getString(R.string.error)
                    binding.tvLongitud.text = getString(R.string.error)
                    binding.tvAltura.text = getString(R.string.error)
                }
            }
        }
    }

    private fun addLugar(audioRoute: String, imageRoute: String) {
        var name = binding.etName.text.toString()

        if(name.isNotEmpty()) {
            var email = binding.etEmail.text.toString()
            var phone = binding.etPhone.text.toString()
            var web = binding.etWebSite.text.toString()
            val latitude = binding.tvLatitud.text.toString().toDouble()
            val length = binding.tvLongitud.text.toString().toDouble()
            val height = binding.tvAltura.text.toString().toDouble()
            var lugar = Lugar("", name, email, phone, web, latitude, length, height, audioRoute, imageRoute)
            lugarViewModel.addLugar(lugar)
            Toast.makeText(requireContext(), getString(R.string.msg_added), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_missing_data), Toast.LENGTH_LONG).show()
        }
        findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
    }

    fun uploadAudiCloud() {
        val audioFile = audioUtiles.audioFile

        if (audioFile.exists() && audioFile.isFile && audioFile.canRead()) {
            val route = Uri.fromFile(audioFile)
            val cloudRoute = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${audioFile.name}"
            var reference: StorageReference = Firebase.storage.reference.child(cloudRoute)
            reference.putFile(route)
                .addOnSuccessListener {
                    reference.downloadUrl
                        .addOnCanceledListener {
                            val audioRoute = it.toString()
                            uploadImageToCloud(audioRoute)
                        }
                }
                .addOnFailureListener{uploadImageToCloud("")}
        } else {
            uploadImageToCloud("")
        }
    }

    private fun uploadImageToCloud(audioRoute: String) {
        val imageFile = imageUtiles.imagenFile

        if (imageFile.exists() && imageFile.isFile && imageFile.canRead()) {
            val route = Uri.fromFile(imageFile)
            val cloudRoute = "lugaresApp/${Firebase.auth.currentUser?.email}/images/${imageFile.name}"
            var reference: StorageReference = Firebase.storage.reference.child(cloudRoute)
            reference.putFile(route)
                .addOnSuccessListener {
                    reference.downloadUrl
                        .addOnCanceledListener {
                            val imageRoute = it.toString()
                            addLugar(audioRoute, imageRoute)
                        }
                }
                .addOnFailureListener{addLugar(audioRoute, "")}
        } else {
            addLugar(audioRoute, "")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}