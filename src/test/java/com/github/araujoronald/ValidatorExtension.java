package com.github.araujoronald;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidatorExtension implements ParameterResolver {

    private final Validator validator;

    public ValidatorExtension() {
        // Obtém a configuração padrão do provedor de validação
        Configuration<?> config = Validation.byDefaultProvider().configure();

        // Define o nome base do nosso arquivo de mensagens (ex: "messages.properties")
        config.addProperty("hibernate.validator.message_bundle", "messages");

        // Constrói a fábrica de validadores com a configuração customizada
        ValidatorFactory factory = config.buildValidatorFactory();

        // Obtém o validador
        this.validator = factory.getValidator();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == Validator.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return validator;
    }
}