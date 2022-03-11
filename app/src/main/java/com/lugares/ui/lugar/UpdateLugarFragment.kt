package com.lugares.ui.lugar

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import java.util.jar.Manifest


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

        binding.tvLength.text = args.lugar.length.toString()
        binding.tvLatitude.text = args.lugar.latitude.toString()
        binding.tvHeight.text = args.lugar.height.toString()

        binding.btUpdate.setOnClickListener {
            updateLugar()
        }

        binding.btEmail.setOnClickListener {
            sendEmail()
        }

        binding.btPhone.setOnClickListener {
            makeACall()
        }

        binding.btWhatsapp.setOnClickListener {
            sendWhatsApp()
        }

        binding.btWeb.setOnClickListener {
            seeWebsite()
        }

        binding.btLocation.setOnClickListener {
            seeMap()
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

    private fun sendEmail() {
        val to = binding.etEmail.text.toString()

        if (to.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.msg_greetings) + " " + binding.etName.text)
            intent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.msg_email_message))
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun makeACall() {
        val phone = binding.etPhone.text.toString()

        if (phone.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phone")

            if (requireActivity().checkSelfPermission(android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
                    requireActivity()
                        .requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE), 105)

            } else {
                requireActivity().startActivity(intent)
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun sendWhatsApp() {
        val phone = binding.etPhone.text.toString()

        if (phone.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$phone&text=" + getString(R.string.msg_greetings)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun seeWebsite() {
        val site = binding.etWebSite.text.toString()

        if (site.isNotEmpty()) {
            val uri = Uri.parse("http://$site")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun seeMap() {
        val latitude = binding.tvLatitude.text.toString().toDouble()
        val length = binding.tvLength.text.toString().toDouble()

        if (latitude.isFinite() && length.isFinite()) {
            val location = Uri.parse("geo:$latitude,$length?z=18")
            val intent = Intent(Intent.ACTION_VIEW, location)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
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