package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.data.PlayerInventorySizeData;
import ga.melara.stevesminipouch.data.StatsSynchronizer;

public interface IMenuSynchronizer
{
    void setStatsSynchronizer(StatsSynchronizer synchronizer);

    void initMenu(PlayerInventorySizeData data);
}
