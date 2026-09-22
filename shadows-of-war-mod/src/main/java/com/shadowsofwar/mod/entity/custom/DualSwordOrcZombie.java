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

public class DualSwordOrcZombie extends Zombie {
    
    private static final String[] ORC_NAMES = {
        "Gorbag", "Shagrat", "Lugburz", "Snaga", "Ugluk", "Grishnak", 
        "Yazneg", "Fimbul", "Azog", "Bolg", "Krod", "Dush", "Gorgor", 
        "Muzgash", "Radbug", "Sharku", "Thak", "Uruk", "Vlag"
    };
    
    private final Random random = new Random();
    private int attackCooldown = 0;
    private boolean usingOffhandAttack = false;

    public DualSwordOrcZombie(EntityType<? extends Zombie> entityType, Level level) {
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
        ItemStack mainHandSword = new ItemStack(Items.IRON_SWORD);
        ItemStack offhandSword = new ItemStack(Items.IRON_SWORD);
        
        this.setItemSlot(EquipmentSlot.MAINHAND, mainHandSword);
        this.setItemSlot(EquipmentSlot.OFFHAND, offhandSword);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.5f);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.5f);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new DualSwordMeleeAttackGoal());
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
    public void aiStep() {
        super.aiStep();
        
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        LivingEntity target = this.getTarget();
        if (target != null && attackCooldown == 0 && this.distanceTo(target) < 4.0D) {
            performDualAttack(target);
        }
    }

    private void performDualAttack(LivingEntity target) {
        // Ataque con la mano principal
        this.swing(InteractionHand.MAIN_HAND);
        target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        
        attackCooldown = 10;
        
        // Pequeño delay para el segundo ataque
        this.level().getGameTime();
        
        // Ataque con la mano secundaria (offhand)
        if (!this.level().isClientSide()) {
            this.swing(InteractionHand.OFF_HAND);
            target.hurt(this.damageSources().mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8));
        }
        
        attackCooldown = 20; // Cooldown total entre combos dobles
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D) // Ligeramente más rápido
            .add(Attributes.ATTACK_DAMAGE, 4.0D) // 2 puntos más que el zombie normal (2.0)
            .add(Attributes.ARMOR, 8.0D) // 4 puntos más que el zombie normal (4.0)
            .add(Attributes.MAX_HEALTH, 28.0D);
    }

    class DualSwordMeleeAttackGoal extends MeleeAttackGoal {
        public DualSwordMeleeAttackGoal() {
            super(DualSwordOrcZombie.this, 1.2D, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double maxReach = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= maxReach && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.swing(InteractionHand.MAIN_HAND);
                
                // Primer ataque (main hand)
                enemy.hurt(this.mob.damageSources().mobAttack(this.mob), 
                    (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE));
                
                // Segundo ataque (offhand) con pequeño delay
                this.mob.schedule(() -> {
                    if (DualSwordOrcZombie.this.isAlive() && enemy.isAlive()) {
                        DualSwordOrcZombie.this.swing(InteractionHand.OFF_HAND);
                        enemy.hurt(DualSwordOrcZombie.this.damageSources().mobAttack(DualSwordOrcZombie.this), 
                            (float) (DualSwordOrcZombie.this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.7));
                    }
                }, 5);
            }
        }
    }
}
