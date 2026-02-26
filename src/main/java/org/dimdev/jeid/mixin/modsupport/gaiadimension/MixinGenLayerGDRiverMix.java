package org.dimdev.jeid.mixin.modsupport.gaiadimension;

import androsa.gaiadimension.world.layer.GenLayerGDRiverMix;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GenLayerGDRiverMix.class)
public class MixinGenLayerGDRiverMix {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
