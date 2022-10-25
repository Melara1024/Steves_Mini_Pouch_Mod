package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import ga.melara.stevesminipouch.data.*;
import ga.melara.stevesminipouch.util.IAdditionalStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.antlr.v4.gui.SystemFontMetrics;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@Mixin(Player.class)
public abstract class PlayerMixin
{

    @Shadow
    @Final
    private
    Inventory inventory;


    @Shadow public abstract Component getDisplayName();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void oninit(Level p_219727_, BlockPos p_219728_, float p_219729_, GameProfile p_219730_, ProfilePublicKey p_219731_, CallbackInfo ci) {

        //プレイヤーがnullなのかメッセージがnullなのか

        //PlayerInventorySizeDataのところまでは問題ないはず




        //Todo プレイヤーの初期化の前に呼ばれてしまっている？


        //メッセージを送るのもデータ取得も両方失敗している
        //Messager.sendToPlayer(new InventorySyncPacket(p), ((ServerPlayer)(Object)(this)));
    }




        @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onReadData(CompoundTag p_36215_, CallbackInfo ci)
    {
        //System.out.println("minipouch read");

        LazyOptional<PlayerInventorySizeData> l = ((Player)(Object)this).getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());

        System.out.println("onReadData");
        System.out.println(p.getSlot());
        System.out.println(p.isActiveInventory());
        System.out.println(p.isEquippable());
        System.out.println(p.isActiveOffhand());
        System.out.println(p.isCraftable());

        //Messager.sendToPlayer(new InventorySyncPacket(p), ((ServerPlayer)(Object)(this)));

        //無理やりServerPlayerを手に入れているところが原因では？
        //ServerPlayer側にMixinして状態を送るほうが良い？

        //ここに入ってくるのはServerPlayerインスタンスのみ
        //ということは多分そもそも送信する情報が間違っている
        //Messager.sendToPlayer(new InventorySyncPacket(p), (ServerPlayer)(Object)(this));
        //読み込み時にプレイヤーのインベントリステータスもクライアントと同期する？
//            LazyOptional<PlayerInventorySizeData> l = ((Player)(Object)this).getCapability(PlayerInventoryProvider.DATA);
//            PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());
//            Messager.sendToPlayer(new InventorySyncPacket(p), ((ServerPlayer)(Object)(this)));
//

        ListTag listtag = p_36215_.getList("MiniPouch", 10);
        ((IAdditionalStorage) this.inventory).loadAdditional(listtag);
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"), cancellable = true)
    public void onAddData(CompoundTag p_36265_, CallbackInfo ci)
    {
        //System.out.println("minipouch add");
        p_36265_.put("MiniPouch", ((IAdditionalStorage) this.inventory).saveAdditional(new ListTag()));
    }

}
