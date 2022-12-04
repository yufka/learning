package com.osa.learning.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author oleksii
 * @since 29 Nov 2022
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String s) {
        super(s);
    }
}
