package com.funmelonboy.pokemob.projectile;

import com.funmelonboy.pokemob.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

public class PokeballProject extends ThrowableItemProjectile {
    private boolean empty = true;

    public PokeballProject(final EntityType<? extends net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball> type, final Level level) {
        super(type, level);
    }

    public PokeballProject(final Level level, final LivingEntity mob, final ItemStack itemStack) {
        super(EntityTypes.SNOWBALL, mob, level, itemStack);
    }

    public PokeballProject(final Level level, final double x, final double y, final double z, final ItemStack itemStack) {
        super(EntityTypes.SNOWBALL, x, y, z, level, itemStack);
    }

    protected Item getDefaultItem() {
        return ModItems.POKEBALL;
    }

    private ParticleOptions getParticle() {
        ItemStack item = this.getItem();
        return (ParticleOptions)(item.isEmpty() ? ParticleTypes.ITEM_SNOWBALL : new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(item)));
    }

    public void handleEntityEvent(final @EntityEvent.Value byte id) {
        if (id == 3) {
            ParticleOptions particle = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }

    protected void onHitEntity(final EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        ServerLevel serverLevel = Objects.requireNonNull(entity.level().getServer()).getLevel(entity.level().dimension());
        assert serverLevel != null;

        if (empty){
            entity.kill(serverLevel);
            spawnAtLocation(serverLevel, ModItems.POKEBALL);
            setEmpty(false);
        }
        else {
            setEmpty(true);
        }
    }

    protected void onHit(final HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }

    }

    public boolean isEmpty() {
        return empty;
    }

    private void setEmpty(boolean empty) {
        this.empty = empty;
    }
}

