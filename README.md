# Roughly Enough IDs
[![Modrinth: Roughly Enough IDs](https://img.shields.io/modrinth/v/reid?logo=modrinth&label=RoughlyEnoughIDs)](https://modrinth.com/mod/reid) [![CurseForge: Roughly Enough IDs](https://img.shields.io/curseforge/v/629017?logo=curseforge&label=RoughlyEnoughIDs)](https://curseforge.com/minecraft/mc-mods/reid)

<img src="src/main/resources/assets/reid/logo.png" width="128" height="128" alt="RoughlyEnoughIDs logo"/>

## Building
1. Clone this repository via
  - SSH `git clone git@github.com:TerraFirmaCraft-The-Final-Frontier/RoughlyEnoughIDs.git` or
  - HTTPS `git clone https://github.com/TerraFirmaCraft-The-Final-Frontier/RoughlyEnoughIDs.git`
2. Build using the `gradlew build` command. Jar will be in build/libs

**Gradle for this project requires Java 25 or higher!**  
Running on:
- Gradle 9.2.1
- RetroFuturaGradle 2.0.2
- Forge 14.23.5.2847

## Maven
Besides the automatic artifacts available from CurseForge and Modrinth, REID is also available on GitHub Packages.  

Using Github Packages [requires special setup](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages)
to authenticate with your personal access token (this is a limitation of GitHub itself).  
Here is a more explicit example:
```groovy
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name 'RoughlyEnoughIDs GPR'
                url 'https://maven.pkg.github.com/TerraFirmaCraft-The-Final-Frontier/RoughlyEnoughIDs'
                credentials {
                    username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
                }
            }
        }
        filter {
            includeGroup 'tff'
        }
    }
}
```

When compiling against REID's API, you can declare the Gradle dependency by adding:
```groovy
dependencies {
    compileOnly 'tff:reid:VERSION:api' // Compile against the API
    runtimeOnly 'tff:reid:VERSION'     // Run with REID in dev
}
```
or add the `compileOnly` to your existing `dependencies` block in your `build.gradle`. The `runtimeOnly` line is only
necessary if you are testing your mod in dev with REID.

## API
Since v2.3.0, this mod provides an API to allow mod authors to add REID compatibility. Generally, most mods will already
be supported out of the box - this API is for certain elements of mods that require explicit support, either from REID's
side through mixins, or from the mod's side. The API currently provides:
- [`BiomeApi`](/src/main/java/org/dimdev/jeid/api/BiomeApi.java): Reading/writing biome ids in REID format. Any mod with
mechanics that manually change biomes in a chunk should use this.
- Various classes for compatibility: see [`api/compat`](/src/main/java/org/dimdev/jeid/api/compat) package.

See the javadocs for each api service for more details.

### Usage example
Before the API existed, some mods added compatibility for JEID/REID's extended biomes like so:
```java
public static void setBiome(World world, BlockPos pos, int id) {
    Chunk chunk = world.getChunk(pos);
    int i = ((pos.getZ() & 15) << 4) | (pos.getX() & 15);
    // JEID/REID biome update
    if (jeidLoaded && chunk instanceof INewChunk) {
        ((INewChunk) chunk).getIntBiomeArray()[i] = id;
    }
    // Vanilla biome update
    else {
        chunk.getBiomeArray()[i] = (byte) id;
    }
}
```
(See an example [here](https://github.com/Um-Mitternacht/Bewitchment/blob/1.12.2/src/main/java/com/bewitchment/common/world/BiomeChangingUtils.java#L90-L102) by Bewitchment).
The `BiomeApi` seeks to simplify this usage; now all you would need to update the biome in REID format is:
```java
public static void setBiome(World world, BlockPos pos, int id) {
    Chunk chunk = world.getChunk(pos);
    // REID biome update
    if (reidApiLoaded) {
        BiomeApi.INSTANCE.updateBiome(chunk, pos, id);
    }
    // Vanilla biome update
    else {
        // The index is only needed for vanilla now!
        int i = ((pos.getZ() & 15) << 4) | (pos.getX() & 15);
        chunk.getBiomeArray()[i] = (byte) id;
    }
}
```
This could also allow future versions of REID to change its biome format without breaking your compatibility code.