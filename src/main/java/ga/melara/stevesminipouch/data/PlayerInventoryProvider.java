package ga.melara.stevesminipouch.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class PlayerInventoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<PlayerInventorySizeData> DATA = CapabilityManager.get(new CapabilityToken<>(){});

    private PlayerInventorySizeData playerData = null;

    private final LazyOptional<PlayerInventorySizeData> opt = LazyOptional.of(this::createData);

    @Nonnull
    private PlayerInventorySizeData createData() {
        if (playerData == null) {
            playerData = new PlayerInventorySizeData();
        }
        return playerData;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if (cap == DATA) {
            return opt.cast();
        }
        return LazyOptional.empty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public CompoundTag serializeNBT() {
        //セーブ
        CompoundTag nbt = new CompoundTag();
        createData().saveNBTData(nbt);
        return nbt;

    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //ロード
        createData().loadNBTData(nbt);

    }
}
