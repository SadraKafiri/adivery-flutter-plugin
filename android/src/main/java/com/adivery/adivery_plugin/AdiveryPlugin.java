package com.adivery.adivery_plugin;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.adivery.sdk.Adivery;
import com.adivery.sdk.AdiveryInterstitialCallback;
import com.adivery.sdk.AdiveryLoadedAd;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

/**
 * AdiveryPlugin
 */
public class AdiveryPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;

    static Context context;

    static Activity activity;

    static BinaryMessenger messenger;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "adivery_plugin");
        channel.setMethodCallHandler(this);

        context = flutterPluginBinding.getApplicationContext();

        messenger = flutterPluginBinding.getBinaryMessenger();

        // factory for banner ad.
        flutterPluginBinding.getPlatformViewRegistry()
                .registerViewFactory("adivery/bannerAd",
                        new AdiveryAdViewFactory(flutterPluginBinding.getBinaryMessenger()));
    }


    // handling flutter api v1
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "adivery_plugin");
        channel.setMethodCallHandler(new AdiveryPlugin());

        context = registrar.activity().getApplication();

        messenger = registrar.messenger();

        // factory for banner ad.
        registrar.platformViewRegistry()
                .registerViewFactory("adivery/bannerAd",
                        new AdiveryAdViewFactory(registrar.messenger()));
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "initialize":
                Adivery.configure((Application) context, (String) call.argument("appId"));
                break;
            case "interstitial":
                requestInterstitialAd((String) call.argument("placement_id"),
                        (String) call.argument("id"));
                break;
            case "rewarded":
                requestRewardedAd((String)call.argument("placement_id"), (String) call.argument("id"));
                break;
            case "native":
                requestNativeAd((String)call.argument("placement_id"), (String) call.argument("id"));
                break;
            default:
                result.notImplemented();
        }
        result.success(true);
    }

    private void requestNativeAd(String placementId ,String id) {
        new NativeAd(activity,placementId,id,messenger);
    }

    private void requestRewardedAd(String placementId,String id) {
        new RewardedAd(activity,placementId,id,messenger);
    }

    private void requestInterstitialAd(String placementId, String id) {
        new InterstitialAd(activity, placementId, id, messenger);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
