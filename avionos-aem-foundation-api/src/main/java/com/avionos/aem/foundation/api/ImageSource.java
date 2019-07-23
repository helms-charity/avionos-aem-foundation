package com.avionos.aem.foundation.api;

/**
 * Definition for items with associated images, such as pages and components.
 */
public interface ImageSource {

    /**
     * Check if the current resource has a default image.
     *
     * @return true if image has content
     */
    boolean isHasImage();

    /**
     * Check if the current resource has a named image.
     *
     * @param name image name (name of image as defined in dialog)
     * @return true if image has content
     */
    boolean isHasImage(String name);
}
