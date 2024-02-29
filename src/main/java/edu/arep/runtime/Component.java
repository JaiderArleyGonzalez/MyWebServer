package edu.arep.runtime;
import java.lang.annotation.*;
/**
 *
 * @author jaider.gonzalez
 */
@Retention(RetentionPolicy.RUNTIME) //hasta tiempo de ejecución
@Target(ElementType.TYPE)
public @interface Component {
}
