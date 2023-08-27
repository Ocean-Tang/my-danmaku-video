package com.study.mydanmakuvideo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JwtToken
 *
 * @author Ocean
 * 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    public String accessToken;
    public String refreshToken;
}
