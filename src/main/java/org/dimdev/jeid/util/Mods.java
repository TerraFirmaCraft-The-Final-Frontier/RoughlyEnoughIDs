package org.dimdev.jeid.util;

import net.minecraftforge.fml.common.Loader;

public enum Mods {
    ADVANCED_ROCKETRY("advancedrocketry"),
    ABYSSALCRAFT("abyssalcraft"),
    ATUM("atum"),
    BIOMES_O_PLENTY("biomesoplenty"),
    BIOME_STAFF("biomestaff"),
    BIOME_TWEAKER("biometweaker"),
    BOOKSHELF("bookshelf"),
    COMPACT_MACHINES("compactmachines3"),
    CREEPING_NETHER("creepingnether"),
    CHUNK_PREGENERATOR("chunkpregenerator"),
    CUBIC_CHUNKS("cubicchunks"),
    CYCLOPS_CORE("cyclopscore"),
    EXTRA_UTILS("extrautils2"),
    GAIA_DIMENSION("gaiadimension"),
    GEOGRAPHICRAFT("geographicraft"),
    HAMMERLIB("hammercore"),
    JOURNEYMAP("journeymap"),
    KATHAIRIS("kathairis"),
    MORE_PLANETS("moreplanets"),
    MYSTCRAFT("mystcraft"),
    NATURES_COMPASS("naturescompass"),
    REALISTIC_TERRAIN_GEN("rtg"),
    SCAPE_RUN_PARASITES("srparasites"),
    THAUMCRAFT("thaumcraft"),
    BETWEENLANDS("thebetweenlands"),
    TOFUCRAFT("tofucraft"),
    TROPICRAFT("tropicraft"),
    TWILIGHT_FOREST("twilightforest"),
    WARP_DRIVE("warpdrive"),
    WORLD_EDIT("worldedit"),
    WYRMS_OF_NYRUS("wyrmsofnyrus"),
    ;

    public final String modId;

    Mods(String modId) {
        this.modId = modId;
    }

    public boolean isLoaded() {
        return Loader.isModLoaded(modId);
    }
}
