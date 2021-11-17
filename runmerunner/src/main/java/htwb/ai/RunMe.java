package htwb.ai;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RunMe {
    String issueId() default "No Description";
    String description() default "No Description";
    String author() default "No Description";
    String date() default "No Description";
}
