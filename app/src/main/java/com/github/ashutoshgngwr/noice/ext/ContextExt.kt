package com.github.ashutoshgngwr.noice.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.github.ashutoshgngwr.noice.R
import com.google.android.material.elevation.SurfaceColors
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Launches a [CustomTabsIntent] with default application theme ([R.style.Theme_App]) for the given
 * [uri].
 */
fun Context.startCustomTab(uri: String) {
  val tbColor = SurfaceColors.SURFACE_2.getColor(this)
  val colorParams = CustomTabColorSchemeParams.Builder()
    .setToolbarColor(tbColor)
    .setNavigationBarColor(tbColor)
    .build()

  val customTabsIntent = CustomTabsIntent.Builder()
    .setDefaultColorSchemeParams(colorParams)
    .build()

  customTabsIntent.launchUrl(this, Uri.parse(uri))
}

/**
 * Launches a [CustomTabsIntent] with default application theme ([R.style.Theme_App]) for the given
 * [uri string resource][uriStringRes].
 */
fun Context.startCustomTab(@StringRes uriStringRes: Int) {
  startCustomTab(getString(uriStringRes))
}

/**
 * Starts an activity with [Intent] action [Settings.ACTION_APPLICATION_DETAILS_SETTINGS] for the
 * current context's package name.
 */
fun Context.startAppDetailsSettingsActivity() {
  try {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      .setData(Uri.fromParts("package", packageName, null))
      .also { startActivity(it) }
  } catch (e: ActivityNotFoundException) {
    Log.w(this::class.simpleName, "startAppDetailsSettingsActivity: failed to start activity", e)
  }
}

/**
 * Returns a [callback flow][callbackFlow] that watches and emits device's internet connectivity
 * status for as long as it has a collector.
 */
fun Context.getInternetConnectivityFlow(): Flow<Boolean> = callbackFlow {
  val connectivityManager = requireNotNull(getSystemService<ConnectivityManager>())
  trySend(
    // emit initial state
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
      @Suppress("DEPRECATION")
      connectivityManager.activeNetworkInfo?.isConnectedOrConnecting
    } ?: false
  )

  // observe network changes
  val hasInternet = hashMapOf<Network, Boolean>()
  val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
      hasInternet[network] = true
      notifyChanged()
    }

    override fun onLost(network: Network) {
      hasInternet.remove(network)
      notifyChanged()
    }

    override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
      hasInternet[network] = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      notifyChanged()
    }

    private fun notifyChanged() {
      trySend(hasInternet.values.any { it })
    }
  }

  val networkRequest = NetworkRequest.Builder()
    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    .build()

  connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
  awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
}

fun Context.hasSelfPermission(permission: String): Boolean {
  return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
