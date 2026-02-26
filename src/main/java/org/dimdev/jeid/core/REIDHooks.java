package org.dimdev.jeid.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import org.tff.reid.api.BiomeApi;
import org.tff.reid.api.compat.CompatibleChunkGenerator;

import javax.annotation.Nullable;

public class REIDHooks {
    private static final Biome[] reusableBiomeList = new Biome[256];

    /**
     * Initialize biome array after any calls to {@link net.minecraft.world.gen.IChunkGenerator#generateChunk}.
     * This guarantees the correct biomes even for modded chunk generators.
     */
    public static void initializeBiomeArray(Chunk chunk, @Nullable IChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof CompatibleChunkGenerator) {
            return;
        }
        Biome[] biomes = chunk.getWorld().getBiomeProvider().getBiomes(reusableBiomeList, chunk.x * 16, chunk.z * 16, 16, 16);
        BiomeApi.INSTANCE.replaceBiomes(chunk, biomes);
    }

    /**
     * Set the int biome id for a certain block position.
     * It is not this method's responsibility to make sure the change is synced to the client.
     * @param biomeId the biome id
     * @param world the world
     * @param pos the block position
     * @return the set biome id
     */
    public static int setBiomeId(int biomeId, World world, BlockPos pos) {
        BiomeApi.INSTANCE.updateBiome(world.getChunk(pos), pos, biomeId);
        return biomeId;
    }
}
