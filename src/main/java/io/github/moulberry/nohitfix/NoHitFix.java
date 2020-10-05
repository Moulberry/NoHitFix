package io.github.moulberry.nohitfix;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.*;

@Mod(modid = NoHitFix.MODID, version = NoHitFix.VERSION, clientSideOnly = true)
public class NoHitFix {
    public static final String MODID = "nohitfix";
    public static final String VERSION = "1.0-REL";

    public static NoHitFix INSTANCE;

    public boolean noHitFix = true;
    private boolean clickTest = false;
    private List<Long> successfulClicks = new ArrayList<>();
    private List<Long> unsuccessfulClicks = new ArrayList<>();

    private static final String PREFIX = EnumChatFormatting.GOLD+"["+EnumChatFormatting.RED+"NoHitFix"+EnumChatFormatting.GOLD+"] ";

    public void print(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(PREFIX + message));
    }

    SimpleCommand nohitcommand = new SimpleCommand("nohitfix", new SimpleCommand.ProcessCommandRunnable() {
        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("toggle")) {
                    noHitFix = !noHitFix;
                    if(noHitFix) {
                        print(EnumChatFormatting.GREEN + "Fix: ENABLED");
                    } else {
                        print(EnumChatFormatting.RED + "Fix: DISABLED");
                    }
                } else if(args[0].equalsIgnoreCase("starttest")) {
                    clickTest = true;
                    successfulClicks.clear();
                    unsuccessfulClicks.clear();
                    print(EnumChatFormatting.GREEN + "Started test");
                } else if(args[0].equalsIgnoreCase("stoptest")) {
                    clickTest = false;

                    int successful = successfulClicks.size();
                    int unsuccessful = unsuccessfulClicks.size();

                    long start, end;
                    if(successful > 0 && unsuccessful > 0) {
                        start = Math.min(successfulClicks.get(0), unsuccessfulClicks.get(0));
                        end = Math.max(successfulClicks.get(successfulClicks.size()-1), unsuccessfulClicks.get(unsuccessfulClicks.size()-1));
                    } else if(successful > 0) {
                        start = successfulClicks.get(0);
                        end = successfulClicks.get(successfulClicks.size()-1);
                    } else if(unsuccessful > 0) {
                        start = unsuccessfulClicks.get(0);
                        end = unsuccessfulClicks.get(unsuccessfulClicks.size()-1);
                    } else {
                        print(EnumChatFormatting.RED + "No clicks were registered");
                        return;
                    }

                    float cps = Math.round((float)successful/(end - start)*100*1000)/100f;

                    print(EnumChatFormatting.GREEN + "Registered " + successful + " clicks w/ " + unsuccessful + " noregs");
                    print(EnumChatFormatting.GREEN + "Actual CPS: " + cps);
                }
            } else {
                print(EnumChatFormatting.GREEN + "Available subcommands: toggle, starttest, stoptest");
            }
        }
    });

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(nohitcommand);
    }

    public void registerClick(boolean successful) {
        if(clickTest) {
            (successful ? successfulClicks : unsuccessfulClicks).add(System.currentTimeMillis()); //Ternary cuz I'm cool
        }
    }

}
