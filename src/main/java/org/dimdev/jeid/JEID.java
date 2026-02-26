package org.dimdev.jeid;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.dimdev.jeid.debug.RegistryDebug;
import org.dimdev.jeid.init.REIDBiomes;
import org.dimdev.jeid.network.MessageManager;
import org.dimdev.jeid.proxy.IProxy;
import org.tff.reid.Tags;

@Mod(modid = JEID.MODID,
     name = JEID.NAME,
     version = JEID.VERSION,
     dependencies = JEID.DEPENDENCIES)
public class JEID {
    public static final String MODID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final String DEPENDENCIES = "required:mixinbooter@[10.7,);"
        + "after:cubicchunks@[1.12.2-0.0.1271.0-SNAPSHOT,);"
        + "after:wyrmsofnyrus@[0.8,);";

    @SidedProxy(clientSide = "org.dimdev.jeid.proxy.ClientProxy", serverSide = "org.dimdev.jeid.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.checkIncompatibleMods();
        // Register messages
        MessageManager.init();
        // Register Error Biome
        MinecraftForge.EVENT_BUS.register(REIDBiomes.class);
        // Debug code
        MinecraftForge.EVENT_BUS.register(new RegistryDebug());
    }

    @Mod.EventHandler
    public void onRemap(FMLModIdMappingEvent event) {
        REIDBiomes.ERROR.updateMapping(event);
    }
}