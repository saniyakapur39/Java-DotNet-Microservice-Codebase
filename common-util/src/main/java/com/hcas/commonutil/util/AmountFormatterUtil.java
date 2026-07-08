package com.hcas.commonutil.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormatterUtil {
    public static String formatAmount(BigDecimal amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        return nf.format(amount);
    }
}