package com.sudhanva.library_management_v2.exceptions.LibraryExceptions;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;

public class SettingNotFoundException extends RuntimeException {

    private SettingNotFoundException(String message) {
        super(message);
    }

    public static SettingNotFoundException byKey(SettingKey key) {
        return new SettingNotFoundException("Setting not found with key: " + key);
    }
}
