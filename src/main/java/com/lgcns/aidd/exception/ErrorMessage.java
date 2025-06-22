package com.lgcns.aidd.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = ErrorMessage.ErrorMessageSerializer.class)
public class ErrorMessage {
    private String message;
    private List<UserError> userErrors;

    public ErrorMessage() {}

    public static ErrorMessageBuilder builder() {
        return new ErrorMessageBuilder();
    }

    public static class ErrorMessageBuilder {
        private String message;
        private List<UserError> userErrors;

        public ErrorMessageBuilder addMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorMessageBuilder addErrors(UserError userError) {
            if (this.userErrors == null) {
                this.userErrors = new ArrayList<>();
            }
            this.userErrors.add(userError);
            return this;
        }

        public ErrorMessage build() {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.message = this.message;
            errorMessage.userErrors = this.userErrors;
            return errorMessage;
        }
    }

    public static class ErrorMessageSerializer extends JsonSerializer<ErrorMessage> {
        @Override
        public void serialize(ErrorMessage errorMessage, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            if (StringUtils.isNotBlank(errorMessage.message)) {
                gen.writeStartObject();
                gen.writeFieldName("message");
                gen.writeString(errorMessage.message);
                gen.writeEndObject();
                return;
            }

            var namingStrategy = serializerProvider.getConfig().getPropertyNamingStrategy();

            if (errorMessage.userErrors != null && !errorMessage.userErrors.isEmpty()) {
                gen.writeStartArray();
                for (var error : errorMessage.userErrors) {
                    gen.writeStartObject();
                    String code = namingStrategy != null
                            ? namingStrategy.nameForField(null, null, error.getCode())
                            : error.getCode();
                    gen.writeStringField("code", code);
                    gen.writeStringField("message", error.getMessage());
                    if (!CollectionUtils.isEmpty(error.getFields())) {
                        gen.writeFieldName("fields");
                        gen.writeStartArray();
                        for (var field : error.getFields()) {
                            String fieldName = namingStrategy != null
                                    ? namingStrategy.nameForField(null, null, field)
                                    : field;
                            gen.writeString(fieldName);
                        }
                        gen.writeEndArray();
                    }
                    gen.writeEndObject();
                }
                gen.writeEndArray();
                return;
            }

            gen.writeStartObject();
            gen.writeStringField("message", "no message");
            gen.writeEndObject();
        }
    }
}
