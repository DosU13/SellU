package com.dosu.sellu.ui.user_profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.R
import com.dosu.sellu.databinding.UserProfileFragmentBinding
import com.dosu.sellu.ui.products.util.OnSwipeTouchListener
import com.dosu.sellu.ui.user_profile.util.UserListener
import com.dosu.sellu.ui.user_profile.viewmodel.UserViewModel
import com.dosu.sellu.ui.user_profile.viewmodel.UserViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.toByteArray
import com.dosu.sellu.util.toDrawable
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class UserProfileFragment : Fragment(), DIAware, UserListener {
    private var _binding: UserProfileFragmentBinding? = null
    private val binding get() = _binding!!
    override val di: DI by closestDI()
    private val userViewModelFactory: UserViewModelFactory by instance()
    private lateinit var userViewModel: UserViewModel
    private val user get() = FirebaseAuth.getInstance().currentUser!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = UserProfileFragmentBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this, userViewModelFactory).get(UserViewModel::class.java)
        userViewModel.setListener(this)

        if (user.isAnonymous) setUpWithGuest()
        else setUpWithUser()

        binding.username.setOnTouchListener(object : OnSwipeTouchListener(requireContext()){
            override fun onSwipeRight() {
                Toast.makeText(context, "right", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeLeft() {
                Toast.makeText(context, "left", Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun setUpWithGuest(){
        binding.btn.setText(R.string.sign_in)
        binding.btn.setOnClickListener { signIn() }
        binding.username.text = getString(R.string.guest)
        binding.email.text = getString(R.string.guest_email_text)
        binding.profilePicture.setImageResource(R.drawable.ic_baseline_face_24)
    }

    private fun setUpWithUser(){
        binding.btn.setText(R.string.sign_out)
        binding.btn.setOnClickListener { signOut() }
        binding.username.text = user.displayName
        binding.email.text = user.email
        userViewModel.downloadImage()
        binding.profilePicture.setOnLongClickListener {setProfilePicture(); true }
    }

    private fun setProfilePicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getImageLauncher.launch(intent)
    }
    private var getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == RESULT_OK){
            result.data?.data?.let {
                binding.profilePicture.setImageURI(it)
                userViewModel.uploadImage(it.toByteArray(requireContext())) }
        }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setAlwaysShowSignInMethodScreen(true).build()
            //.enableAnonymousUsersAutoUpgrade().build()
        signInLauncher.launch(signInIntent)
    }
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){ result->
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK){
            setUpWithUser()
        }else{
            Toast.makeText(context, response?.error?.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if(it.isSuccessful) setUpWithGuest()
            else Toast.makeText(context, "create guest failed", Toast.LENGTH_LONG).show()
        }
    }

    override fun downloadImage(byteArray: ByteArray) {
        binding.profilePicture.setImageDrawable(byteArray.toDrawable(resources))
    }

    override fun uploadImageSucceed() {
        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, responseBody?.detail, Toast.LENGTH_SHORT ).show()
    }
}