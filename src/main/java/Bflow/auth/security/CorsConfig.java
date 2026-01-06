package Bflow.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //Uso de cookies
        corsConfiguration.setAllowCredentials(true);

        //Development
        corsConfiguration.addAllowedOrigin("http://127.0.0.2:5501");

        corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));

        corsConfiguration.addAllowedHeader("Origin");
        corsConfiguration.addAllowedHeader("Content-Type");
        corsConfiguration.addAllowedHeader("Accept");
        corsConfiguration.addAllowedHeader("Authorization");
        corsConfiguration.addAllowedHeader("X-Requested-With");
        corsConfiguration.addAllowedHeader("Access-Control-Request-Method");
        corsConfiguration.addAllowedHeader("Access-Control-Request-Headers");
        corsConfiguration.addAllowedHeader("Cookie");
        corsConfiguration.addAllowedHeader("Set-Cookie");

        corsConfiguration.setExposedHeaders(Arrays.asList("Set-Cookie", "Cookie", "Authorization", "Content-Disposition"));

        corsConfiguration.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true); //Enable cookies

        configuration.addAllowedOrigin("http://127.0.0.1:5501");

        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type","Authorization","Accept","Origin","X-Requested-With"));

        configuration.addExposedHeader("Set-Cookie");
        configuration.addExposedHeader("Cookie");
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
