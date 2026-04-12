package org.dimdev.jeid.mixin.modsupport.warpdrive;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;

import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.world.SpaceWorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpaceWorldProvider.class)
public abstract class MixinSpaceWorldProvider extends WorldProvider {
    /*
     * The biome provider is supposed to be set in init(), but is incorrectly set in the constructor
     * to then be overwritten by the default provider due to super.init().
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void reid$fixBiomeProvider(CallbackInfo ci) {
        biomeProvider = new BiomeProviderSingle(WarpDrive.biomeSpace);
    }
}
