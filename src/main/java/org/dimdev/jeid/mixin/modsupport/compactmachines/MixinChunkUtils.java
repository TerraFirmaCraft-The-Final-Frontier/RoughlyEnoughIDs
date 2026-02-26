package org.dimdev.jeid.mixin.modsupport.compactmachines;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.common.util.Constants;
import org.dave.compactmachines3.utility.ChunkUtils;
import org.dimdev.jeid.JEID;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.impl.BiomeApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkUtils.class, remap = false)
public class MixinChunkUtils {
    @Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", ordinal = 0, remap = true))
    private static void reid$setNBTPalette(CallbackInfoReturnable<NBTTagCompound> cir, @Local(ordinal = 1) NBTTagCompound nbtTagCompound, @Local ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = ((INewBlockStateContainer) extendedBlockStorage.getData()).getTemporaryPalette();
        nbtTagCompound.setIntArray("Palette", palette);
    }

    @Redirect(method = "writeChunkToNBT",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0, remap = true),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setHasEntities(Z)V", remap = true)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", remap = true)
    )
    private static void reid$setNBTBiomeArray(NBTTagCompound instance, String key, byte[] value, Chunk chunkIn) {
        if (!key.equals("Biomes")) {
            throw new AssertionError(JEID.MODID + " :: NBTTagCompound#setByteArray key of writeChunkToNBT isn't \"Biomes\"");
        }

        instance.setIntArray(key, BiomeApiImpl.getInternalBiomeArray(chunkIn));
    }

    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByteArray(Ljava/lang/String;)[B", ordinal = 0, remap = true))
    private static void reid$setTempPalette(CallbackInfoReturnable<Chunk> cir, @Local(ordinal = 1) NBTTagCompound nbtTagCompound, @Local ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = nbtTagCompound.hasKey("Palette", 11) ? nbtTagCompound.getIntArray("Palette") : null;
        ((INewBlockStateContainer) extendedBlockStorage.getData()).setTemporaryPalette(palette);
    }

    @Definition(id = "compound", local = @Local(type = NBTTagCompound.class, argsOnly = true))
    @Definition(id = "hasKey", method = "Lnet/minecraft/nbt/NBTTagCompound;hasKey(Ljava/lang/String;I)Z")
    @Expression("compound.hasKey('Biomes', 7)")
    @Inject(method = "readChunkFromNBT", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static void reid$setBiomeArray(World worldIn, NBTTagCompound nbt, CallbackInfoReturnable<Chunk> cir) {
        if (nbt.hasKey("Biomes", Constants.NBT.TAG_INT_ARRAY)) {
            BiomeApi.INSTANCE.replaceBiomes(cir.getReturnValue(), nbt.getIntArray("Biomes"));
        } else {
            // Convert old chunks
            int[] intBiomeArray = new int[256];
            int index = 0;
            for (byte b : nbt.getByteArray("Biomes")) {
                intBiomeArray[index++] = b & 0xFF;
            }
            BiomeApi.INSTANCE.replaceBiomes(cir.getReturnValue(), intBiomeArray);
        }
    }
}
