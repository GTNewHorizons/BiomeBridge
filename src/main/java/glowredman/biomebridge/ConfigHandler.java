package glowredman.biomebridge;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

    // Colors
    public static boolean enableFogColors = true;
    public static boolean enableSkyColors = true;

    // Biomes
    public static Set<String> biomesAllowingStrongholds;
    public static Set<String> biomesAllowingVillages;
    public static Set<String> enabledBiomes;
    public static Set<String> enabledBiomeOverrides;
    public static final Map<String, Integer> BIOME_WEIGHTS = new HashMap<>();
    public static final Map<String, Integer> BIOME_IDS = new HashMap<>();

    // Mobs
    public static boolean spawnAll = true;
    public static boolean spawnGlob = true;
    public static boolean spawnJungleSpider = true;
    public static boolean spawnPhantom = true;
    public static boolean spawnPixie = true;
    public static boolean spawnRosester = true;

    // Generation
    public static boolean enableOreGen = true;
    public static boolean generateAmber = true;
    public static boolean generateAmethyst = true;
    public static boolean generateMalachite = true;
    public static boolean generatePeridot = true;
    public static boolean generateRuby = true;
    public static boolean generateSapphire = true;
    public static boolean generateTanzanite = true;
    public static boolean generateTopaz = true;
    public static boolean generateWaspHives = true;

    // Potions
    public static boolean autoAssignPotions = true;
    public static int paralysisID = 20;
    public static int possessionID = 21;

    // Spawn
    public static boolean onlySpawnOnBeaches = true;
    public static int spawnLocationSearchRadius = 1024;

    // World type
    public static int biomeSize = 4;
    public static int landmassPercentage = 10;
    public static boolean oceanFiller = true;

    // Misc
    public static boolean enableDebug = false;
    public static boolean addCustomDungeonLoot = true;
    public static boolean enableSpecialEvents = true;
    public static boolean enableMainMenuPanorama = true;
    public static boolean enablePoisonIvyEffects = true;
    public static boolean enableSpringWaterRegenEffect = true;

    private final Path CONFIG_DIR;

    ConfigHandler(Path configDir) {
        this.CONFIG_DIR = configDir;
    }

    void importBOPConfigs() {
        Path bopDir = this.CONFIG_DIR.resolve("biomesoplenty");
        if (!Files.exists(bopDir)) {
            return;
        }

        Path structuresDir = bopDir.resolve("structures");
        if (Files.exists(structuresDir)) {
            Path strongholdsFile = structuresDir.resolve("strongholds.cfg");
            if (Files.exists(strongholdsFile)) {
                Configuration structuresCfg = new Configuration(strongholdsFile.toFile());
                biomesAllowingStrongholds = parseBooleanProps(structuresCfg.getCategory("allow strongholds"));
                this.backup(structuresDir);
            }

            Path villagesFile = structuresDir.resolve("villages.cfg");
            if (Files.exists(villagesFile)) {
                Configuration villagesCfg = new Configuration(villagesFile.toFile());
                biomesAllowingVillages = parseBooleanProps(villagesCfg.getCategory("allow villages"));
                this.backup(villagesFile);
            }
        }

        Path biomegenFile = bopDir.resolve("biomegen.cfg");
        if (Files.exists(biomegenFile)) {
            Configuration biomegenCfg = new Configuration(biomegenFile.toFile());
            String[] categoryNames = new String[] { "end biomes to generate", "nether biomes to generate",
                "overworld (river) biomes to generate", "overworld (sub) biomes to generate",
                "overworld biomes to generate" };
            ConfigCategory[] categories = new ConfigCategory[categoryNames.length];
            for (int i = 0; i < categoryNames.length; i++) {
                categories[i] = biomegenCfg.getCategory(categoryNames[i]);
            }
            enabledBiomes = parseBooleanProps(categories);
            enabledBiomeOverrides = parseBooleanProps(biomegenCfg.getCategory("vanilla biomes to override"));
            this.backup(biomegenFile);
        }

        Path biomeweightsFile = bopDir.resolve("biomeweights.cfg");
        if (Files.exists(biomeweightsFile)) {
            Configuration biomeweightsCfg = new Configuration(biomeweightsFile.toFile());
            Arrays.asList("end biome weights", "nether biome weights", "overworld biome weights")
                .stream()
                .flatMap(
                    category -> biomeweightsCfg.getCategory(category)
                        .entrySet()
                        .stream())
                .forEach(e -> {
                    try {
                        BIOME_WEIGHTS.put(
                            e.getKey(),
                            e.getValue()
                                .getInt());
                    } catch (NumberFormatException ignored) {}
                });
            this.backup(biomeweightsFile);
        }

        Path idsFile = bopDir.resolve("ids.cfg");
        if (Files.exists(idsFile)) {
            Configuration idsCfg = new Configuration(idsFile.toFile());
            idsCfg.getCategory("biome ids")
                .entrySet()
                .forEach(e -> {
                    try {
                        BIOME_IDS.put(
                            e.getKey(),
                            e.getValue()
                                .getInt());
                    } catch (NumberFormatException ignored) {}
                });
            autoAssignPotions = idsCfg.get("potion auto assign", "Potion Auto Assign", autoAssignPotions)
                .getBoolean();
            paralysisID = idsCfg.get("potions ids", "Paralysis Potion ID", paralysisID)
                .getInt();
            possessionID = idsCfg.get("potions ids", "Possession Potion ID", possessionID)
                .getInt();
            this.backup(idsFile);
        }

        Path mainFile = bopDir.resolve("main.cfg");
        if (Files.exists(mainFile)) {
            Configuration mainCfg = new Configuration(mainFile.toFile());
            enableDebug = mainCfg.get("debug settings", "Debug Mode", enableDebug)
                .getBoolean();
            spawnAll = mainCfg.get("mob settings", "Spawn all mobs", spawnAll)
                .getBoolean();
            spawnGlob = mainCfg.get("mob settings", "Spawn Glob", spawnGlob)
                .getBoolean();
            spawnJungleSpider = mainCfg.get("mob settings", "Spawn Jungle Spider", spawnJungleSpider)
                .getBoolean();
            spawnPhantom = mainCfg.get("mob settings", "Spawn Phantom", spawnPhantom)
                .getBoolean();
            spawnPixie = mainCfg.get("mob settings", "Spawn Pixie", spawnPixie)
                .getBoolean();
            spawnRosester = mainCfg.get("mob settings", "Spawn Rosester", spawnRosester)
                .getBoolean();
            this.backup(mainFile);
        }

        Path miscFile = bopDir.resolve("misc.cfg");
        if (Files.exists(miscFile)) {
            Configuration miscCfg = new Configuration(miscFile.toFile());
            enableFogColors = miscCfg.get("hard-coded colors", "Enable Fog Colors", enableFogColors)
                .getBoolean();
            enableSkyColors = miscCfg.get("hard-coded colors", "Enable Sky Colors", enableSkyColors)
                .getBoolean();
            addCustomDungeonLoot = miscCfg
                .get("miscellanious settings", "Add Custom Dungeon Loot", addCustomDungeonLoot)
                .getBoolean();
            enableSpecialEvents = !miscCfg
                .get("miscellanious settings", "Behave Normally During Special Events", enableSpecialEvents)
                .getBoolean();
            enableMainMenuPanorama = miscCfg
                .get("miscellanious settings", "Enable Biomes O' Plenty Main Menu Panorama", enableMainMenuPanorama)
                .getBoolean();
            enablePoisonIvyEffects = miscCfg
                .get("miscellanious settings", "Enable Poison Ivy Effects", enablePoisonIvyEffects)
                .getBoolean();
            enableSpringWaterRegenEffect = miscCfg
                .get("miscellanious settings", "Enable Spring Water Regeneration Effect", enableSpringWaterRegenEffect)
                .getBoolean();
            onlySpawnOnBeaches = miscCfg.get("spawn settings", "Only Spawn On Beaches", onlySpawnOnBeaches)
                .getBoolean();
            spawnLocationSearchRadius = miscCfg
                .get("spawn settings", "Spawn Location Search Radius", spawnLocationSearchRadius)
                .getInt();
            this.backup(miscFile);
        }

        Path terraingenFile = bopDir.resolve("terraingen.cfg");
        if (Files.exists(terraingenFile)) {
            Configuration terraingenCfg = new Configuration(terraingenFile.toFile());
            biomeSize = terraingenCfg.get("biomes o' plenty world type settings", "Biome Size", biomeSize)
                .getInt();
            landmassPercentage = terraingenCfg
                .get("biomes o' plenty world type settings", "Landmass Percentage", landmassPercentage)
                .getInt();
            oceanFiller = terraingenCfg.get("biomes o' plenty world type settings", "OceanFiller", oceanFiller)
                .getBoolean();
            enableOreGen = terraingenCfg.get("biomes o' plenty world type settings", "OreGeneration", enableOreGen)
                .getBoolean();
            generateAmber = terraingenCfg.get("biomes o' plenty world type settings", "genAmberOre", generateAmber)
                .getBoolean();
            generateAmethyst = terraingenCfg
                .get("biomes o' plenty world type settings", "genAmethystOre", generateAmethyst)
                .getBoolean();
            generateMalachite = terraingenCfg
                .get("biomes o' plenty world type settings", "genMalachiteOre", generateMalachite)
                .getBoolean();
            generatePeridot = terraingenCfg
                .get("biomes o' plenty world type settings", "genPeridotOre", generatePeridot)
                .getBoolean();
            generateRuby = terraingenCfg.get("biomes o' plenty world type settings", "genRubyOre", generateRuby)
                .getBoolean();
            generateSapphire = terraingenCfg
                .get("biomes o' plenty world type settings", "genSapphireOre", generateSapphire)
                .getBoolean();
            generateTanzanite = terraingenCfg
                .get("biomes o' plenty world type settings", "genTanzaniteOre", generateTanzanite)
                .getBoolean();
            generateTopaz = terraingenCfg.get("biomes o' plenty world type settings", "genTopazOre", generateTopaz)
                .getBoolean();
            generateWaspHives = terraingenCfg
                .get("biomes o' plenty world type settings", "genWaspHives", generateWaspHives)
                .getBoolean();
            this.backup(terraingenFile);
        }
    }

    void readConfig() {
        // TODO
    }

    private void backup(Path path) {
        // spotless:off
        // disabled during testing
//        Path backupPath = path.getParent().resolve(path.getFileName() + ".bak");
//        try {
//            Files.move(path, backupPath);
//        } catch (Exception e) {
//            BiomeBridge.LOGGER.warn("Failed to rename " + this.CONFIG_DIR.relativize(path) + " to " + this.CONFIG_DIR.relativize(backupPath), e);
//        }
        // spotless:on
    }

    private static Set<String> parseBooleanProps(ConfigCategory... categories) {
        return Arrays.stream(categories)
            .flatMap(
                category -> category.entrySet()
                    .stream())
            .filter(
                e -> e.getValue()
                    .getBoolean())
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

}
