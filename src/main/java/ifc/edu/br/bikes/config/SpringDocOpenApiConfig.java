package ifc.edu.br.bikes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Bikes API")
                        .description("API para gestão de locadora de bikes.")
                        .version("V1")
                        .contact(new Contact()
                                .name("Samuel")
                                .email("samuelcastilhopereira@gmail.com")
                        )
                );
    }
}
