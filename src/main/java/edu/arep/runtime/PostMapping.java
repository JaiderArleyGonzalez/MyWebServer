package edu.arep.runtime;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) //hasta tiempo de ejecución
@Target(ElementType.METHOD)
public @interface PostMapping {
    String value();
}
