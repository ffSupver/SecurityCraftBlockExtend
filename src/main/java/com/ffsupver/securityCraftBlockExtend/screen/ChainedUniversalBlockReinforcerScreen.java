package com.ffsupver.securityCraftBlockExtend.screen;

import com.ffsupver.securityCraftBlockExtend.SecurityCraftBlockExtend;
import com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerFilter;
import net.geforcemods.securitycraft.screen.components.ToggleComponentButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

import static com.ffsupver.securityCraftBlockExtend.screen.ChainedUniversalBlockReinforcerMenu.getChainedPositionValue;

public class ChainedUniversalBlockReinforcerScreen extends AbstractContainerScreen<ChainedUniversalBlockReinforcerMenu> {
    private static final ResourceLocation TEXTURE = SecurityCraftBlockExtend.asResource("textures/gui/chained_universal_block_reinforcer.png");
    private final EditBox[] inputs = new EditBox[6];
    private final SelectButton[] buttons = new SelectButton[6];
    private ToggleComponentButton chainedModeButton;


    public ChainedUniversalBlockReinforcerScreen(ChainedUniversalBlockReinforcerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageHeight = 186;
        imageWidth = 186;
    }

    @Override
    protected void init() {
        super.init();

        for (int i = 0; i < 6; i++) {
            final int index = i;
            EditBox box = new EditBox(this.font, leftPos + 132, topPos + 18 + i * 11, 28, 9, Component.literal("0"));
            box.setCanLoseFocus(true);
            box.setBordered(false);
            box.setMaxLength(4);
            box.setValue(String.valueOf(getChainedPositionValue(this.menu.serverData,i)));
            // 非负数
            box.setFilter(text -> text.matches("\\d*"));
            box.setResponder(text -> {
                if (!text.isEmpty()) {
                    int value = Integer.parseInt(text);
                    this.menu.onInputChanged(index, value);
                }
            });
            this.inputs[i] = box;
            this.addWidget(box);
        }
        this.menu.setOnPositionChanged((index,data)->{
            for (int i = 0; i < 6; i++){
                inputs[i].setValue(String.valueOf(getChainedPositionValue(data,i)));
            }
        });


        for (int i = 0; i < ChainedUniversalBlockReinforcerFilter.FILTER_COUNT; i++) {
            SelectButton button = new SelectButton( leftPos + 9 + i * 9,  topPos + 18, Component.literal("1"),i);
            buttons[i] = button;
            this.addWidget(button);
        }

        this.chainedModeButton = addRenderableWidget(new ToggleComponentButton(
                leftPos + 108, topPos + 86,70,16,
                index-> {
                    if (index == 1) {
                        return Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.mode.filling");
                    } else {
                        return Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.mode.chained");
                    }
                },
        this.menu.isFillingMode ? 1 : 0,2,
        button->{
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, ChainedUniversalBlockReinforcerMenu.SET_MODE_BUTTON_ID);
            }
        }
        ));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (EditBox box : inputs) {
            box.tick();
        };

        // sync chainedModeButton state
        if (menu.isFillingMode != (chainedModeButton.getCurrentIndex() == 1)){
            chainedModeButton.setCurrentIndex(menu.isFillingMode ? 1 : 0);
            chainedModeButton.onValueChange();
        }
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
        if (!menu.isFillingMode){
            guiGraphics.blit(TEXTURE, leftPos + 9, topPos + 26, 191, 0, 48, 48);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        for (EditBox input : inputs) {
            input.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        for (SelectButton button : buttons) {
            button.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        if (pMouseX > leftPos + 5 && pMouseX < leftPos + 5 + 176 && pMouseY > topPos + 7 && pMouseY < topPos + 7 + 77){
            if (this.menu.isFillingMode){
                renderTooltip(guiGraphics, pMouseX, pMouseY);
                // render filter button tooltip
                for (int i = 0; i < ChainedUniversalBlockReinforcerFilter.FILTER_COUNT; i++){
                    if (buttons[i].isHovered()){
                        guiGraphics.renderTooltip(this.font, List.of(
                                Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.filter",i + 1).getVisualOrderText(),
                                Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.filter."+(buttons[i].isActiveState() ? "active" : "inactive"))
                                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)).getVisualOrderText()
                        ), pMouseX, pMouseY);
                    }
                }
                // render chainedPositionData tooltip
                for (int i = 0;i < 6;i++){
                    int dataX = leftPos + 120;
                    int dataY = topPos + 16;
                    int dataWidth = 38;
                    int dataHeight = 11;
                    if (isMouseInBox(pMouseX,pMouseY,dataX,dataY + i*dataHeight,dataWidth,dataHeight)){
                        guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position."+i,getChainedPositionValue(menu.serverData,i)), pMouseX, pMouseY);
                    }
                }
                if (isMouseInBox(pMouseX,pMouseY,leftPos + 72, topPos + 19, 8, 4)) {
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.0",getChainedPositionValue(menu.serverData,0)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 72, topPos + 23, 34, 1)) {
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.2",getChainedPositionValue(menu.serverData,2)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 71, topPos + 24, 1, 58)){
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.1",getChainedPositionValue(menu.serverData,1)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 88, topPos + 55, 3, 24)){
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.4",getChainedPositionValue(menu.serverData,4)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 85, topPos + 79, 5, 3)){
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.3",getChainedPositionValue(menu.serverData,3)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 72, topPos + 82, 13, 1)){
                    guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position.5",getChainedPositionValue(menu.serverData,5)), pMouseX, pMouseY);
                }else if (isMouseInBox(pMouseX,pMouseY,leftPos + 88, topPos + 52, 3, 3)){
                    List<FormattedCharSequence> tooltips = new ArrayList<>();
                    for (int i = 0;i < 6;i++){
                        tooltips.add(Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.position."+i,getChainedPositionValue(menu.serverData,i)).getVisualOrderText());
                    }
                    guiGraphics.renderTooltip(this.font, tooltips, pMouseX, pMouseY);
                }
            }else {
                guiGraphics.renderTooltip(this.font,Component.translatable("gui.securitycraft_block_extend.chained_universal_block_reinforcer.mode.only_in_filling"), pMouseX, pMouseY);
            }
        }else {
            renderTooltip(guiGraphics, pMouseX, pMouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        guiGraphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 5, 0x404040, false);
        guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 0x404040, false);
    }

    private boolean isMouseInBox(int mouseX,int mouseY,int x,int y,int boxWidth,int boxHeight)
    {
        return mouseX >= x && mouseX < x + boxWidth && mouseY >= y && mouseY < y + boxHeight;
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
