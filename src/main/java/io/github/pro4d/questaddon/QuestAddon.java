package io.github.pro4d.questaddon;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.pro4d.questaddon.commands.PumpkinCommand;
import io.github.pro4d.questaddon.listener.QuestAddonListener;
import io.github.pro4d.questaddon.util.QAUtils;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestAddon extends JavaPlugin {


    private QAUtils qaUtils;
    private ProtocolManager protocolManager;
    public final static Material pumpkinType = Material.CARVED_PUMPKIN;
    @Override
    public void onEnable() {
        // Plugin startup logic
        protocolManager = ProtocolLibrary.getProtocolManager();
        qaUtils = new QAUtils(this);
        new PumpkinCommand(this);
        new QuestAddonListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(qaUtils != null) {
            qaUtils.getPumpkinMap().clear();
        }
    }

    public QAUtils getQaUtils() {
        return qaUtils;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

}
