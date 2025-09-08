@Configuration
public class WebCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "https://java-debugger-system-frontend.onrender.com",
                            "http://localhost:3000"
                        )
                        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS");
            }
        };
    }
}
