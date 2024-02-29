package edu.arep.components;
import edu.arep.runtime.*;

/**
 *
 * @author jaider.gonzalez
 */
@Component
public class ComponenteWeb {

    @GetMapping("/hello")
    public static String hello(String nombre){
        return "hello World! "+ nombre;
    }
}