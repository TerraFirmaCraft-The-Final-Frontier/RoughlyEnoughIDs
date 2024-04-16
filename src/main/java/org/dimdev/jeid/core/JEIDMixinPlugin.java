package org.dimdev.jeid.core;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Loader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class JEIDMixinPlugin implements IMixinConfigPlugin {

    private final List<String> matchingTarget = ImmutableList.of(
            "net.minecraft.world.chunk.Chunk",
            "net.minecraft.world.chunk.storage.AnvilChunkLoader",
            "net.minecraft.network.play.server.SPacketChunkData");
    private final List<String> matchingMixin = ImmutableList.of(
            "org.dimdev.jeid.mixin.core.world.MixinChunk",
            "me.jellysquid.mods.phosphor.mixins.lighting.common.MixinChunk",
            "org.dimdev.jeid.mixin.core.world.MixinAnvilChunkLoader",
            "me.jellysquid.mods.phosphor.mixins.lighting.common.MixinAnvilChunkLoader",
            "org.dimdev.jeid.mixin.core.network.MixinSPacketChunkData",
            "me.jellysquid.mods.phosphor.mixins.lighting.common.MixinSPacketChunkData",
            "me.jellysquid.mods.phosphor.mixins.lighting.common.MixinChunk$Sponge",
            "me.jellysquid.mods.phosphor.mixins.lighting.common.MixinChunk$Vanilla");

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
//        if (!Loader.isModLoaded("phosphor-lighting")){
//            return  "mixins.jeid.refmap.json";
//        }
        return  "mixins.jeid.refmap.json";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (Loader.isModLoaded("phosphor-lighting")) {
           if (matchingTarget.contains(targetClassName) && matchingMixin.contains(mixinClassName)) {
               return false;
           }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        if (Loader.isModLoaded("phosphor-lighting")){
            return ImmutableList.of(
                    "org.dimdev.jeid.mixin.modsupport.phosphor.MixinChunk",
                    "org.dimdev.jeid.mixin.modsupport.phosphor.MixinAnvilChunkLoader",
                    "org.dimdev.jeid.mixin.modsupport.phosphor.MixinSPacketChunkData",
                    "org.dimdev.jeid.mixin.modsupport.phosphor.MixinChunk$Vanilla",
                    "org.dimdev.jeid.mixin.modsupport.phosphor.MixinChunk$Sponge");
        }

        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
