package de.oliverpabst.pqt;

import java.util.HashMap;

public class SettingsStore {

    private HashMap<String, String> settings;

    private static SettingsStore instance = null;

    private SettingsStore() {
        settings = new HashMap<>();
    }

    private static SettingsStore getInstance() {
        if(instance == null) {
            instance = new SettingsStore();
        }
        return instance;
    }

    private String getSetting(String _key) {
        return settings.get(_key);
    }

    private Boolean changeSetting(String _key, String _value) {
        if(!settings.containsKey(_key)) {
            return false;
        } else {
            settings.put(_key, _value);
            return true;
        }
    }

    private Boolean addSetting(String _key, String _value) {
        if(settings.containsKey(_key)) {
            return false;
        } else {
            settings.put(_key, _value);
            return true;
        }
    }
}
