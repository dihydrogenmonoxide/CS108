/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client.resources;
import java.net.URL;

/**
 *
 * @author oliverwisler
 */
public class ResourceLoader
{   
    private void ResourceLoader()
    {
        return;
    }
    
    
    /**this loads a given resource inside the jar
     @param name the filename of the resource
     @return the URL of the resource
     */
    public URL load(String name)
    {
        return this.getClass().getResource(name);
    }
    
}
