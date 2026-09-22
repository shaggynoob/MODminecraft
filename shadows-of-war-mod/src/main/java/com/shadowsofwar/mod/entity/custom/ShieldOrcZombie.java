package com.shadowsofwar.mod.entity.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Random;

public class ShieldOrcZombie extends Zombie {
    
    private static final String[] ORC_NAMES = {
        "Gorbag", "Shagrat", "Lugburz", "Snaga", "Ugluk", "Grishnak", 
        "Yazneg", "Fimbul", "Azog", "Bolg", "Krod", "Dush", "Gorgor", 
        "Muzgash", "Radbug", "Sharku", "Thak", "Uruk", "Vlag"
    };
    
    private final Random random = new Random();

    public ShieldOrcZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        this.setCustomNameVisible(true);
        this.setAlwaysShowNameTag(true);
        generateRandomName();
        equipItems();
    }

    private void generateRandomName() {
        String randomName = ORC_NAMES[random.nextInt(ORC_NAMES.length)];
        this.setCustomName(net.minecraft.network.chat.Component.literal(randomName));
    }

    private void equipItems() {
        ItemStack ironSword = new ItemStack(Items.IRON_SWORD);
        ItemStack ironShield = new ItemStack(Items.SHIELD);
        
        this.setItemSlot(EquipmentSlot.MAINHAND, ironSword);
        this.setItemSlot(EquipmentSlot.OFFHAND, ironShield);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.5f);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.5f);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return true;
    }

    @Override
    protected void tickOffhandCombat(LivingEntity target) {
        // Lógica para usar el escudo activamente
        if (this.isHoldingInOffHand(item -> item.getItem() == Items.SHIELD) && target != null) {
            double distanceToTarget = this.distanceTo(target);
            if (distanceToTarget < 8.0 && this.random.nextFloat() < 0.3F) {
                this.startUsingItem(InteractionHand.OFF_HAND);
            }
        }
        super.tickOffhandCombat(target);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 4.0D) // 2 puntos más que el zombie normal (2.0)
            .add(Attributes.ARMOR, 8.0D) // 4 puntos más que el zombie normal (4.0)
            .add(Attributes.MAX_HEALTH, 30.0D);
    }
}
