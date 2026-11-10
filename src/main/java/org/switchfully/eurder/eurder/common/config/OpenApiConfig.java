package org.switchfully.eurder.eurder.common.config;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eurderOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .info(new Info()
                        .title("Eurder API")
                        .description("Order management back end: customers, items and orders.")
                        .version("v1")
                        .contact(new Contact().name("NoahVBDev").email("noahvanbrabant@hotmail.com")))
                .tags(List.of(
                        new Tag().name("Customers").description("Create and view customers"),
                        new Tag().name("Items").description("Add items"),
                        new Tag().name("Orders").description("Place orders")));
    }
}
