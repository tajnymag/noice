package com.github.ashutoshgngwr.noice.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.github.ashutoshgngwr.noice.R
import com.github.ashutoshgngwr.noice.databinding.SignInFormFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@AndroidEntryPoint
class SignInFormFragment : Fragment() {

  private lateinit var binding: SignInFormFragmentBinding
  private val viewModel: SignInFormViewModel by viewModels()
  private val mainNavController by lazy {
    Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
    binding = SignInFormFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // TODO: workaround until the upstream issue resolved!
    //  https://issuetracker.google.com/issues/167959935
    (activity as? AppCompatActivity)
      ?.supportActionBar
      ?.setTitle(
        if (viewModel.isReturningUser) {
          R.string.sign_in
        } else {
          R.string.sign_up
        }
      )

    binding.lifecycleOwner = viewLifecycleOwner
    binding.viewModel = viewModel
    binding.signIn.setOnClickListener {
      mainNavController.navigate(
        R.id.sign_in_result,
        SignInResultFragmentArgs(
          isReturningUser = viewModel.isReturningUser,
          name = viewModel.name.value,
          email = requireNotNull(viewModel.email.value),
        ).toBundle()
      )
    }
  }
}

@HiltViewModel
class SignInFormViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

  val isReturningUser: Boolean = SignInFormFragmentArgs.fromSavedStateHandle(savedStateHandle)
    .isReturningUser

  val name = MutableStateFlow("")
  val email = MutableStateFlow("")

  val isNameValid: StateFlow<Boolean> = name.transform { name ->
    // maxLength: 64
    // minLength: 1
    emit(isReturningUser || (name.isNotBlank() && name.length <= 64))
  }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

  val isEmailValid: StateFlow<Boolean> = email.transform { email ->
    // maxLength: 64
    // minLength: 3
    emit(
      email.isNotBlank()
        && email.length <= 64
        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    )
  }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
}
