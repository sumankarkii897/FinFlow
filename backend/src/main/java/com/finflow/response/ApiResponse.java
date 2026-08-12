package com.finflow.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ApiResponse<T> {

    private Integer status;

    private String message;

    private LocalDateTime timestamp;

    private T data;
}
