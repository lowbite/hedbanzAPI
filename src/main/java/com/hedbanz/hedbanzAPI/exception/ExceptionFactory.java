package com.hedbanz.hedbanzAPI.exception;

import com.hedbanz.hedbanzAPI.error.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class ExceptionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionFactory.class);

    public static InputException create(final Throwable cause, final InputError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new InputException (error, cause, messageArguments);
    }

    public static InputException create(final InputError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new InputException(error, messageArguments);
    }

    public static NotFoundException create(final Throwable cause, final NotFoundError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new NotFoundException (error, cause, messageArguments);
    }

    public static NotFoundException create(final NotFoundError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new NotFoundException(error, messageArguments);
    }

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

    public static FcmException create(final Throwable cause, final FcmError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new FcmException (error, cause, messageArguments);
    }

    public static FcmException create(final FcmError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new FcmException(error, messageArguments);
    }

    public static PasswordResetException create(final Throwable cause, final PasswordResetError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new PasswordResetException (error, cause, messageArguments);
    }

    public static PasswordResetException create(final PasswordResetError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new PasswordResetException(error, messageArguments);
    }

    public static MessageException create(final Throwable cause, final MessageError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new MessageException (error, cause, messageArguments);
    }

    public static MessageException create(final MessageError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new MessageException(error, messageArguments);
    }

    public static AuthenticationException create(final Throwable cause, final AuthenticationError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments), cause);
        return new AuthenticationException (error, cause, messageArguments);
    }

    public static AuthenticationException create(final AuthenticationError error, final Object... messageArguments) {
        LOG.error(MessageFormat.format(error.getErrorMessage(), messageArguments));
        return new AuthenticationException(error, messageArguments);
    }
}
