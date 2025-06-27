package com.uberhelixx.enchlibathome.util.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.inventory.DataSlot;

/**
 * Simple DataSlot implementation that allows for lambda registration.
 * The other option is creation of anonymous classes.
 */
public class SimpleDataSlots {

    protected List<DataSlot> slots = new ArrayList<>();

    public void addSlot(DataSlot slot) {
        this.slots.add(slot);
    }

    public void addData(IntSupplier getter, IntConsumer setter) {
        this.addSlot(new LambdaDataSlot(getter, setter));
    }

    public void addData(BooleanSupplier getter, BooleanConsumer setter) {
        this.addData(() -> getter.getAsBoolean() ? 1 : 0, v -> setter.accept(v == 1));
    }

    public List<DataSlot> getSlots() {
        return this.slots;
    }

    public void register(Consumer<DataSlot> consumer) {
        this.slots.forEach(consumer);
    }

    public class LambdaDataSlot extends DataSlot {

        private final IntSupplier getter;
        private final IntConsumer setter;

        public LambdaDataSlot(IntSupplier getter, IntConsumer setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public int get() {
            return this.getter.getAsInt();
        }

        @Override
        public void set(int pValue) {
            this.setter.accept(pValue);
        }

    }

    public interface IDataAutoRegister {
        public void registerSlots(Consumer<DataSlot> consumer);
    }

}
