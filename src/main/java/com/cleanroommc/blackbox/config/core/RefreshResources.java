package com.cleanroommc.blackbox.config.core;

import net.minecraftforge.client.resource.VanillaResourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a config annotated with @RefreshResources and has been changed, it will refresh resources
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshResources {

    VanillaResourceType value(); // TODO

}
