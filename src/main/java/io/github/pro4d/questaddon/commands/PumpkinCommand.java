package io.github.pro4d.questaddon.commands;

import io.github.pro4d.questaddon.QuestAddon;
import io.github.pro4d.questaddon.util.QAUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


@SuppressWarnings("ConstantConditions")
public class PumpkinCommand implements CommandExecutor {

    private final QAUtils qaUtils;
    public PumpkinCommand(QuestAddon plugin) {
        Bukkit.getPluginCommand("fakepumpkinhead").setExecutor(this);
        qaUtils = plugin.getQaUtils();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("fakepumpkinhead")) {
            if (args.length == 2) {
                try {
                    Integer.parseInt(args[1]);

                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number!");
                    return false;
                }

                if (Bukkit.getPlayer(args[0]) != null) {
                    qaUtils.pumpkinHead(Bukkit.getPlayer(args[0]), Integer.parseInt(args[1]));
                } else {
                    sender.sendMessage(qaUtils.notOnline(args[0]));
                }
                return true;
            }
            sender.sendMessage(qaUtils.invalidArgs());
        }
        return false;
    }

}
