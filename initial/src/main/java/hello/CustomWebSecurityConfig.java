package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

public class CustomWebSecurityConfig {

    // generic part of security you want to reuse
    static class BaseWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            if(isRestrictedAccesConfigured(http)) {
                http
                        .formLogin().permitAll()
                        .and()
                        .logout().permitAll();
            }
        }

        private boolean isRestrictedAccesConfigured(HttpSecurity http) {
            ExpressionUrlAuthorizationConfigurer<?> configurer = http
                    .getConfigurer(ExpressionUrlAuthorizationConfigurer.class);
            return (configurer != null);
        }
    }

    public static class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void init(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .and().formLogin()
                    .and().logout();
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.formLogin().permitAll();
            http.logout().permitAll();
        }

        private boolean isRestrictedAccesConfigured(HttpSecurity http) {
            ExpressionUrlAuthorizationConfigurer<?> configurer = http
                    .getConfigurer(ExpressionUrlAuthorizationConfigurer.class);
            return (configurer != null);
        }

        public static MyCustomDsl customDsl() {
            return new MyCustomDsl();
        }
    }


    // application security that reuses a generic security part:
//    @Configuration
//    static class ApplicationWebSecurityConfigurerAdapter extends BaseWebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception{
//            http.authorizeRequests()
//                    .antMatchers("/", "/home").permitAll()
//                    .anyRequest().authenticated();
//            super.configure(http);
//        }
//    }

    //OR

    @Configuration
    static class ApplicationWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception{
            http.authorizeRequests()
                    .antMatchers("/", "/home").permitAll()
                    .anyRequest().authenticated();
                // disabled in favor of the META-INF/spring.factories
                // see org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.java:223
//            http.apply(MyCustomDsl.customDsl());
        }
    }
}
