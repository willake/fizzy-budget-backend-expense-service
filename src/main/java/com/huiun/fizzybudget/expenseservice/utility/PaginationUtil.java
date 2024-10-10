package com.huiun.fizzybudget.expenseservice.utility;

import java.util.Base64;

public class PaginationUtil {
    public static Long decodeCursor(String cursor) {
        return Long.parseLong(new String(Base64.getDecoder().decode(cursor)));
    }

    public static String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(id.toString().getBytes());
    }
}
