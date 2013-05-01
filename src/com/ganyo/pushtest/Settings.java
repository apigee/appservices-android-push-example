package com.ganyo.pushtest;

import org.usergrid.android.client.Client;

/** Change these values to match your setup! */
public class Settings {

  // Google Client Id from Google API Console
  static final String GCM_SENDER_ID = "978423379483";

  // Notifier Name created in app services
  static final String NOTIFIER = "google";

//  static final String API_URL = Client.PUBLIC_API_URL;
  static final String API_URL = "http://10.0.0.3:8080";

  static final String ORG = "test-organization";
  static final String APP = "test-app";

  // set these if you want to use a user login
  static final String USER = null;
  static final String PASSWORD = null;
}
