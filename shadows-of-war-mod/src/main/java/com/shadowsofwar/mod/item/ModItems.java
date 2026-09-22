package com.shadowsofwar.mod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, "shadowsofwar");

    // Los spawn eggs se registrarán dinámicamente
    public static final List<SpawnEggItem> SPAWN_EGGS = new ArrayList<>();

    public static void registerSpawnEggs() {
        // Los spawn eggs se crean programáticamente en el setup
    }
}
