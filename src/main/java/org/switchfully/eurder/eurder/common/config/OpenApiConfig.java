package org.switchfully.eurder.eurder.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eurderOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eurder API")
                        .description("Order management back end: customers, items and orders.")
                        .version("v1")
                        .contact(new Contact().name("NoahVBDev").email("noahvanbrabant@hotmail.com")))
                .tags(List.of(
                        new Tag().name("Customers").description("Create and view customers"),
                        new Tag().name("Items").description("Manage items and stock"),
                        new Tag().name("Orders").description("Place, view and reorder orders")));
    }
}
