package com.library.common;

public final class Constants {
    private Constants() {}

    public static final int DEFAULT_MAX_BORROW = 5;
    public static final int DEFAULT_BORROW_DAYS = 30;
    public static final int MAX_RENEW_COUNT = 1;
    public static final int RESERVE_HOLD_DAYS = 3;

    /** 复本状态 */
    public static final int STOCK_AVAILABLE = 0;
    public static final int STOCK_BORROWED = 1;
    public static final int STOCK_DAMAGED = 2;
    public static final int STOCK_RESERVED = 3;

    /** 借阅状态 */
    public static final int BORROW_ACTIVE = 0;
    public static final int BORROW_RETURNED = 1;
    public static final int BORROW_OVERDUE = 2;
    public static final int BORROW_RENEWED = 3;

    /** 读者状态 */
    public static final int READER_NORMAL = 0;
    public static final int READER_SUSPENDED = 1;
    public static final int READER_CANCELLED = 2;

    /** 用户状态 */
    public static final int USER_DISABLED = 0;
    public static final int USER_ENABLED = 1;
}
