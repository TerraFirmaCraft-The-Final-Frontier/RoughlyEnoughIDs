package org.dimdev.jeid.mixin.modsupport.tropicraft;

import net.tropicraft.core.common.worldgen.genlayer.GenLayerTropiVoronoiZoom;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GenLayerTropiVoronoiZoom.class)
public class MixinGenLayerTropiVoronoiZoom {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
