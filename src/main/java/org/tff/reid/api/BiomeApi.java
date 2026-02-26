package org.tff.reid.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import org.tff.reid.api.biome.BiomeAccessor;

/**
 * API class to provide utility methods to easily read & write extended biomes in RoughlyEnoughIDs format.
 */
public interface BiomeApi {
    /** The {@link BiomeApi} service. */
    BiomeApi INSTANCE = REIDApi.loadService(BiomeApi.class);

    /**
     * Returns a {@link BiomeAccessor} from the specified chunk, used to read biomes.
     *
     * @param chunk the chunk
     * @return a {@link BiomeAccessor} of the chunk
     */
    BiomeAccessor getBiomeAccessor(Chunk chunk);

    /**
     * Updates a biome at a single position within the specified chunk.
     *
     * @param chunk the chunk
     * @param pos the position of the biome to change
     * @param biomeId the id of the biome
     * @throws IllegalArgumentException if {@code pos} is not within the {@code chunk}
     */
    void updateBiome(Chunk chunk, BlockPos pos, int biomeId);

    /**
     * Updates all biomes in the specified chunk.
     *
     * @param chunk the chunk
     * @param biomeIds an array of biome ids to replace the chunk's array with.
     * @throws IllegalArgumentException if {@code biomeIds} does not match the length of the {@code chunk}'s array
     */
    void replaceBiomes(Chunk chunk, int[] biomeIds);

    /**
     * Updates all biomes in the specified chunk.
     *
     * @param chunk the chunk
     * @param biomes an array of biomes to replace the chunk's array with
     * @throws IllegalArgumentException if {@code biomes} does not match the length of the {@code chunk}'s array
     */
    void replaceBiomes(Chunk chunk, Biome[] biomes);

    /**
     * Fills the entire chunk with a single biome.
     *
     * @param chunk the chunk
     * @param biomeId the id of the biome to fill the chunk with.
     */
    void fillWithBiome(Chunk chunk, int biomeId);

    /**
     * Returns the value that serves as an identity mask for the maximum number of biomes RoughlyEnoughIDs supports.
     * Generally for use in {@code GenLayer} masking.
     *
     * @return the identity mask
     */
    int getIdentityMask();

    /**
     * Returns the maximum biome id supported by RoughlyEnoughIDs.
     *
     * @return the max biome id
     */
    int getMaxBiomeId();
}
