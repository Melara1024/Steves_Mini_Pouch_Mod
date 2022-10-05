package ga.melara.stevesminipouch.mixin;

import ga.melara.stevesminipouch.util.IHasSlotType;
import ga.melara.stevesminipouch.util.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin implements IHasSlotType {

    public SlotType type = SlotType.OTHER;
    public int page = 0;

    private static final int ESCAPE_RANGE = 10000;

    @Shadow
    public int x;
    @Shadow
    public int y;

    private boolean isHiding = false;


    @Override
    public void setType(SlotType type)
    {
        this.type = type;
    }

    @Override
    public SlotType getType()
    {
        return this.type;
    }

    @Override
    public void setPage(int page)
    {
        this.page = page;
    }

    @Override
    public int getPage()
    {
        return this.page;
    }

    @Override
    public void hide()
    {
        if(!isHiding)this.y = this.y + ESCAPE_RANGE;
        isHiding = true;
    }

    @Override
    public void show()
    {
        if(isHiding)this.y = this.y - ESCAPE_RANGE;
        isHiding = false;
    }

}
