package org.dimdev.jeid.mixin.modsupport.tofucraft;

import cn.mcmod.tofucraft.world.gen.layer.GenLayerRiverMix;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GenLayerRiverMix.class)
public class MixinGenLayerRiverMix {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
