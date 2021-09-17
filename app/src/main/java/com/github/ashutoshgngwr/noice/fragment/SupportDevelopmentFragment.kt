package com.github.ashutoshgngwr.noice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.ashutoshgngwr.noice.NoiceApplication
import com.github.ashutoshgngwr.noice.R
import com.github.ashutoshgngwr.noice.databinding.SupportDevelopmentFragmentBinding

class SupportDevelopmentFragment : Fragment() {

  private lateinit var binding: SupportDevelopmentFragmentBinding
  private lateinit var app: NoiceApplication

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = SupportDevelopmentFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    app = NoiceApplication.of(requireContext())
    app.donateViewProvider.addViewToParent(binding.donateViewContainer)

    binding.shareButton.setOnClickListener {
      val text = getString(R.string.app_description)
      val playStoreURL = getString(R.string.play_store_url)
      val fdroidURL = getString(R.string.fdroid_url)
      ShareCompat.IntentBuilder(requireActivity())
        .setChooserTitle(R.string.support_development__share)
        .setType("text/plain")
        .setText("$text\n\n$playStoreURL\n$fdroidURL")
        .startChooser()

      app.analyticsProvider.logEvent("share_app_with_friends", bundleOf())
    }

    app.analyticsProvider.setCurrentScreen("support_development", SupportDevelopmentFragment::class)
  }
}
