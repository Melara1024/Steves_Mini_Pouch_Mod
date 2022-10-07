package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IStorageChangable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


//こいつはサーバー側
@Mixin(Inventory.class)
public class InventoryMixin implements IStorageChangable {

    /*
    Todo コマンド・インベントリ拡張アイテムの放つイベントに合わせてitemsやsizeを増減する
     */

    private int size = 36;

    @Shadow
    public NonNullList<ItemStack> items;


    @Override
    public void changeStorageSize(int change, Level level, LivingEntity entity)
    {
        if(change < 0)return;

        size = (int)(Math.ceil((change-9) / 27)*2);

        System.out.println("storagesize changed to " + size);
        //大きくなる方向ならEMPTYを増やす
        NonNullList<ItemStack> newList = NonNullList.withSize(size, ItemStack.EMPTY);

        if(change >= size)
        {
            for(int i=0; i<items.size(); i++)
            {
                newList.set(i, items.get(i));
            }
        }
        //小さくなる方向なら引数Levelのentityがいる位置にアイテムエンティティを召喚
        else
        {
            for(int i=0; i< newList.size(); i++)
            {
                newList.set(i, items.get(i));
            }

            for(int j= newList.size(); j< items.size(); j++)
            {
                //items.get(j).
                ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getEyeY() - 0.3, entity.getZ(), items.get(j));
                itementity.setDefaultPickUpDelay();
                itementity.setThrower(entity.getUUID());
                level.addFreshEntity(itementity);
            }
        }





    }

}
