package com.avionos.aem.foundation.api.link;

/**
 * An image link contains all of the attributes of a <code>Link</code> with the addition of an image source attribute.
 */
public interface ImageLink extends Link {

    /**
     * @return image source or empty String if none
     */
    String getImageSource();
}
