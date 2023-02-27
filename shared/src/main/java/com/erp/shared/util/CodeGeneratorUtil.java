package com.erp.shared.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class CodeGeneratorUtil {

    private final Random random = new Random();

    public String generateCode() {
        return String.format("%04d", random.nextInt(10000));
    }
}
