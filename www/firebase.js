var exec = require('cordova/exec');

exports.ref = function(path) {
  var _path = path;
  function FbDbRef() {  }

  FbDbRef.setValue = function(updates) { return exports.setValue(_path, updates); };
  FbDbRef.updateChildren = function(updates) { return exports.updateChildren(_path, updates); };
  FbDbRef.child = function(child_path) { return exports.ref(_path + "/" + child_path); };

  return $;
}

exports.getInstanceId = function(success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "getInstanceId", []);
};

exports.updateChildren = function(path, updates) {
  return new Promise(function(success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "updateChildren", [path, updates]);
  });
};

exports.setValue = function(path, updates) {
  return new Promise(function(success, error) {
    if (typeof updates == "boolean") {
      exec(success, error, "FirebaseDatabasePlugin", "setValueBoolean", [path, updates]);
    } else if (typeof updates == "number") {
      exec(success, error, "FirebaseDatabasePlugin", "setValueNumber", [path, updates]);
    } else if (typeof updates == "string") {
      exec(success, error, "FirebaseDatabasePlugin", "setValueString", [path, updates]);
    } else {
      exec(success, error, "FirebaseDatabasePlugin", "setValue", [path, updates]);
    }
  });
};

exports.onTokenRefreshNotification = function(success, error) {
    exec(success, error, "FirebasePlugin", "onTokenRefreshNotification", []);
};

exports.grantPermission = function(success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "grantPermission", []);
};

exports.subscribe = function(topic, success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "subscribe", [topic]);
};

exports.unsubscribe = function(topic, success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "unsubscribe", [topic]);
};

exports.logEvent = function(name, params, success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "logEvent", [name, params]);
};

exports.setUserId = function(id, success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "setUserId", [id]);
};

exports.fetch = function (cacheExpirationSeconds, success, error) {
    var args = [];
    if (typeof cacheExpirationSeconds === 'number') {
        args.push(cacheExpirationSeconds);
    } else {
        error = success;
        success = cacheExpirationSeconds;
    }
    exec(success, error, "FirebaseDatabasePlugin", "fetch", args);
};

exports.getByteArray = function (key, namespace, success, error) {
    var args = [key];
    if (typeof namespace === 'string') {
        args.push(namespace);
    } else {
        error = success;
        success = namespace;
    }
    exec(success, error, "FirebaseDatabasePlugin", "getByteArray", args);
};

exports.getValue = function (key, namespace, success, error) {
    var args = [key];
    if (typeof namespace === 'string') {
        args.push(namespace);
    } else {
        error = success;
        success = namespace;
    }
    exec(success, error, "FirebaseDatabasePlugin", "getValue", args);
};

exports.getInfo = function (success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "getInfo", []);
};

exports.setConfigSettings = function (settings, success, error) {
    exec(success, error, "FirebaseDatabasePlugin", "setConfigSettings", [settings]);
};

exports.setDefaults = function (defaults, namespace, success, error) {
    var args = [defaults];
    if (typeof namespace === 'string') {
        args.push(namespace);
    } else {
        error = success;
        success = namespace;
    }
    exec(success, error, "FirebaseDatabasePlugin", "setDefaults", args);
};

exports.setDatabasePersistent = function(persistent, success, error) {
    exec(success, error, "FirebasePlugin", "setDatabasePersistent", [persistent]);
};
