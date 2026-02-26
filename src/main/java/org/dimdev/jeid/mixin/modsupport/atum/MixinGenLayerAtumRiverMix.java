package org.dimdev.jeid.mixin.modsupport.atum;

import com.teammetallurgy.atum.world.gen.layer.GenLayerAtumRiverMix;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = GenLayerAtumRiverMix.class)
public class MixinGenLayerAtumRiverMix
{
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
