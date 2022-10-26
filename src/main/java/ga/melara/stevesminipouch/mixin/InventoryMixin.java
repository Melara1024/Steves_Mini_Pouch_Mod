package ga.melara.stevesminipouch.mixin;

import com.google.common.collect.ImmutableList;
import ga.melara.stevesminipouch.Config;
import ga.melara.stevesminipouch.data.PlayerInventoryProvider;
import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import ga.melara.stevesminipouch.util.IAdditionalStorage;
import ga.melara.stevesminipouch.util.IStorageChangable;
import ga.melara.stevesminipouch.util.LockableItemStackList;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.commons.lang3.Validate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


//こいつはサーバー側
@Mixin(Inventory.class)
public abstract class InventoryMixin implements IStorageChangable, IAdditionalStorage {

    /*
    Todo コマンド・インベントリ拡張アイテムの放つイベントに合わせてitemsやsizeを増減する


    拾ってもちゃんとスロットに反映されない問題->Inventory.addのどこかがおかしい？
     */

    private int maxPage;

    private int inventorySize;
    private int hotbarSize;


    private boolean isActiveInventory = Config.DEFAULT_INVENTORY.get();
    private boolean isActiveArmor = Config.DEFAULT_ARMOR.get();
    private boolean isActiveOffhand = Config.DEFAULT_OFFHAND.get();

    private boolean isActiveCraft = Config.DEFAULT_CRAFT.get();

    @Shadow
    public NonNullList<ItemStack> items;

    @Shadow
    public NonNullList<ItemStack> armor;

    @Shadow
    public NonNullList<ItemStack> offhand;

    @Shadow
    public int selected;

    //こいつの参照だけは絶対に変更するな！！
    @Shadow
    public List<NonNullList<ItemStack>> compartments = new ArrayList<NonNullList<ItemStack>>();

    @Shadow
    private boolean hasRemainingSpaceForItem(ItemStack p_36015_, ItemStack p_36016_) {
        return false;
    }

    @Shadow
    public ItemStack getItem(int id) {
        return null;
    }


    @Shadow
    public abstract void tick();

    @Shadow
    @Final
    @Mutable
    public Player player;

    public void initMiniPouch(PlayerInventorySizeData data)
    {
        //これをクライアントとサーバー両方からなんとか呼び出す

    }


    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void oninit(Player p_35983_, CallbackInfo ci) {

        //System.out.println(p_35983_);

        System.out.println(p_35983_.getLevel().isClientSide ? "client inventory!" : "server inventory!");



        maxPage = 5;
        //もとの数より減らしてはいけない……
        inventorySize = 92;
        hotbarSize = 9;

        items = LockableItemStackList.withSize(inventorySize, (Inventory) (Object) this, false);
        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, true);
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, true);
        compartments.add(0, items);
        compartments.add(1, armor);
        compartments.add(2, offhand);

        //Todo プレイヤーに紐付けられたスロット数を初期化で適用する

        isActiveOffhand = false;
        isActiveArmor = false;
    }


    @Inject(method = "getSlotWithRemainingSpace(Lnet/minecraft/world/item/ItemStack;)I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetRemainingSpace(ItemStack p_36051_, CallbackInfoReturnable<Integer> cir) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), p_36051_)) {
            cir.setReturnValue(this.selected);
        } else if (this.hasRemainingSpaceForItem(this.getItem(40), p_36051_)) {
            cir.setReturnValue(40);
        } else {
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.hasRemainingSpaceForItem(this.items.get(i), p_36051_)) {
                    if (i < 36) cir.setReturnValue(i);
                    else cir.setReturnValue(i + 5);
                }
            }
        }
    }

    @Inject(method = "getFreeSlot()I", at = @At(value = "HEAD"), cancellable = true)
    public void onGetFreeSlot(CallbackInfoReturnable<Integer> cir) {
        //35番にアイテムが入る問題はここのせい，空きスロットなのでEMPTYを返してしまう
        for (int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty() && !((LockableItemStackList)items).lockList.get(i)) {
                if (i < 36) cir.setReturnValue(i);
                else cir.setReturnValue(i + 5);
            }
        }
        if(Objects.isNull(cir.getReturnValue()))cir.setReturnValue(-1);
    }




    @Override
    public void toggleInventory(Player player) {
        //全部の無効化
        //他の機能をまとめて起動するだけなので実装は後で


        //何故かプレイヤーの参照を正しく得られない？
        //プレイヤー自体は得られている？
        //Todo クライアント側のみ初期化が遅れている，NBTの読み込みからクライアントへの動機までが初期設定部分で行えていない
        //Todo データそのものはサーバーに保存されるので最初に初期化パケットを送るのが必要，その後は多分同期しなくてOK

        System.out.println(player.getDisplayName());

        LazyOptional<PlayerInventorySizeData> l = player.getCapability(PlayerInventoryProvider.DATA);
        PlayerInventorySizeData p = l.orElse(new PlayerInventorySizeData());

        System.out.println("inventory init!  from " + (player.getLevel().isClientSide? "client" : "server"));
        System.out.println(p.getSlot());
        System.out.println(p.isActiveInventory());
        System.out.println(p.isEquippable());
        System.out.println(p.isActiveOffhand());
        System.out.println(p.isCraftable());

        changeStorageSize(1, player);
        isActiveArmor = true;
        toggleArmor(player);
        isActiveOffhand = true;
        toggleOffhand(player);

        //クラフトはここから操作する必要なし

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Inventory Toggled!"));
    }


    @Override
    public void toggleArmor(Player player) {

        System.out.println(player.getLevel().isClientSide ? "client inventory!" : "server inventory!");
        //アーマーリストの無効化
        //溢れたアイテムを撒き散らす
        //menu.slotsを回してSlotType.ARMORを無効化・隠蔽処理有効化

        if (this.isActiveArmor) {
            for (ItemStack item : armor) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }

            compartments.remove(armor);
            armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, true);
            compartments.add(1, armor);

            this.isActiveArmor = false;
            return;
        }

        compartments.remove(armor);
        armor = LockableItemStackList.withSize(4, (Inventory) (Object) this, false);
        compartments.add(1, armor);

        this.isActiveArmor = true;

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Armor Toggled!"));
    }

    @Override
    public void toggleOffhand(Player player) {
        if (this.isActiveOffhand) {
            for (ItemStack item : offhand) {
                Level level = player.level;
                ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(player.getUUID());
                level.addFreshEntity(itementity);
            }

            compartments.remove(offhand);
            offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, true);
            compartments.add(2, offhand);

            this.isActiveOffhand = false;
            return;
        }

        compartments.remove(offhand);
        offhand = LockableItemStackList.withSize(1, (Inventory) (Object) this, false);
        compartments.add(2, offhand);

        this.isActiveOffhand = true;

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Offhand Toggled!"));
    }

    @Override
    public void toggleCraft(Player player)
    {
        //Todo アイテムリストに対しての操作はしないがisActiveCraftのトグル操作のみ行う部分
        isActiveCraft = !isActiveCraft;

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal("Craft Toggled!"));
    }

    @Override
    public boolean isActiveInventory()
    {
        return this.isActiveInventory;
    }

    @Override
    public boolean isActiveArmor()
    {
        return this.isActiveArmor;
    }

    @Override
    public boolean isActiveOffhand()
    {
        return this.isActiveOffhand;
    }

    @Override
    public boolean isActiveCraft()
    {
        return this.isActiveCraft;
    }

    @Override
    public void changeStorageSize(int change, Player player)
    {
        //Todo やっぱり一時スロットを別にするのはやめる
        //Todo 正直処理が重くなるし意味がない

        //なんかマイナス方向に小さくしたときにアイテムが消滅する？
        //ホットバーの奇数スロットに入れたアイテムがもれなく消滅している
        //たぶん拾ったときに一番スロットにものが入ってそのまま消滅という流れ
        //スロットは利用可能だがリストは利用不能という状態に陥っていた
        //アイテムリストがsetを拒絶している？
        //しかもスロット数をへらすたびに偶数奇数が入れ替わっている？
        //そしてなぜか一つスロットに入れただけなのに27個に増える->ただの同期問題

        //多分正しくロックがかかっていない
        //setしたときの同期




        //Todo SlotTypeがINVENTORYではなくHOTBARになっているためにバグっている？

        //Todo とりあえず通常インベントリの減少がうまく行ったら一段落
        //Todo 消費した食べ物がアイテムスロット補充されない->数はあっているのでアップデート処理が行われていないかも？
        //Todo 食べ物はサーバー上ではちゃんと消費されているし補充もされているがクライアント側に同期されていない->見えなくなっていたスロットに入っていた可能性がある
        //Todo 36スロットを下回ってすぐに歯抜け現象が起こる，はじめにホットバーから使い物にならなくなる->リストの逆転が不必要な位置で繰り返されていたのが原因
        //Todo 増殖とかはしない模様，単にスロットのアクセスと同期，validslotメソッドが悪さしている
        //Todo InventoryEffectのapplyに新しいオーバーロードを追加，引数でスロット変更数を変えられるようにする．

        //Todo InventoryActivateFoodの作成，常に減らす方向

        //Todo 2x2クラフトの無効化ロジックを実装する




        //Todo craftingcontainerクラスを書き換える必要があるかも
        //Todo craftingcontainer内のitemsにアイテムが格納されたら問答無用でその場に吐き出す
        //craftingcontainerはplayer.getInventory().getCraftingSlotsで参照可能


        //Todo 結果スロットのみ少し大きいのでカバー絵を変更可能なようにする



        //ここまでやった

        //はじめにホイール選択できないようにする
        //マウスの動きはnet.minecraft.client.MouseHandlerクラスによって定義されている
        //変更するとすればonScroll,ただMath.signumから符号(チルト方向)のみを入手しているので変更不要か

        //Inventory内のメソッド
        //isHobarSlot，9以内の番号を返すとtrueになる
        //swapPaint,これはたぶんスロット位置を9->1や1->9に飛ばすための部分？
        //getSuitableHotbarSlot
        //geteSelectionSizeがホットバーのサイズをちゃんと返すように

        //AbstractContainerScreen内のメソッド
        //checkHotbarMouseClicked

        //ホットバーの動きはホイール駆動->abstractContainerScreen->Inventoryの順に発火されるのでこれだけでokか？
        //selected格納時に手持ちアイテムがサーバーに送られる？
        //とりあえずサーバー側は手持ちアイテムを把握しているはずなのでどこかで送られている




        //拾わないかの確認
        //inventory.itemsリストが正しく閉鎖されていればアイテムは格納されないはず


        //次にレンダリング
        //Guiクラス内に存在するrenderHotbarメソッドの書き換えを行う
        //renderHotbar自体がたぶん9回呼ばれている？ とりあえず絵の場所を確認すべき

        //Todo 不使用ホットバースロットの除去・非表示機能の実装
        //Todo インベントリ・スロット・スクリーン全部を書き換える必要がある
        //Todo HUDの表示クラスを見つける必要がある(TinyInvを参考にする？)


        //Gui.javaのrenderHotbar内でレンダリングが行われている
        //マウスホイールによる選択はどこか？
        //MouseHandlerクラス内で処理内容が決定されている


        //ここまでやった

        //Todo クリエイティブモードのメニューの修正
        //Todo たぶんクリエメニューのみスロットの追加時の動作がおかしいのでSlotTypeが効いていない？

        //Todo CreativeModeInventoryScreen内の491行目のところでインベントリの初期化を行っている
        //Todo addSlotメソッドを使っていない

        //Todo Containermenuを介していないので対応不能，Screen側でSlotのチェック機能をつける？
        //Todo SlotTypeにUndefinedを作る


        //ここまでやった

        //Todo なんかサバイバルモードにして拾っても個数が増えない
        //Todo どこかのスロットがfalseになっていない？
        //Todo 36スロット以上存在するときは正常に機能しているっぽい
        //Todo 予想だけどたぶん空きスロット調査メソッドがスロットのfalse状態を認識していない
        //Todo 35番スロットを空きスロットとしてご認識している様子が見受けられる

        //Todo ひとまずLockList側のsetメソッドを監視
        //Todo 空きスロット探索メソッドの書き換えも必要ならば行うべき
        //Todo for文の定義ズレが発生していただけか


        //Todo 保存系の処理の実装
        //NBTにインベントリの開放状態を保存する
        //インベントリ変更系メソッド全てに保存メソッドの呼び出しを追加
        //インベントリ・メニューの初期化時にNBTから状態を読み込む
        //もし特に保存されている情報がなければConfigからデフォルト設定を読み出して適用
        //PlayerMixin内のonAddDataより新たな値を追加？
        //PlayerInventorySizeDataですでに管理されている,取得はClientInventoryDataからstaticメソッドより入手可


        //ここまでやった
        //toggle機能しか無いので指定の状態にすることが不可能？
        //toggleメソッドとsetメソッドを逆にする必要あり->toggleからそれぞれの状態に応じたsetを呼び出す形式にする

        inventorySize += change;
        LockableItemStackList newItems;
        //とりあえずLockableItemStackListとして宣言してから挿入する？


        //インベントリは必ず1スロット残す
        //インベントリをゼロスロットにするオプションはコマンドとして実装する
        if(inventorySize < 1)inventorySize = 1;

        hotbarSize = 9;
        if(inventorySize< 36)
        {
            //36以内になってしまう場合にはスロットは36固定
            newItems = LockableItemStackList.withSize(36, (Inventory)(Object)this,false);

            for(int i=0; i< (36-inventorySize) ; i++)
            {
                //まず頭から順にtrueにしていく
                newItems.lockList.set(35-i, true);
                newItems.lockList.forEach((b)->{System.out.println(" val is "+ b);});
            }

            //減らすべき分の要素のstopperをtrueにしていく
            //置き換えのときのsetで弾かれて自動で放り投げられるのでほかはそのままでOK?
            if(inventorySize < 9)
            {
                hotbarSize = inventorySize;
                if(selected > inventorySize-1)selected = inventorySize-1;
            }
        }
        else
        {
            newItems = LockableItemStackList.withSize(inventorySize, (Inventory)(Object)this,false);
        }



        //置き換え
        for(int i=0; i<(change>0?items:newItems).size(); i++)
        {
            newItems.set(i, items.get(i));
            items.set(i, ItemStack.EMPTY);
        }

        //ぶちまけ
        for(ItemStack item: items)
        {
            Level level = player.level;
            ItemEntity itementity = new ItemEntity(level, player.getX(), player.getEyeY() - 0.3, player.getZ(), item);
            itementity.setDefaultPickUpDelay();
            itementity.setThrower(player.getUUID());
            level.addFreshEntity(itementity);
        }

        //最後にitemsを更新，参照をcompartmentsに挿入して終了
        compartments.remove(items);
        items = newItems;
        compartments.add(0, items);

        //items.forEach(System.out::println);

        if(player.getLevel().isClientSide()) player.sendSystemMessage(Component.literal(String.format("Storage Size Changed to %s", change)));
    }

    @Override
    public boolean isValidSlot(int id)
    {
        if(id < 36)
        {
            return !((LockableItemStackList)items).lockList.get(id);
        }
        //armor
        else if(id >= 36 && id < 40)
        {
            return !((LockableItemStackList)armor).lockList.get(id-36);
        }
        //offhand
        else if(id == 40)
        {
            return !((LockableItemStackList)offhand).lockList.get(id-40);
        }
        //minipouch
        else if(id > 40)
        {
            return !((LockableItemStackList)items).lockList.get(id-5);
        }
        return true;
    }


    @Inject(method = "isHotbarSlot(I)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void onDetectHotbarSlot(int p_36046_, CallbackInfoReturnable<Boolean> cir)
    {
        //単にスロットをホットバーとして認識しなくなるだけ，とくにいらないかも？
        //とりあえずホットバーの最大値はここで指定する
        cir.setReturnValue(p_36046_ >= 0 && p_36046_ <9);
    }

    @Inject(method = "swapPaint(D)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSwapaint(double p_35989_, CallbackInfo ci)
    {


        //こいついじると選択不能になる！！ ただしマウスのみ
        int i = (int)Math.signum(p_35989_);

        for(this.selected -= i; this.selected < 0; this.selected += hotbarSize) {
        }

        while(this.selected >= hotbarSize) {
            this.selected -= hotbarSize;
        }

        ci.cancel();
    }

    @Inject(method = "getSuitableHotbarSlot()I", at = @At(value = "RETURN"), cancellable = true)
    public void onGetSuitableHotbarSlot(CallbackInfoReturnable<Integer> cir)
    {
        System.out.println("suitableHotbar" + cir.getReturnValue());
    }

    @Inject(method = "getSelectionSize()I", at = @At(value = "HEAD"), cancellable = true)
    private static void onGetSelectionSize(CallbackInfoReturnable<Integer> cir)
    {

    }


    @Override
    public int getMaxPage()
    {
        return maxPage;
    }

    @Override
    public int getInventorySize()
    {
        return inventorySize;
    }

    @Override
    public int getHotbarSize()
    {
        return hotbarSize;
    }

    @Inject(method = "save(Lnet/minecraft/nbt/ListTag;)Lnet/minecraft/nbt/ListTag;", at = @At(value = "HEAD"), cancellable = true)
    public void onSave(ListTag tags, CallbackInfoReturnable<ListTag> cir)
    {
        for(int i = 0; i < 36; ++i)
        {
            if(!items.get(i).isEmpty())
            {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte) i);
                items.get(i).save(compoundtag);
                tags.add(compoundtag);
            }
        }

        for(int j = 0; j < this.armor.size(); ++j)
        {
            if(!armor.get(j).isEmpty())
            {
                CompoundTag compoundtag1 = new CompoundTag();
                compoundtag1.putByte("Slot", (byte) (j + 100));
                armor.get(j).save(compoundtag1);
                tags.add(compoundtag1);
            }
        }

        for(int k = 0; k < this.offhand.size(); ++k)
        {
            if(!offhand.get(k).isEmpty())
            {
                CompoundTag compoundtag2 = new CompoundTag();
                compoundtag2.putByte("Slot", (byte) (k + 150));
                offhand.get(k).save(compoundtag2);
                tags.add(compoundtag2);
            }
        }

        cir.setReturnValue(tags);
    }

    @Inject(method = "load(Lnet/minecraft/nbt/ListTag;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onLoad(ListTag tags, CallbackInfo ci)
    {
        items.clear();
        armor.clear();
        offhand.clear();

        for(int i = 0; i < tags.size(); ++i)
        {
            CompoundTag compoundtag = tags.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if(!itemstack.isEmpty())
            {
                if(j >= 0 && j < 36)
                {
                    items.set(j, itemstack);
                } else if(j >= 100 && j < armor.size() + 100)
                {
                    armor.set(j - 100, itemstack);
                } else if(j >= 150 && j < offhand.size() + 150)
                {
                    offhand.set(j - 150, itemstack);
                }
            }
        }

        //items.forEach(System.out::println);
        ci.cancel();
    }

    @Inject(method = "setItem(ILnet/minecraft/world/item/ItemStack;)V", at = @At(value = "HEAD"), cancellable = true)
    public void onSetItem(int id, ItemStack itemStack, CallbackInfo ci)
    {
        //System.out.println("setItem");
        //System.out.printf("%s, %s%n", String.valueOf(id), itemStack.toString());


        if(id < 36)
        {
            if(id + 1 > items.size()) ci.cancel();
            else if(items != null)
            {
                items.set(id, itemStack);
            }
            ci.cancel();
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) ci.cancel();
            else if(armor != null)
            {
                armor.set(id - 36, itemStack);
            }
            ci.cancel();
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) ci.cancel();
            else if(offhand != null)
            {
                offhand.set(id - 40, itemStack);
            }
            ci.cancel();
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) ci.cancel();
            else if(items != null)
            {
                items.set(id - 5, itemStack);
            }
            ci.cancel();
        } else
        {
            ci.cancel();
        }

    }

    @Inject(method = "getItem(I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onGetItem(int id, CallbackInfoReturnable<ItemStack> cir)
    {
        if(id < 36)
        {
            if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null)
            {
                cir.setReturnValue(items.get(id));
            }
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(armor != null)
            {
                cir.setReturnValue(armor.get(id - 36));
            }
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(offhand != null)
            {
                cir.setReturnValue(offhand.get(id - 40));
            }
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null)
            {
                cir.setReturnValue(items.get(id - 5));
            }
        }

        //System.out.println(cir.getReturnValue().toString());
    }



    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "HEAD"), cancellable = true)
    public void onRemoveItem(int id, int decrement, CallbackInfoReturnable<ItemStack> cir)
    {
        //vanilla inventory
        if(id < 36)
        {
            if(id + 1 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null && !items.get(id).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(items, id, decrement));
            }
        }

        //armor
        else if(id >= 36 && id < 40)
        {
            if(id - 35 > armor.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(armor != null && !armor.get(id - 36).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(armor, id - 36, decrement));
            }
        }

        //offhand
        else if(id == 40)
        {
            if(id - 39 > offhand.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(offhand != null && !offhand.get(id - 40).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(offhand, id - 40, decrement));
            }
        }

        //minipouch
        else if(id > 40)
        {
            if(id - 40 > items.size()) cir.setReturnValue(ItemStack.EMPTY);
            else if(items != null && !items.get(id - 5).isEmpty())
            {
                cir.setReturnValue(ContainerHelper.removeItem(items, id - 5, decrement));
            }
        } else
        {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Override
    public ListTag saveAdditional(ListTag tag)
    {
        for(int i = 36; i < items.size(); ++i)
        {
            //System.out.println("saveAdditional " + i);
            if(!items.get(i).isEmpty())
            {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                items.get(i).save(compoundtag);
                tag.add(compoundtag);
            }
        }
        return tag;
    }

    @Override
    public void loadAdditional(ListTag tag)
    {
        for(int i = 0; i < tag.size(); ++i)
        {
            CompoundTag compoundtag = tag.getCompound(i);
            int j = compoundtag.getInt("Slot");
            ItemStack itemstack = ItemStack.of(compoundtag);
            if(!itemstack.isEmpty())
            {
                if(j < items.size())
                {
                    items.set(j, itemstack);
                }
            }
        }
    }
}
