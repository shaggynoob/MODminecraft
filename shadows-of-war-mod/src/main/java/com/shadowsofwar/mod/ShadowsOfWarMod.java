package com.shadowsofwar.mod;

import com.shadowsofwar.mod.entity.custom.ShieldOrcZombie;
import com.shadowsofwar.mod.entity.custom.DualSwordOrcZombie;
import com.shadowsofwar.mod.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("shadowsofwar")
public class ShadowsOfWarMod {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "shadowsofwar");

    public static final RegistryObject<EntityType<ShieldOrcZombie>> SHIELD_ORC_ZOMBIE = 
        ENTITY_TYPES.register("shield_orc_zombie", () -> 
            EntityType.Builder.of(ShieldOrcZombie::new, MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .clientTrackingRange(8)
                .build("shield_orc_zombie"));

    public static final RegistryObject<EntityType<DualSwordOrcZombie>> DUAL_SWORD_ORC_ZOMBIE = 
        ENTITY_TYPES.register("dual_sword_orc_zombie", () -> 
            EntityType.Builder.of(DualSwordOrcZombie::new, MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .clientTrackingRange(8)
                .build("dual_sword_orc_zombie"));

    // Spawn Eggs
    public static final RegistryObject<Item> SHIELD_ORC_SPAWN_EGG = ModItems.ITEMS.register("shield_orc_spawn_egg", 
        () -> new SpawnEggItem(SHIELD_ORC_ZOMBIE.get(), 0x3A5F0B, 0x8B4513, new Item.Properties()));
    
    public static final RegistryObject<Item> DUAL_SWORD_ORC_SPAWN_EGG = ModItems.ITEMS.register("dual_sword_orc_spawn_egg", 
        () -> new SpawnEggItem(DUAL_SWORD_ORC_ZOMBIE.get(), 0x3A5F0B, 0xCD853F, new Item.Properties()));

    public ShadowsOfWarMod() {
        net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus().register(this);
        ENTITY_TYPES.register(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SHIELD_ORC_ZOMBIE.get(), ShieldOrcZombie.createAttributes().build());
        event.put(DUAL_SWORD_ORC_ZOMBIE.get(), DualSwordOrcZombie.createAttributes().build());
    }
}
