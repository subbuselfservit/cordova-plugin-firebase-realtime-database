package org.apache.cordova.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class FirebaseDatabasePlugin extends CordovaPlugin {
    private final String TAG = "FirebaseDatabasePlugin";

    private DatabaseReference mDatabase;
    private static WeakReference<CallbackContext> callbackContext;

    @Override
    protected void pluginInitialize() {
        final Context context = this.cordova.getActivity().getApplicationContext();
        this.cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Log.d(TAG, "Starting Firebase plugin");
                mDatabase = FirebaseDatabase.getInstance().getReference();
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("updateChildren")) {
            this.updateChildren(callbackContext, args.getString(0), args.getJSONObject(1));
            return true;
        } else if (action.equals("setValueBoolean")) {
            this.setValue(callbackContext, args.getString(0), args.getBoolean(1));
            return true;
        } else if (action.equals("setValueNumber")) {
            this.setValue(callbackContext, args.getString(0), args.getDouble(1));
            return true;
        } else if (action.equals("setValueString")) {
            this.setValue(callbackContext, args.getString(0), args.getString(1));
            return true;
        } else if (action.equals("setValue")) {
            this.setValue(callbackContext, args.getString(0), args.getJSONObject(1));
            return true;
        } else if (action.equals("getByteArray")) {
            if (args.length() > 1) this.getByteArray(callbackContext, args.getString(0), args.getString(1));
            else this.getByteArray(callbackContext, args.getString(0), null);
            return true;
        } else if (action.equals("getInfo")) {
            this.getInfo(callbackContext);
            return true;
        } else if (action.equals("setConfigSettings")) {
            this.setConfigSettings(callbackContext, args.getJSONObject(0));
            return true;
        }
        return false;
    }

    private void fetch(final CallbackContext callbackContext, final Task<Void> task) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            callbackContext.success();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            callbackContext.error(e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void getByteArray(final CallbackContext callbackContext, final String key, final String namespace) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    byte[] bytes = namespace == null ? FirebaseRemoteConfig.getInstance().getByteArray(key)
                            : FirebaseRemoteConfig.getInstance().getByteArray(key, namespace);
                    JSONObject object = new JSONObject();
                    object.put("base64", Base64.encodeToString(bytes, Base64.DEFAULT));
                    object.put("array", new JSONArray(bytes));
                    callbackContext.success(object);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void getInfo(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    FirebaseRemoteConfigInfo remoteConfigInfo = FirebaseRemoteConfig.getInstance().getInfo();
                    JSONObject info = new JSONObject();

                    JSONObject settings = new JSONObject();
                    settings.put("developerModeEnabled", remoteConfigInfo.getConfigSettings().isDeveloperModeEnabled());
                    info.put("configSettings", settings);

                    info.put("fetchTimeMillis", remoteConfigInfo.getFetchTimeMillis());
                    info.put("lastFetchStatus", remoteConfigInfo.getLastFetchStatus());

                    callbackContext.success(info);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void setConfigSettings(final CallbackContext callbackContext, final JSONObject config) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    boolean devMode = config.getBoolean("developerModeEnabled");
                    FirebaseRemoteConfigSettings.Builder settings = new FirebaseRemoteConfigSettings.Builder()
                            .setDeveloperModeEnabled(devMode);
                    FirebaseRemoteConfig.getInstance().setConfigSettings(settings.build());
                    callbackContext.success();
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    private void updateChildren(final CallbackContext callbackContext, final String path, final JSONObject updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              try {
                if (path != null) {
                  mDatabase.child(path).updateChildren(jsonObjectToMap(updates));
                } else {
                  mDatabase.updateChildren(jsonObjectToMap(updates));
                }
              } catch (JSONException e) {
                  callbackContext.error(e.getMessage());
              }
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final JSONObject updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              try {
                DatabaseReference myRef = mDatabase.child(path);

                myRef.setValue(jsonObjectToMap(updates));
              } catch (JSONException e) {
                  callbackContext.error(e.getMessage());
              }
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final String updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              DatabaseReference myRef = mDatabase.child(path);
              myRef.setValue(updates);
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final double updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              DatabaseReference myRef = mDatabase.child(path);
              myRef.setValue(updates);
            }
        });
    }

    private void setValue(final CallbackContext callbackContext, final String path, final boolean updates) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
              DatabaseReference myRef = mDatabase.child(path);
              myRef.setValue(updates);
            }
        });
    }

    private static Map<String, Object> jsonObjectToMap(JSONObject json) throws JSONException {
      Gson gson = new Gson();
      Map<String,Object> map = new HashMap<String,Object>();
      map = (Map<String,Object>) gson.fromJson(json.toString(), map.getClass());
      return map;
    }
}
