package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.RoomError;
import com.hedbanz.hedbanzAPI.error.UserError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class ExceptionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionFactory.class);

    public static RoomException create(final Throwable cause, final RoomError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new RoomException (error, cause, messageArguments);
    }

    public static RoomException create(final RoomError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new RoomException(error, messageArguments);
    }

    public static UserException create(final Throwable cause, final UserError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new UserException (error, cause, messageArguments);
    }

    public static UserException create(final UserError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new UserException(error, messageArguments);
    }
}
