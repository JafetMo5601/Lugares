package com.lugares.ui.lugar

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lugares.R
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.model.Lugar
import com.lugares.view_models.LugarViewModel
import java.util.jar.Manifest


class AddLugarFragment : Fragment() {
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.btAdd.setOnClickListener {
            addLugar()
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

    private fun addLugar() {
        var name = binding.etName.text.toString()

        if(name.isNotEmpty()) {
            var email = binding.etEmail.text.toString()
            var phone = binding.etPhone.text.toString()
            var web = binding.etWebSite.text.toString()
            val latitude = binding.tvLatitud.text.toString().toDouble()
            val length = binding.tvLongitud.text.toString().toDouble()
            val height = binding.tvAltura.text.toString().toDouble()
            var lugar = Lugar("", name, email, phone, web, latitude, length, height, "", "")
            lugarViewModel.addLugar(lugar)
            Toast.makeText(requireContext(), getString(R.string.msg_added), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_missing_data), Toast.LENGTH_LONG).show()
        }
        findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}