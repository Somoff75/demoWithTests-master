package com.example.demowithtests.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NewUserNameIsNotSetException extends RuntimeException{

}
