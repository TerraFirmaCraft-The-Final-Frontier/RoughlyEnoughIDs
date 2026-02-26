package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockParasiteSpreading.class, remap = false)
public class MixinBlockParasiteSpreading {
    /**
     * Capture biome id (int) before cast to byte.
     */
    @ModifyExpressionValue(method = "positionToParasiteBiome", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getIdForBiome(Lnet/minecraft/world/biome/Biome;)I", remap = true))
    private static int reid$captureIntBiomeId(int biomeId, @Share("intBiomeId") LocalIntRef intBiomeId) {
        intBiomeId.set(biomeId);
        return biomeId;
    }

    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for spreading infected biome.
     */
    @Definition(id = "biomeId", local = @Local(type = byte.class, name = "biomeID"))
    @Expression("?[?] = biomeId")
    @Inject(method = "positionToParasiteBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void reid$updateParasiteBiome(World worldIn, BlockPos pos, int type, CallbackInfo ci, @Share("intBiomeId") LocalIntRef intBiomeId) {
        BiomeApi.INSTANCE.updateBiome(worldIn.getChunk(pos), pos, intBiomeId.get());
    }
}
