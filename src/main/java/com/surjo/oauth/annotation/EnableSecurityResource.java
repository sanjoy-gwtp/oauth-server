package com.surjo.oauth.annotation;



import com.surjo.oauth.config.SecurityServiceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SecurityServiceConfiguration.class})
public @interface EnableSecurityResource {
}
