package com.sudhanva.library_management_v2.exceptions.LibraryExceptions;

public class NoLibrarySettingsAvailableException extends RuntimeException {

    public NoLibrarySettingsAvailableException() {
        super("No Settings available in the library.");
    }
}
