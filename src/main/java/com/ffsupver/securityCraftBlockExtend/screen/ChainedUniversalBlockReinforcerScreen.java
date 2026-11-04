package com.ffsupver.securityCraftBlockExtend.screen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerFilter;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChainedUniversalBlockReinforcerScreen extends AbstractContainerScreen<ChainedUniversalBlockReinforcerMenu> {
    private static final ResourceLocation TEXTURE = SecurityCraftBlockExtend.asResource("textures/gui/chained_universal_block_reinforcer.png");
    private final EditBox[] inputs = new EditBox[6];
    private final SelectButton[] buttons = new SelectButton[6];


    public ChainedUniversalBlockReinforcerScreen(ChainedUniversalBlockReinforcerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 186;
        imageWidth = 186;
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < 6; i++) {
            EditBox box = new EditBox(this.font, leftPos + 132, topPos + 18 + i * 11, 28, 9, Component.literal("0"));
            box.setCanLoseFocus(true);
            box.setBordered(false);
            box.setMaxLength(4);
            box.setValue(String.valueOf(switch (i){
                case 0-> menu.serverData.x();
                case 1-> menu.serverData.y();
                case 2-> menu.serverData.z();
                case 3-> menu.serverData.posX();
                case 4-> menu.serverData.posY();
                case 5-> menu.serverData.posZ();
                default-> "0";
            }));
            // 非负数
            box.setFilter(text -> text.matches("\\d*"));
            final int index = i;
            box.setResponder(text -> {
                if (!text.isEmpty()) {
                    this.menu.onInputChanged(index, Integer.parseInt(text));
                }
            });
            this.inputs[i] = box;
            this.addWidget(box);
        }

        ChainedUniversalBlockReinforcerFilter filter = this.menu.getFilter();
        for (int i = 0; i < ChainedUniversalBlockReinforcerFilter.FILTER_COUNT; i++) {
            SelectButton button = new SelectButton( leftPos + 9 + i * 9,  topPos + 18, Component.literal("1"),i);
            buttons[i] = button;
            this.addWidget(button);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (EditBox box : inputs) {
            box.tick();
        };
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode != 256 ){
            for (EditBox box : inputs){
                if (box.isFocused()){
                    return box.keyPressed(pKeyCode, pScanCode, pModifiers);
                }
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        String[] s = new String[6];
        for (int i = 0; i < 6; i++) {
            s[i] = inputs[i].getValue();
        }
        super.resize(pMinecraft, pWidth, pHeight);
        for (int i = 0; i < 6; i++) {
            inputs[i].setValue(s[i]);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        for (EditBox input : inputs) {
            input.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        for (SelectButton button : buttons) {
            button.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 5, 0x404040, false);
        guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 0x404040, false);
    }

    private class SelectButton extends AbstractButton {
        private final int index;
        public SelectButton(int pX, int pY, Component pMessage, int index) {
            super(pX, pY, 5, 5, pMessage);
            this.index = index;
        }

        @Override
        public void onPress() {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            int v = 0;
            if (isActiveState()){
                v = 10;
            }else if (isHovered){
                v = 5;
            }
            pGuiGraphics.blit(TEXTURE, getX(), getY(), 186, v, width, height);
            if (isSelectedState()){
                pGuiGraphics.blit(TEXTURE, getX(), getY(), 186, 15, width, height);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
            this.defaultButtonNarrationText(pNarrationElementOutput);
        }

        private boolean isActiveState(){
            return menu.getFilter().isActive(this.index);
        }

        private boolean isSelectedState(){
            return menu.getFilterIndex() == this.index;
        }
    }
}
