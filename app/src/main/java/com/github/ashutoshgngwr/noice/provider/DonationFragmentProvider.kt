package com.github.ashutoshgngwr.noice.provider

import androidx.fragment.app.Fragment
import com.github.ashutoshgngwr.noice.fragment.OpenCollectiveDonationFragment

/**
 * [DonationFragmentProvider] provides an interface to add different variants of donate buttons in
 * support development fragment based on the implementation used by the application.
 */
interface DonationFragmentProvider {

  /**
   * Returns a new instance of a donation fragment.
   */
  fun get(): Fragment

  companion object {
    val IN_APP_DONATION_PRODUCTS = listOf(
      "donate_usd1", "donate_usd2", "donate_usd5",
      "donate_usd10", "donate_usd15", "donate_usd25",
    )
  }
}

object OpenCollectiveDonationFragmentProvider : DonationFragmentProvider {

  override fun get(): Fragment {
    return OpenCollectiveDonationFragment()
  }
}
