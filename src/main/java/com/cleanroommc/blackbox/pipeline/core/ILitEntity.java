package com.cleanroommc.blackbox.pipeline.core;

import com.cleanroommc.blackbox.pipeline.handlers.DynamicLightingHandler.LitInstance;

import javax.annotation.Nullable;

public interface ILitEntity {

    LitInstance setLitInstance(LitInstance litInstance);

    @Nullable
    LitInstance getLitInstance();

}
