/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.Giddh.ui.Activities;

import android.util.Log;

import com.Giddh.commonUtilities.CommonUtility;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * This example shows how to fetch tokens if you are creating a foreground task/activity and handle
 * auth exceptions.
 */
public class GetNameInForeground extends AbstractGetNameTask {
    public GetNameInForeground(GoogleLoginActivity activity, String email, String scope) {
        super(activity, email, scope);
    }

    @Override
    protected void onPostExecute(Void result) {
        CommonUtility.dialog.dismiss();
        mActivity.finish();
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
        CommonUtility.show_PDialog(mActivity);
        super.onPreExecute();
    }

    @Override
    protected String fetchToken() throws IOException {
        try {
            String token = GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            Log.e("gmail token ", "" + token);
            mActivity.setToken(token);
            return token;
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present, which is
            // recoverable, so we need to show the user some UI through the activity.
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
        }
        return null;
    }
}
