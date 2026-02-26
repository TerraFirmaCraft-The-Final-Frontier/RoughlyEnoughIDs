package org.dimdev.jeid.mixin.modsupport.biometweaker;

import me.superckl.biometweaker.util.BiomeColorMappings;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = BiomeColorMappings.class, remap = false)
public class MixinBiomeColorMappings {
    @ModifyConstant(method = "getColorForBiome", constant = @Constant(intValue = 0xFF))
    private static int reid$getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
