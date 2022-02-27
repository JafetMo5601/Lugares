package com.lugares.ui.lugar

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.view_models.LugarViewModel


class UpdateLugarFragment : Fragment() {
    private lateinit var lugarViewModel: LugarViewModel
    private val args by navArgs<UpdateLugarFragmentArgs>()
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        binding.etName.setText(args.lugar.name)
        binding.etEmail.setText(args.lugar.email)
        binding.etPhone.setText(args.lugar.phone)
        binding.etWebSite.setText(args.lugar.web)

        binding.btUpdate.setOnClickListener {
            updateLugar()
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mennu_delete) {
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        var name = binding.etName.text.toString()
        var email = binding.etEmail.text.toString()
        var phone = binding.etPhone.text.toString()
        var web = binding.etWebSite.text.toString()

        var lugar = Lugar(args.lugar.id, name, email, phone, web, 0.0, 0.0, 0.0, "", "")
        lugarViewModel.updateLugar(lugar)
        Toast.makeText(requireContext(), getString(R.string.msg_updated), Toast.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
    }

    private fun deleteLugar() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) {_,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }
        builder.setNegativeButton(getString(R.string.no)) {_,_ ->}
        builder.setTitle(R.string.menu_delete)
        builder.setMessage(getString(R.string.msg_sure_delete) + " ${args.lugar.name}?")
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}