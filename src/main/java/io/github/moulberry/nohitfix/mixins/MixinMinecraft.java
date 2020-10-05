package io.github.moulberry.nohitfix.mixins;

import io.github.moulberry.nohitfix.NoHitFix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    public int leftClickCounter;

    @Inject(method = "clickMouse", at=@At("HEAD"))
    public void clickMouse(CallbackInfo info) {
        //System.out.println("Counter: "+ leftClickCounter);
        NoHitFix.INSTANCE.registerClick(this.leftClickCounter <= 0);
    }

    private static final String TARGET = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V";
    @Redirect(method="runTick", at=@At(value="INVOKE", target=TARGET))
    public void runTick_setKeyBindState(int keybind, boolean state) {
        if(NoHitFix.INSTANCE.noHitFix && keybind == -100 && !state) { //left click
            //System.out.println("resetting left click counter");
            leftClickCounter = 0;
        }

        KeyBinding.setKeyBindState(keybind, state);
    }


}
