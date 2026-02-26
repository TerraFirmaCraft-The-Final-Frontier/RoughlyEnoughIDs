package org.dimdev.jeid.mixin.core.world;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.dimdev.jeid.JEID;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.impl.BiomeApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilChunkLoader.class)
public class MixinAnvilChunkLoader {
    /**
     * @reason Read palette from NBT for JustEnoughIDs BlockStateContainers.
     */
    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByteArray(Ljava/lang/String;)[B", ordinal = 0))
    private void reid$readPaletteNBT(CallbackInfoReturnable<Chunk> cir, @Local(ordinal = 1) NBTTagCompound storageNBT, @Local ExtendedBlockStorage extendedBlockStorage) {
        int[] palette = storageNBT.hasKey("Palette", 11) ? storageNBT.getIntArray("Palette") : null;
        ((INewBlockStateContainer) extendedBlockStorage.getData()).setTemporaryPalette(palette);
        NibbleArray add2 = storageNBT.hasKey("Add2", 7) ? new NibbleArray(storageNBT.getByteArray("Add2")) : null;
        ((INewBlockStateContainer) extendedBlockStorage.getData()).setLegacyAdd2(add2);
    }

    /**
     * @reason Write palette to NBT for JustEnoughIDs BlockStateContainers.
     */
    @Inject(method = "writeChunkToNBT", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/chunk/BlockStateContainer;getDataForNBT([BLnet/minecraft/world/chunk/NibbleArray;)Lnet/minecraft/world/chunk/NibbleArray;", ordinal = 0))
    private void reid$writePaletteNBT(CallbackInfo ci, @Local ExtendedBlockStorage extendedBlockStorage, @Local(ordinal = 1) NBTTagCompound storageNBT) {
        int[] palette = ((INewBlockStateContainer) extendedBlockStorage.getData()).getTemporaryPalette();
        if (palette != null) storageNBT.setIntArray("Palette", palette);
    }

    /**
     * @reason Read int biome array from NBT if it's there.
     */
    @Inject(method = "readChunkFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;hasKey(Ljava/lang/String;I)Z", ordinal = 1))
    private void reid$readBiomeArray(World world, NBTTagCompound nbt, CallbackInfoReturnable<Chunk> cir, @Local Chunk chunk) {
        INewChunk newChunk = (INewChunk) chunk;
        if (nbt.hasKey("Biomes", 11)) {
            BiomeApi.INSTANCE.replaceBiomes(chunk, nbt.getIntArray("Biomes"));
        } else {
            // Convert old chunks
            int[] intBiomeArray = new int[256];
            int index = 0;
            for (byte b : nbt.getByteArray("Biomes")) {
                intBiomeArray[index++] = b & 0xFF;
            }
            BiomeApi.INSTANCE.replaceBiomes(chunk, intBiomeArray);
        }
    }

    /**
     * @reason Save the correct biome array type
     */
    @Redirect(method = "writeChunkToNBT",
            slice = @Slice(
                    id = "nbtBiomes",
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setHasEntities(Z)V")
            ), at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setByteArray(Ljava/lang/String;[B)V", ordinal = 0, slice = "nbtBiomes"))
    private void reid$writeBiomeArray(NBTTagCompound instance, String key, byte[] value, Chunk chunkIn) {
        if (!key.equals("Biomes")) {
            throw new AssertionError(JEID.MODID + " :: Sliced target setByteArray isn't \"Biomes\"");
        }
        instance.setIntArray(key, BiomeApiImpl.getInternalBiomeArray(chunkIn));
    }

    /**
     * @reason Disable default biome array save logic.
     */
    @Redirect(method = "writeChunkToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", ordinal = 0))
    private byte[] reid$defaultWriteBiomeArray(Chunk chunk) {
        return new byte[0];
    }
}
