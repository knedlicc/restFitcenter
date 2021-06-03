//package cz.cvut.kbss.ear.config;
//
//import cz.cvut.kbss.ear.security.JWTAuthenticationFilter;
//import cz.cvut.kbss.ear.security.JWTAuthorizationFilter;
//import cz.cvut.kbss.ear.security.SecurityConstants;
//import cz.cvut.kbss.ear.service.security.UserDetailsServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
//import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.reactive.function.client.WebClient;
//
//
//import static cz.cvut.kbss.ear.security.SecurityConstants.SIGN_UP_URL;
//
////@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    private final UserDetailsServiceImpl userDetailsService;
//    private static final String[] COOKIES_TO_DESTROY = {
//            SecurityConstants.SESSION_COOKIE_NAME,
//            SecurityConstants.REMEMBER_ME_COOKIE_NAME
//    };
//
//    @Autowired
//    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//    //    private final AuthenticationFailureHandler authenticationFailureHandler;
////
////    private final AuthenticationSuccessHandler authenticationSuccessHandler;
////
////    private final LogoutSuccessHandler logoutSuccessHandler;
////
////    private final AuthenticationProvider authenticationProvider;
//
////    @Autowired
////    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
//////            AuthenticationFailureHandler authenticationFailureHandler,
//////                          AuthenticationSuccessHandler authenticationSuccessHandler,
//////                          LogoutSuccessHandler logoutSuccessHandler,
////                          AuthenticationProvider authenticationProvider) {
//////        this.authenticationFailureHandler = authenticationFailureHandler;
//////        this.authenticationSuccessHandler = authenticationSuccessHandler;
//////        this.logoutSuccessHandler = logoutSuccessHandler;
////        this.authenticationProvider = authenticationProvider;
////        this.userDetailsService = userDetailsService;
////    }
////
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) {
////        auth.authenticationProvider(authenticationProvider);
////    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.antMatcher("/**")
//                .authorizeRequests()
//                .antMatchers("/")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .oauth2Login();
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
//        return source;
//    }
//    @Bean
//    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
//                        OAuth2AuthorizedClientRepository authorizedClientRepository) {
//        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
//                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
//                        authorizedClientRepository);
//        oauth2.setDefaultOAuth2AuthorizedClient(true);
//        return WebClient.builder().apply(oauth2.oauth2Configuration()).build();
//    }
//}
