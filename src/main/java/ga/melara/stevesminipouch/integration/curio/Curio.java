package ga.melara.stevesminipouch.integration.curio;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

public class Curio {
    public static boolean hasCharmCurio(String item, PlayerEntity player)
    {
        if (ModList.get().isLoaded("curios")) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> slot = CuriosApi.getCuriosHelper().findEquippedCurio(itemStack -> itemStack.getItem().toString().equals(item), player);
            if (slot.isPresent()) {
                return true;
            }
        }
        return false;
    }
}
