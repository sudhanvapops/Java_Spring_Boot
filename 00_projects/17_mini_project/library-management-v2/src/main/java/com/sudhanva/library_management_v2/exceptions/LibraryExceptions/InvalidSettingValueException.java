package com.sudhanva.library_management_v2.exceptions.LibraryExceptions;

import com.sudhanva.library_management_v2.enums.Setting.SettingKey;
import com.sudhanva.library_management_v2.enums.Setting.SettingValueType;

public class InvalidSettingValueException extends RuntimeException{
    public InvalidSettingValueException(SettingKey key, SettingValueType valueType, String value) {
        super("Invalid value '" + value + "' for setting " + key + ": expected a " + valueType + " value");
    }
}
