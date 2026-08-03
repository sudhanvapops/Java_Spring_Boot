package com.sudhanva.library_management_v2.exceptions.LibraryExceptions;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;

public class SettingAlreadyExistsException extends RuntimeException {

    public SettingAlreadyExistsException(SettingKey key) {
        super("Setting already exists with key: " + key);
    }
}
