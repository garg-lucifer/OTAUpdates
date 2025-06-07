package com.awesomeproject

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.react.soloader.OpenSourceMergedSoMapping
import com.facebook.soloader.SoLoader

import java.io.File


class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost =
      object : DefaultReactNativeHost(this) {
        override fun getPackages(): List<ReactPackage> =
            PackageList(this).packages.apply {
              // Packages that cannot be autolinked yet can be added manually here
              // add(MyReactNativePackage())
            }

        override fun getJSMainModuleName(): String {
          // val prefs: SharedPreferences = applicationContext.getSharedPreferences("BUNDLE_PREFS", Context.MODE_PRIVATE)
          // val useVariant: Boolean = prefs.getBoolean("use_variant", false)

          // val filesDir: File = applicationContext.filesDir
          // val jsBundle: File = File(filesDir, if (useVariant) "index2.android.bundle" else "index.android.bundle")

          return "index"
        }

        override fun getJSBundleFile(): String? {
          // Get version string from file
          val versionFile = File(applicationContext.filesDir, "version.txt")
          val version = if (versionFile.exists()) versionFile.readText().trim() else null
      
          Log.d("VERSION", "Using version: $version")
      
          // Now resolve the actual bundle file
          val jsBundleFile = version?.let { File(applicationContext.filesDir, it) }
      
          return if (jsBundleFile != null && jsBundleFile.exists()) {
              jsBundleFile.absolutePath
          } else {
              null
          }
      }
      

        override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

        override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
        override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
      }

  override val reactHost: ReactHost
    get() = getDefaultReactHost(applicationContext, reactNativeHost)

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, OpenSourceMergedSoMapping)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      load()
    }
  }
}
