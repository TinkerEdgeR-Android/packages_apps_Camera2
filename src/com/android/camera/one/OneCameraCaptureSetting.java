/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.one;

import com.android.camera.async.Observable;
import com.android.camera.async.Observables;
import com.android.camera.debug.Log;
import com.android.camera.device.CameraId;
import com.android.camera.hardware.HardwareSpec;
import com.android.camera.one.OneCamera.Facing;
import com.android.camera.settings.Keys;
import com.android.camera.settings.ResolutionSetting;
import com.android.camera.settings.SettingObserver;
import com.android.camera.settings.SettingsManager;
import com.android.camera.util.Size;
import com.google.common.base.Function;

/**
 * Contains related settings to configure a camera for a particular type of
 * capture.
 */
public class OneCameraCaptureSetting {
    private final Size mCaptureSize;
    private final Observable<OneCamera.PhotoCaptureParameters.Flash> mFlashSetting;
    private final Observable<OneCamera.PhotoCaptureParameters.WhiteBalance> mWhiteBalanceSetting;
    private final Observable<Integer> mExposureSetting;
    private final Observable<Boolean> mHdrSceneSetting;
    private final boolean mIsHdrPlusEnabled;
    
    private static final Log.Tag TAG = new Log.Tag("OneCameraCaptureSetting");

    public static OneCameraCaptureSetting create(
            Size pictureSize,
            SettingsManager settingsManager,
            final HardwareSpec hardwareSpec,
            String cameraSettingScope,
            boolean isHdrPlusEnabled) {
        Log.d(TAG, "OneCameraCaptureSetting.create()");
        Observable<OneCamera.PhotoCaptureParameters.Flash> flashSetting;
        if (hardwareSpec.isFlashSupported()) {
            flashSetting = new FlashSetting(SettingObserver.ofString(
                        settingsManager, cameraSettingScope, Keys.KEY_FLASH_MODE));
        } else {
            flashSetting = new FlashSetting(Observables.of("off"));
        }
        Observable<Integer> exposureSetting = SettingObserver.ofInteger(
                settingsManager, cameraSettingScope, Keys.KEY_EXPOSURE);
        Observable<Boolean> hdrSceneSetting;
        if (hardwareSpec.isHdrSupported()) {
            hdrSceneSetting = SettingObserver.ofBoolean(settingsManager,
                    SettingsManager.SCOPE_GLOBAL, Keys.KEY_CAMERA_HDR);
        } else {
            hdrSceneSetting = Observables.of(false);
        }
        Observable<OneCamera.PhotoCaptureParameters.WhiteBalance> WbSetting = new WhiteBalanceSetting(
                SettingObserver.ofString(settingsManager, cameraSettingScope, Keys.KEY_WHITEBALANCE));
        return new OneCameraCaptureSetting(
                pictureSize,
                flashSetting,
                WbSetting,
                exposureSetting,
                hdrSceneSetting,
                isHdrPlusEnabled);
    }

    private OneCameraCaptureSetting(
            Size captureSize,
            Observable<OneCamera.PhotoCaptureParameters.Flash> flashSetting,
            Observable<OneCamera.PhotoCaptureParameters.WhiteBalance> wbSetting,
            Observable<Integer> exposureSetting,
            Observable<Boolean> hdrSceneSetting,
            boolean isHdrPlusEnabled) {
        mCaptureSize = captureSize;
        mFlashSetting = flashSetting;
        mWhiteBalanceSetting = wbSetting;
        mExposureSetting = exposureSetting;
        mHdrSceneSetting = hdrSceneSetting;
        mIsHdrPlusEnabled = isHdrPlusEnabled;
    }

    public Size getCaptureSize() {
        return mCaptureSize;
    }

    public Observable<OneCamera.PhotoCaptureParameters.Flash> getFlashSetting() {
        return mFlashSetting;
    }
    
    public Observable<OneCamera.PhotoCaptureParameters.WhiteBalance> getWhiteBalanceSetting() {
        return mWhiteBalanceSetting;
    }

    public Observable<Integer> getExposureSetting() {
        return mExposureSetting;
    }

    public Observable<Boolean> getHdrSceneSetting() {
        return mHdrSceneSetting;
    }

    public boolean isHdrPlusEnabled() {
        return mIsHdrPlusEnabled;
    }
}
