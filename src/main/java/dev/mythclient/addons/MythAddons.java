package dev.mythclient.addons;

import dev.mythclient.MythClient;
import dev.mythclient.core.ClientModule;
import dev.mythclient.core.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import java.util.function.Supplier;

/** Registers every add-on module into the base client's ModuleManager (runs after the base mod's own init). */
public class MythAddons implements ClientModInitializer {
    private ModuleManager manager;

    @Override
    public void onInitializeClient() {
        manager = MythClient.modules();
        if (manager == null) {
            MythClient.LOGGER.error("Myth add-ons: base ModuleManager not ready, no add-on modules registered");
            return;
        }
        add(dev.mythclient.module.render.GammaModule::new);
        add(dev.mythclient.module.render.ZoomModule::new);
        add(dev.mythclient.module.hud.ArmorHudModule::new);
        add(dev.mythclient.module.hud.PotionHudModule::new);
        add(dev.mythclient.module.misc.NoSignEditModule::new);
        add(dev.mythclient.module.misc.SubtitlesModule::new);
        add(dev.mythclient.module.render.NoViewBobModule::new);
        add(dev.mythclient.module.render.NoScoreboardModule::new);
        add(dev.mythclient.module.render.NoFovEffectsModule::new);
        add(dev.mythclient.module.hud.PingModule::new);
        add(dev.mythclient.module.hud.ClockModule::new);
        add(dev.mythclient.module.hud.SpeedModule::new);
        add(dev.mythclient.module.hud.DirectionModule::new);
        add(dev.mythclient.module.hud.KeystrokesModule::new);
        add(dev.mythclient.module.hud.LastDeathModule::new);
        add(dev.mythclient.module.hud.DurabilityWarningModule::new);
        add(dev.mythclient.module.hud.ItemCounterModule::new);
        add(dev.mythclient.module.hud.BiomeModule::new);
        add(dev.mythclient.module.hud.LightLevelModule::new);
        add(dev.mythclient.module.hud.GameTimeModule::new);
        add(dev.mythclient.module.misc.AutoJumpModule::new);
        add(dev.mythclient.module.render.NoDarknessModule::new);
        add(dev.mythclient.module.render.NoDistortionModule::new);
        add(dev.mythclient.module.render.LowFireModule::new);
        add(dev.mythclient.module.render.LowShieldModule::new);
        add(dev.mythclient.module.render.ItemPositionModule::new);
        add(dev.mythclient.module.render.NoHurtCamModule::new);
        add(dev.mythclient.module.misc.AutoRespawnModule::new);
        add(dev.mythclient.module.render.NoWeatherModule::new);
        add(dev.mythclient.module.render.ReducedParticlesModule::new);
        add(dev.mythclient.module.misc.ToolSaverModule::new);
        add(dev.mythclient.module.hud.ConsumablesModule::new);
        add(dev.mythclient.module.misc.HideChatModule::new);
        add(dev.mythclient.module.misc.MuteModule::new);
        add(dev.mythclient.module.hud.TargetDistanceModule::new);
        add(dev.mythclient.module.hud.NetherCoordsModule::new);
        add(dev.mythclient.module.hud.MemoryModule::new);
        add(dev.mythclient.module.hud.InventoryFullModule::new);
        add(dev.mythclient.module.render.FreelookModule::new);
        add(dev.mythclient.module.render.FreecamModule::new);
        add(dev.mythclient.module.render.HealthHeartsModule::new);
        add(dev.mythclient.module.render.CosmeticsModule::new);
        // re-apply saved settings / keybinds now that the add-on modules exist
        try { MythClient.config().load(); }
        catch (Throwable t) { MythClient.LOGGER.warn("Myth add-ons: config reload failed", t); }
        MythClient.LOGGER.info("Myth add-ons registered ({} modules total)", manager.all().size());
    }

    private void add(Supplier<ClientModule> factory) {
        try { manager.register(factory.get()); }
        catch (Throwable t) { MythClient.LOGGER.error("Myth add-ons: a module failed to register", t); }
    }
}
