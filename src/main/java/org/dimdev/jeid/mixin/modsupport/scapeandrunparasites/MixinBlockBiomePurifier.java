package org.dimdev.jeid.mixin.modsupport.scapeandrunparasites;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.dhanantry.scapeandrunparasites.block.BlockBiomePurifier;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import org.tff.reid.api.BiomeApi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockBiomePurifier.class, remap = false)
public class MixinBlockBiomePurifier {
    /**
     * @author roguetictac, jchung01
     * @reason Support int biome id for resetting infected biome.
     */
    @Definition(id = "biomeId", local = @Local(type = int.class, argsOnly = true))
    @Expression("?[?] = (byte) biomeId")
    @Inject(method = "positionToBiome", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void reid$updatePurifiedBiome(World worldIn, BlockPos pos, int type, CallbackInfo ci) {
        BiomeApi.INSTANCE.updateBiome(worldIn.getChunk(pos), pos, type);
    }
}
