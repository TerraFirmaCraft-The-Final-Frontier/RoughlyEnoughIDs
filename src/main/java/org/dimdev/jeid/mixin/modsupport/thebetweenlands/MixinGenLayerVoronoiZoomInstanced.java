package org.dimdev.jeid.mixin.modsupport.thebetweenlands;

import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import thebetweenlands.common.world.gen.layer.GenLayerVoronoiZoomInstanced;

@Mixin(GenLayerVoronoiZoomInstanced.class)
public abstract class MixinGenLayerVoronoiZoomInstanced {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
