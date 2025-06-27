package com.uberhelixx.enchlibathome.common.block;

import com.uberhelixx.enchlibathome.client.screen.LibraryMenu;
import com.uberhelixx.enchlibathome.common.blockentity.LibraryBE;
import com.uberhelixx.enchlibathome.util.menu.MenuUtil;
import com.uberhelixx.enchlibathome.util.menu.SimplerMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

public class LibraryBlock extends HorizontalDirectionalBlock implements EntityBlock {
    
    public static final Component NAME = Component.translatable("apotheosis.ench.library");
    
    protected final BlockEntitySupplier<? extends LibraryBE> tileSupplier;
    protected final int maxLevel;
    
    public LibraryBlock(BlockEntitySupplier<? extends LibraryBE> tileSupplier, int maxLevel) {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(5.0F, 1200.0F));
        this.tileSupplier = tileSupplier;
        this.maxLevel = maxLevel;
    }
    
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return MenuUtil.openGui(pPlayer, pPos, LibraryMenu::new);
    }
    
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimplerMenuProvider<>(pLevel, pPos, LibraryMenu::new);
    }
    
    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return this.tileSupplier.create(pPos, pState);
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockState pState, HitResult pTarget, BlockGetter pLevel, BlockPos pPos, Player pPlayer) {
        ItemStack s = new ItemStack(this);
        BlockEntity te = pLevel.getBlockEntity(pPos);
        if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
        return s;
    }
    
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        BlockEntity te = pLevel.getBlockEntity(pPos);
        if (te != null) {
            te.load(pStack.getOrCreateTagElement("BlockEntityTag"));
        }
    }
    
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder ctx) {
        ItemStack stack = new ItemStack(this);
        BlockEntity blockEntity = ctx.getParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity != null) stack.getOrCreateTag().put("BlockEntityTag", blockEntity.saveWithoutMetadata());
        return Arrays.asList(stack);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.enchlib.capacity", Component.translatable("enchantment.level." + this.maxLevel)).withStyle(ChatFormatting.GOLD));
        CompoundTag tag = pStack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains("Points")) {
            pTooltip.add(Component.translatable("tooltip.enchlib.item", tag.getCompound("Points").size()).withStyle(ChatFormatting.GOLD));
        }
    }
    
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean isMoving) {
        if (pNewState.getBlock() != this) {
            pLevel.removeBlockEntity(pPos);
        }
    }
}
