package org.dimdev.jeid;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.dimdev.jeid.biome.BiomeError;
import org.dimdev.jeid.debug.RegistryDebug;
import org.dimdev.jeid.jeid.Tags;
import org.dimdev.jeid.network.MessageManager;
import org.dimdev.jeid.proxy.IProxy;

@Mod(modid = JEID.MODID,
     name = JEID.NAME,
     version = JEID.VERSION,
     dependencies = JEID.DEPENDENCIES)
public class JEID {
    public static final String MODID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required:mixinbooter@[8.0,);"
        + "after:abyssalcraft;"
        + "after:advancedrocketry;"
        + "after:atum;"
        + "after:biomesoplenty;"
        + "after:biometweaker;"
        + "after:bookshelf;"
        + "after:compactmachines3;"
        + "after:creepingnether;"
        + "after:cubicchunks@[1.12.2-0.0.1271.0-SNAPSHOT,);"
        + "after:cyclopscore;"
        + "after:extrautils2;"
        + "after:gaiadimension;"
        + "after:geographicraft;"
        + "after:hammercore;"
        + "after:journeymap;"
        + "after:moreplanets;"
        + "after:mystcraft;"
        + "after:srparasites@[1.10.2,);"
        + "after:thaumcraft;"
        + "after:thebetweenlands;"
        + "after:tofucraft;"
        + "after:tropicraft;"
        + "after:twilightforest;"
        + "after:wyrmsofnyrus;"
        + "after:worldedit";
    @SidedProxy(clientSide = "org.dimdev.jeid.proxy.ClientProxy", serverSide = "org.dimdev.jeid.proxy.ServerProxy")
    public static IProxy proxy;


    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.checkIncompatibleMods();
        // Register messages
        MessageManager.init();
        // Register Error Biome
        BiomeError.createInstance();
        ForgeRegistries.BIOMES.register(BiomeError.getInstance());
        // Debug code
        MinecraftForge.EVENT_BUS.register(new RegistryDebug());
    }
}