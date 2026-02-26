package org.dimdev.jeid.mixin.modsupport.twilightforest;

import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import twilightforest.world.layer.GenLayerTFRiverMix;

@Mixin(GenLayerTFRiverMix.class)
public class MixinGenLayerTFRiverMix {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}