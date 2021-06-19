package com.example.reactnativetelinkble

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.facebook.react.*
import com.facebook.soloader.SoLoader
import com.reactnativetelinkble.TelinkBleModule
import com.reactnativetelinkble.TelinkBlePackage
import com.telink.ble.mesh.foundation.EventBus
import java.lang.reflect.InvocationTargetException

class MainApplication : Application(), ReactApplication {
  private var mOfflineCheckHandler: Handler? = null

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this,  /* native exopackage */false)
    initializeFlipper(
      this,
      reactNativeHost.reactInstanceManager
    ) // Remove this line if you don't want Flipper enabled

    // 2018-11-20T10:05:20-08:00
    // 2020-07-27T15:15:29+0800
    val offlineCheckThread = HandlerThread("offline check thread")
    offlineCheckThread.start()
    mOfflineCheckHandler = Handler(offlineCheckThread.looper)
    TelinkBleModule.setEventBus(EventBus())
  }

  private val mReactNativeHost: ReactNativeHost = object : ReactNativeHost(this) {
    override fun getUseDeveloperSupport(): Boolean {
      return BuildConfig.DEBUG
    }

    override fun getPackages(): List<ReactPackage> {
      val packages: MutableList<ReactPackage> = PackageList(this).packages
      // Packages that cannot be autolinked yet can be added manually here, for TelinkBleExample:
      // packages.add(new MyReactNativePackage());
      packages.add(TelinkBlePackage())
      return packages
    }

    override fun getJSMainModuleName(): String {
      return "index"
    }
  }

  override fun getReactNativeHost(): ReactNativeHost {
    return mReactNativeHost
  }

  companion object {
    /**
     * Loads Flipper in React Native templates.
     *
     * @param context
     */
    private fun initializeFlipper(context: Context, reactInstanceManager: ReactInstanceManager) {
      if (BuildConfig.DEBUG) {
        try {
          /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
          val aClass = Class.forName("com.reactnativetelinkbleExample.ReactNativeFlipper")
          aClass
            .getMethod("initializeFlipper", Context::class.java, ReactInstanceManager::class.java)
            .invoke(null, context, reactInstanceManager)
        } catch (e: ClassNotFoundException) {
          e.printStackTrace()
        } catch (e: NoSuchMethodException) {
          e.printStackTrace()
        } catch (e: IllegalAccessException) {
          e.printStackTrace()
        } catch (e: InvocationTargetException) {
          e.printStackTrace()
        }
      }
    }
  }
}
