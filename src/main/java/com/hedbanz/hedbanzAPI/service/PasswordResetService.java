package com.hedbanz.hedbanzAPI.service;

import com.hedbanz.hedbanzAPI.model.PasswordResetData;

public interface PasswordResetService {
    void generatePasswordResetKeyWord(PasswordResetData passwordResetData);

    boolean isValidUserKeyWord(PasswordResetData passwordResetData);

    void resetUserPassword(PasswordResetData passwordResetData);
}
