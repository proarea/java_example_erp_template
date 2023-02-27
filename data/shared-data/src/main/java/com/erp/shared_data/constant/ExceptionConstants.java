package com.erp.shared_data.constant;

public class ExceptionConstants {

    /**
     *  CORE
     */
    public static final String EMAIL_EXISTS = "User with email: %s already exists";
    public static final String PHONE_EXISTS = "User with phone: %s already exists";
    public static final String USER_NOT_EXISTS = "User with id: %s does not exist";
    public static final String TOKEN_NOT_EXISTS = "Token with userId: %s does not exist";
    public static final String EMAIL_NOT_EXISTS = "User with email: %s does not exist";
    public static final String CONFIRMATION_NOT_EXISTS = "Conformation with userId: %s does not exist";
    public static final String CODE_IS_EXPIRED = "Confirmation code is expired";
    public static final String INVALID_CODE = "Code is invalid";
    public static final String CODE_RESEND_NOT_AVAILABLE = "Resend code will be available at %s";
    public static final String INVALID_USER_STATUS = "User with id: %s does not have status: %s";

    /**
     * MEDIA
     */
    public static final String UNABLE_TO_LOAD_FILE_EXCEPTION = "Unable to load file";
    public static final String FILE_NOT_FOUND = "File with url %s not found in Amazon S3";

    private ExceptionConstants() {

    }
}
