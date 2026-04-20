package de.oliverpabst.pqt;

import java.util.HashMap;

public class SettingsStore {

    private HashMap<String, String> settings;

    private static final SettingsStore instance = new SettingsStore();

    private SettingsStore() {
        settings = new HashMap<>();
    }

    public static SettingsStore getInstance() {
        return instance;
    }

    public String getSetting(String _key) {
        return settings.get(_key);
    }

    public Boolean changeSetting(String _key, String _value) {
        if(!settings.containsKey(_key)) {
            return false;
        } else {
            settings.put(_key, _value);
            return true;
        }
    }

    public Boolean addSetting(String _key, String _value) {
        if(settings.containsKey(_key)) {
            return false;
        } else {
            settings.put(_key, _value);
            return true;
        }
    }
}
