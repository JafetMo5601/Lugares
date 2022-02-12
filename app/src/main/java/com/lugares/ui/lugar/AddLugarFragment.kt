package com.lugares.ui.lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lugares.R
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.model.Lugar


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddLugarFragment : Fragment() {

    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

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
            Toast.makeText(requireContext(), getString(R.string.msg))
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddLugarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddLugarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}