package ga.melara.stevesminipouch.integration.curio;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class Curio {

    public static boolean hasCharmCurio(String item, Player player)
    {
        if (ModList.get().isLoaded("curios")) {
            Optional<SlotResult> slot = CuriosApi.getCuriosHelper().findFirstCurio(player, itemStack -> itemStack.getItem().toString().equals(item));
            if (slot.isPresent()) {
                return true;
            }
        }
        return false;
    }
}
