package com.cydeo.exeption;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ExceptionWrapper {

    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private String path;
    private List<ValidationException> validationExceptionList;
    private Integer errorCount;

    public ExceptionWrapper(Integer status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }


}
