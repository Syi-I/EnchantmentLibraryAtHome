package com.uberhelixx.enchlibathome.util.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;

public interface IDataUpdateListener {

    /**
     * Called when the client receives a data update from the server.<br>
     * Specifically, called from {@link BaseMenu#setData(int, int)} when a {@link ClientboundContainerSetDataPacket} is received.
     *
     * @param id    The ID of the data slot that was updated.
     * @param value The new value of the data slot.
     */
    void dataUpdated(int id, int value);

}
