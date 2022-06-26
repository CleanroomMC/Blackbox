package com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.pipeline;

import com.cleanroommc.blackbox.pipeline.rendering.quad.lighting.LightDataAccess;

public class LightPipelineProvider {

    private final SmoothLightPipeline smoothLigher;
    private final FlatLightPipeline flatLighter;

    public LightPipelineProvider(LightDataAccess lightDataAccess) {
        this.smoothLigher = new SmoothLightPipeline(lightDataAccess);
        this.flatLighter = new FlatLightPipeline(lightDataAccess);
    }

    public FlatLightPipeline getFlatLighter() {
        return flatLighter;
    }

    public SmoothLightPipeline getSmoothLigher() {
        return smoothLigher;
    }

}
