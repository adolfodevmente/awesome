package com.test.test.awesomeproject;

import android.app.Application;
import android.content.Context;

import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin;
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin;
import com.facebook.flipper.plugins.fresco.FrescoFlipperPlugin;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.flipper.plugins.react.ReactFlipperPlugin;
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin;
import com.facebook.react.ReactInstanceEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.network.NetworkingModule;
import com.test.test.awesomeproject.BuildConfig;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.soloader.SoLoader;
import com.test.test.awesomeproject.newarchitecture.MainApplicationReactNativeHost;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // packages.add(new MyReactNativePackage());
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };

  private final ReactNativeHost mNewArchitectureNativeHost =
      new MainApplicationReactNativeHost(this);

  @Override
  public ReactNativeHost getReactNativeHost() {
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      return mNewArchitectureNativeHost;
    } else {
      return mReactNativeHost;
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // If you opted-in for the New Architecture, we enable the TurboModule system
    ReactFeatureFlags.useTurboModules = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
    SoLoader.init(this, /* native exopackage */ false);
    initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
  }

  /**
   * Loads Flipper in React Native templates. Call this in the onCreate method with something like
   * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
   *
   * @param context
   * @param reactInstanceManager
   */
  private static void initializeFlipper(
      Context context, ReactInstanceManager reactInstanceManager) {
    if (BuildConfig.DEBUG) {
      try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
        Class<?> aClass = Class.forName("com.test.test.awesomeproject.MainApplication$ReactNativeFlipper");
        aClass
            .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
            .invoke(null, context, reactInstanceManager);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

    public static class ReactNativeFlipper {
      public static void initializeFlipper(Context context, ReactInstanceManager reactInstanceManager) {
        if (FlipperUtils.shouldEnableFlipper(context)) {
          final FlipperClient client = AndroidFlipperClient.getInstance(context);

          client.addPlugin(new InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()));
          client.addPlugin(new ReactFlipperPlugin());
          client.addPlugin(new DatabasesFlipperPlugin(context));
          client.addPlugin(new SharedPreferencesFlipperPlugin(context));
          client.addPlugin(CrashReporterPlugin.getInstance());

          NetworkFlipperPlugin networkFlipperPlugin = new NetworkFlipperPlugin();
          NetworkingModule.setCustomClientBuilder(
              new NetworkingModule.CustomClientBuilder() {
                @Override
                public void apply(OkHttpClient.Builder builder) {
                  builder.addNetworkInterceptor(new FlipperOkhttpInterceptor(networkFlipperPlugin));
                }
              });
          client.addPlugin(networkFlipperPlugin);
          client.start();

          // Fresco Plugin needs to ensure that ImagePipelineFactory is initialized
          // Hence we run if after all native modules have been initialized
          ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
          if (reactContext == null) {
            reactInstanceManager.addReactInstanceEventListener(
                new ReactInstanceEventListener() {
                  @Override
                  public void onReactContextInitialized(ReactContext reactContext) {
                    reactInstanceManager.removeReactInstanceEventListener(this);
                    reactContext.runOnNativeModulesQueueThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            client.addPlugin(new FrescoFlipperPlugin());
                          }
                        });
                  }
                });
          } else {
            client.addPlugin(new FrescoFlipperPlugin());
          }
        }
      }
    }
}
