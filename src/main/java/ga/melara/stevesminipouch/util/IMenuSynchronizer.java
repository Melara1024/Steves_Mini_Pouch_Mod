package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.PlayerInventorySizeData;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;

public interface IMenuSynchronizer
{
    void setStatsSynchronizer(StatsSynchronizer synchronizer);

    void initMenu(PlayerInventorySizeData data);
}
