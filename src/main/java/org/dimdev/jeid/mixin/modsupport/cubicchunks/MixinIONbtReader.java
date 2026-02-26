package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtReader;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import net.minecraftforge.common.util.Constants;
import org.tff.reid.api.BiomeApi;
import org.dimdev.jeid.impl.type.BiomeStorage;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.dimdev.jeid.mixin.core.world.MixinBlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IONbtReader.class, remap = false)
public class MixinIONbtReader {
    @Inject(method = "readBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getByteArray(Ljava/lang/String;)[B", ordinal = 0, remap = true))
    private static void reid$setTempPalette(NBTTagCompound nbt, World world, Cube cube, CallbackInfo ci, @Local ExtendedBlockStorage ebs) {
        int[] palette = nbt.hasKey("Palette", 11) ? nbt.getIntArray("Palette") : null;
        ((INewBlockStateContainer) ebs.getData()).setTemporaryPalette(palette);
    }

    /**
     * Delegate to JEID version of {@link BlockStateContainer#setDataFromNBT(byte[], NibbleArray, NibbleArray)}.
     *
     * @see MixinBlockStateContainer#reid$newSetDataFromNBT(byte[], NibbleArray, NibbleArray, CallbackInfo) delegated logic
     */
    @Expression("? < 4096")
    @ModifyExpressionValue(method = "readBlocks", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean reid$setBlockStateContainerData(boolean original, @Local(name = "ebs") ExtendedBlockStorage ebs, @Local(name = "abyte") byte[] abyte, @Local(name = "data") NibbleArray data, @Local(name = "add") NibbleArray add) {
        ebs.getData().setDataFromNBT(abyte, data, add);
        // Skip loop
        return false;
    }

    /**
     * @author Exsolutus, jchung01
     * @reason Support int biome ids
     */
    @Overwrite
    private static void readBiomes(NBTTagCompound nbt, Chunk column) {// column biomes
        if (nbt.hasKey("Biomes", Constants.NBT.TAG_INT_ARRAY)) {
            BiomeApi.INSTANCE.replaceBiomes(column, nbt.getIntArray("Biomes"));
        } else {
            // Convert old chunks
            int[] intBiomeArray = new int[256];
            int index = 0;
            for (byte b : nbt.getByteArray("Biomes")) {
                intBiomeArray[index++] = b & 0xFF;
            }
            BiomeApi.INSTANCE.replaceBiomes(column, intBiomeArray);
        }
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    private static void readBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        BiomeContainer container = ((BiomeStorage) cube).reid$getBiomes();
        // REID format, read directly
        if (nbt.hasKey("Biomes3D", Constants.NBT.TAG_INT_ARRAY)) {
            container.setBiomes(nbt.getIntArray("Biomes3D"));
        }
        // Vanilla CC conversion
        else if (nbt.hasKey("Biomes3D", Constants.NBT.TAG_BYTE_ARRAY)) {
            int[] intBiomeArray = new int[Coords.BIOMES_PER_CUBE];
            int index = 0;
            for (byte b : nbt.getByteArray("Biomes3D")) {
                intBiomeArray[index++] = b & 0xFF;
            }
            container.setBiomes(intBiomeArray);
        }
        // Legacy CC conversion
        if (nbt.hasKey("Biomes")) {
            container.setBiomes(reid$convertFromOldCubeBiomes(nbt.getIntArray("Biomes")));
        }
    }

    @Unique
    private static int[] reid$convertFromOldCubeBiomes(int[] biomes) {
        int[] newBiomes = new int[Coords.BIOMES_PER_CUBE];

        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 4; ++z) {
                    newBiomes[AddressTools.getBiomeAddress3d(x, y, z)] = biomes[getOldBiomeAddress(x << 1 | y & 1, z << 1 | y >> 1 & 1)];
                }
            }
        }

        return newBiomes;
    }

    @Shadow
    public static int getOldBiomeAddress(int biomeX, int biomeZ) {
        return 0;
    }
}
