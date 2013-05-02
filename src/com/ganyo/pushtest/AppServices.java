package com.ganyo.pushtest;

import android.content.Context;

import android.util.Log;
import org.usergrid.android.client.Client;
import org.usergrid.android.client.callbacks.ApiResponseCallback;
import org.usergrid.android.client.callbacks.DeviceRegistrationCallback;
import org.usergrid.java.client.entities.Device;
import org.usergrid.java.client.entities.Entity;
import org.usergrid.java.client.response.ApiResponse;
import org.usergrid.java.client.utils.JsonUtils;

import java.util.HashMap;

import static com.ganyo.pushtest.Util.*;
import static com.ganyo.pushtest.Settings.*;

public final class AppServices {

  private static Client client;
  private static Device device;

  static synchronized Client getClient() {
    if (client == null) {
      client = new Client();
      client.setApiUrl(API_URL);
      client.setOrganizationId(ORG);
      client.setApplicationId(APP);
    }
    return client;
  }

  static void login(final Context context) {

    if (USER != null) {
      getClient().authorizeAppUserAsync(USER, PASSWORD, new ApiResponseCallback() {

        @Override
        public void onResponse(ApiResponse apiResponse) {
          Log.i(TAG, "login response: " + apiResponse);
          registerPush(context);
        }

        @Override
        public void onException(Exception e) {
          displayMessage(context, "Login Exception: " + e);
          Log.i(TAG, "login exception: " + e);
        }
      });
    } else {
      registerPush(context);
    }
  }

  /**
   * Register this user/device pair on App Services.
   */
  static void register(final Context context, final String regId) {
    Log.i(TAG, "registering device: " + regId);

    getClient().registerDeviceForPushAsync(context, NOTIFIER, regId, null, new DeviceRegistrationCallback() {

      @Override
      public void onResponse(Device device) {
        Log.i(TAG, "register response: " + device);
        AppServices.device = device;
        displayMessage(context, "Device registered as: " + regId);

        // connect Device to current User - if there is one
        if (getClient().getLoggedInUser() != null) {
          getClient().connectEntitiesAsync("users", getClient().getLoggedInUser().getUuid().toString(),
                                           "devices", device.getUuid().toString(),
                                           new ApiResponseCallback() {
            @Override
            public void onResponse(ApiResponse apiResponse) {
              Log.i(TAG, "connect response: " + apiResponse);
            }

            @Override
            public void onException(Exception e) {
              displayMessage(context, "Connect Exception: " + e);
              Log.i(TAG, "connect exception: " + e);
            }
          });
        }
      }

      @Override
      public void onException(Exception e) {
    	displayMessage(context, "Register Exception: " + e);
        Log.i(TAG, "register exception: " + e);
      }

      @Override
      public void onDeviceRegistration(Device device) { /* this won't ever be called */ }
    });
  }

  static void sendMyselfANotification(final Context context) {
    if (device == null) {
      displayMessage(context, "Device not registered (yet?)");
    }
    else {
      String entityPath = "devices/" + device.getUuid().toString() + "/notifications";
      Entity notification = new Entity(entityPath);

      HashMap<String,String> payloads = new HashMap<String, String>();
      payloads.put("google", "Hi there!");
      notification.setProperty("payloads", JsonUtils.toJsonNode(payloads));
      getClient().createEntityAsync(notification, new ApiResponseCallback() {

        @Override
        public void onResponse(ApiResponse apiResponse) {
          Log.i(TAG, "send response: " + apiResponse);
        }

        @Override
        public void onException(Exception e) {
          displayMessage(context, "Send Exception: " + e);
          Log.i(TAG, "send exception: " + e);
        }
      });
    }
  }

  /**
   * Unregister this device within the server.
   */
  static void unregister(final Context context, final String regId) {
    Log.i(TAG, "unregistering device: " + regId);
    register(context, "");
  }
}
