package ga.melara.stevesminipouch.util;

import ga.melara.stevesminipouch.stats.InventoryStatsData;
import ga.melara.stevesminipouch.stats.StatsSynchronizer;

public interface IMenuSynchronizer {
    void setStatsSynchronizer(StatsSynchronizer synchronizer);

    void initMenu(InventoryStatsData data);
}
