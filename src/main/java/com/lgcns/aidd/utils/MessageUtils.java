package com.lgcns.aidd.utils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lgcns.aidd.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.lgcns.aidd.exception.UserError;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import  jakarta.validation.ConstraintViolation;
import  lombok.AccessLevel;
import java.util.*;
import java.util.stream.Stream;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageUtils {

    private  static  final Map<Class<?>, String> mapClass;

    static {
        mapClass = new HashMap<>();

        //Number types
        Stream.of(int.class, Integer.class, long.class, Long.class)
                .forEach(mClass -> mapClass.put(mClass, "Number"));

        //Decimal types
        Stream.of(float.class, Float.class, double.class, Double.class)
                .forEach(mClass -> mapClass.put(mClass, "Decimal"));

        //Boolean type
        Stream.of(boolean.class, Boolean.class)
                .forEach(mClass -> mapClass.put(mClass, "Boolean"));

        //String type
        Stream.of(String.class)
                .forEach(mClass -> mapClass.put(mClass, "String"));
    }

    public static ErrorMessage toError(JsonMappingException e) {
        String message;
        if (e instanceof InvalidFormatException invalidFormatException) {
            message = toMessage(invalidFormatException.getTargetType(),
                    invalidFormatException.getValue());
        } else {
            message = "Invalid JSON mapping: " + e.getMessage();
        }
        return ErrorMessage.builder()
                .addMessage(message)
                .build();
    }

    private static String toMessage(Class<?> targetType, Object inputValue) {
        String targetClass = mapClass.get(targetType);
        if(targetClass != null) {
            if(inputValue != null) {
                var inputClass = mapClass.get(inputValue.getClass());
                if(inputClass != null) {
                    return "Can't convert %s value to %s".formatted(inputValue, targetClass);
                }
            }
            return "Can't convert % value to %s".formatted(inputValue, targetClass);
        }

        if(targetType.isArray() || Collection.class.isAssignableFrom(targetType)) {
            return "Can't convert value to List";
        }

        if(targetType.isEnum()) {
            var validValues = Arrays.stream(targetType.getEnumConstants())
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            return "Can't convert value % in enum (%s):".formatted(inputValue, validValues);
        }

        return "Unspupported type conversion for " + targetType.getSimpleName();
    }

}
