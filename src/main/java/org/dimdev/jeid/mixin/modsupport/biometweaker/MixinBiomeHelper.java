package org.dimdev.jeid.mixin.modsupport.biometweaker;

import me.superckl.biometweaker.util.BiomeHelper;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = BiomeHelper.class, remap = false)
public class MixinBiomeHelper {
    @ModifyConstant(method = "getNextFreeBiomeId", constant = @Constant(intValue = 256))
    private static int reid$getLoopUpperLimit(int oldValue) {
        return BiomeApi.INSTANCE.getMaxBiomeId() + 1;
    }

    @ModifyConstant(method = "getNextFreeBiomeId", constant = @Constant(intValue = 255))
    private static int reid$getMaxBiomeId(int oldValue) {
        return BiomeApi.INSTANCE.getMaxBiomeId();
    }
}
