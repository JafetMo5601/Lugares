package com.lugares.ui.lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lugares.R
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.model.Lugar
import com.lugares.view_models.LugarViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddLugarFragment : Fragment() {
    private lateinit var lugarViewModel: LugarViewModel
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        binding.btAdd.setOnClickListener {
            addLugar()
        }

        return binding.root
    }

    private fun addLugar() {
        var name = binding.etName.text.toString()

        if(name.isNotEmpty()) {
            var email = binding.etEmail.text.toString()
            var phone = binding.etPhone.text.toString()
            var web = binding.etWebSite.text.toString()
            var lugar = Lugar(0, name, email, phone, web, 0.0, 0.0, 0.0, "", "")
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