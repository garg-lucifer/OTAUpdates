package com.awesomeproject

import android.app.Application
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
import android.content.Context
import android.content.SharedPreferences
import java.io.File
import android.util.Log

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
          val file = File(applicationContext.filesDir, "index.android.bundle")
          return if (file.exists()) {
              file.absolutePath
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
