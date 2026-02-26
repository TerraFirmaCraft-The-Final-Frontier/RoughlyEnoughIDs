package org.dimdev.jeid.ducks;

import org.tff.reid.api.BiomeApi;
import org.tff.reid.api.biome.BiomeAccessor;
import org.dimdev.jeid.impl.type.BiomeStorage;

/**
 * Duck interface for Chunk mixins.
 */
public interface INewChunk extends BiomeStorage {
    /**
     * @deprecated Use the {@link BiomeApi} to get a {@link BiomeAccessor} instead.
     */
    @Deprecated
    int[] getIntBiomeArray();

    /**
     * @deprecated Use {@link BiomeApi}'s update methods instead.
     */
    @Deprecated
    void setIntBiomeArray(int[] intBiomeArray);
}
