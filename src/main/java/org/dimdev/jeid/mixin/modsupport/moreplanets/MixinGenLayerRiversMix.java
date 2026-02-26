package org.dimdev.jeid.mixin.modsupport.moreplanets;

import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import stevekung.mods.moreplanets.planets.chalos.world.gen.biome.layer.GenLayerSlimelyStreamMix;
import stevekung.mods.moreplanets.planets.fronos.world.gen.biome.layer.GenLayerFronosRiverMix;
import stevekung.mods.moreplanets.planets.nibiru.world.gen.biome.layer.GenLayerNibiruRiverMix;

@Mixin(value = {GenLayerFronosRiverMix.class, GenLayerNibiruRiverMix.class, GenLayerSlimelyStreamMix.class})
public class MixinGenLayerRiversMix {
    @ModifyConstant(method = "getInts", constant = @Constant(intValue = 255))
    private int getBitMask(int oldValue) {
        return BiomeApi.INSTANCE.getIdentityMask();
    }
}
