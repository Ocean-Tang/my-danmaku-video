package com.study.mydanmakuvideo.common.convertors;

import com.study.mydanmakuvideo.common.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToBaseEnumConverter<>(targetType);
    }

    private static class StringToBaseEnumConverter<T extends BaseEnum> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToBaseEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            for (T enumConstant : enumType.getEnumConstants()) {
                if (enumConstant.getDetail().equals(source)) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("错误的枚举值：" + source + "——" + enumType.getSimpleName());
        }
    }
}
