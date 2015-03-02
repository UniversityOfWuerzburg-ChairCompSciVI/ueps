package de.uniwue.info6.webapp.misc;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ResourceWrapper;

public class CDNResourceHandler extends ResourceHandlerWrapper {

  private ResourceHandler wrapped;

  public CDNResourceHandler(ResourceHandler wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public Resource createResource(final String resourceName,
                                 final String libraryName) {
    final Resource resource = super.createResource(resourceName,
                              libraryName);
    return resource;

    // if (resource == null || !"primefaces".equals(libraryName)
    //     || !"jquery/jquery.js".equals(resourceName)) {
    //   return resource;
    // }

    // return new ResourceWrapper() {
    //   @Override
    //   public String getRequestPath() {
    //     return "https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js";
    //   }

    //   @Override
    //   public Resource getWrapped() {
    //     return resource;
    //   }
    // };
  }

  @Override
  public ResourceHandler getWrapped() {
    return wrapped;
  }

}
