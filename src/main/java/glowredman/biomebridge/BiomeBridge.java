package glowredman.biomebridge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(acceptedMinecraftVersions = "1.7.10", modid = BiomeBridge.MODID, name = "BiomeBridge", version = Tags.VERSION)
public class BiomeBridge {

    public static final String MODID = "biomebridge";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("BiomesOPlenty")) {
            throw new RuntimeException("Biomes O' Plenty is incompatible with BiomeBridge!");
        }
        ConfigHandler configHandler = new ConfigHandler(
            event.getModConfigurationDirectory()
                .toPath());
        configHandler.importBOPConfigs();
        configHandler.readConfig();
    }

}
