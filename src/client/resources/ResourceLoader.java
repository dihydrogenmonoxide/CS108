/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.resources;
import java.net.URL;

/**
 *the ResourceLoader loads all resources which are placed inside the jar file 
 * for the other classes.
 */
public class ResourceLoader
{       
    /**this loads a given resource inside the jar
     * <p> Sample usage "res.load("/images/bang.png");"</p>
     *
     @param name the filename of the resource
     @return the URL of the resource
     */
    public URL load(String name)
    {
        return this.getClass().getResource(name);
    }
    
}
