package mia.modmod.features.impl.general.chat;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.logging.LogUtils;
import mia.modmod.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;

@Environment(EnvType.CLIENT)
public class StaffChatComponent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_CHAT_HISTORY = 100;
    private static final int MESSAGE_INDENT = 4;
    private static final int BOTTOM_MARGIN = 40;
    private static final int TOOLTIP_MAX_WIDTH = 210;
    private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
    private static final Component DELETED_CHAT_MESSAGE;
    public static final int MESSAGE_BOTTOM_TO_MESSAGE_TOP = 8;
    public static final Identifier QUEUE_EXPAND_ID;
    private static final Style QUEUE_EXPAND_TEXT_STYLE;
    final Minecraft minecraft;
    private final ArrayListDeque<String> recentChat = new ArrayListDeque(100);
    private final List<GuiMessage> allMessages = Lists.newArrayList();
    private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
    private int chatScrollbarPos;
    private boolean newMessageSinceScroll;
    private Draft latestDraft;
    private @Nullable ChatScreen preservedScreen;
    private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList();

    public StaffChatComponent(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.recentChat.addAll(minecraft.commandHistory().history());
    }

    public void tick() {
        if (!this.messageDeletionQueue.isEmpty()) {
            this.processMessageDeletionQueue();
        }

    }

    private int forEachLine(AlphaCalculator alphaCalculator, LineConsumer lineConsumer) {
        int i = this.getLinesPerPage();
        int j = 0;

        for(int k = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, i) - 1; k >= 0; --k) {
            int l = k + this.chatScrollbarPos;
            GuiMessage.Line line = (GuiMessage.Line)this.trimmedMessages.get(l);
            float f = alphaCalculator.calculate(line);
            if (f > 1.0E-5F) {
                ++j;
                lineConsumer.accept(line, k, f);
            }
        }

        return j;
    }

    public void render(GuiGraphics guiGraphics, Font font, int i, int j, int k, boolean bl, boolean bl2) {
        guiGraphics.pose().pushMatrix();
        this.render((ChatGraphicsAccess)(bl ? new DrawingFocusedGraphicsAccess(guiGraphics, font, j, k, bl2) : new DrawingBackgroundGraphicsAccess(guiGraphics)), guiGraphics.guiHeight(), i, bl);
        guiGraphics.pose().popMatrix();
    }

    public void captureClickableText(ActiveTextCollector activeTextCollector, int i, int j, boolean bl) {
        this.render(new ClickableTextOnlyGraphicsAccess(activeTextCollector), i, j, bl);
    }

    private void render(final ChatGraphicsAccess chatGraphicsAccess, int i, int j, boolean bl) {
        if (!this.isChatHidden()) {
            int k = this.trimmedMessages.size();
            if (k > 0) {
                ProfilerFiller profilerFiller = Profiler.get();
                profilerFiller.push("chat");
                float f = (float)this.getScale();
                int l = Mth.ceil((float)this.getWidth() / f);
                final int m = Mth.floor((float)(i - 40) / f);
                final float g = ((Double)this.minecraft.options.chatOpacity().get()).floatValue() * 0.9F + 0.1F;
                float h = ((Double)this.minecraft.options.textBackgroundOpacity().get()).floatValue();
                Objects.requireNonNull(this.minecraft.font);
                final int n = 9;
                int o = 8;
                double d = (Double)this.minecraft.options.chatLineSpacing().get();
                final int p = (int)((double)n * (d + (double)1.0F));
                final int q = (int)Math.round((double)8.0F * (d + (double)1.0F) - (double)4.0F * d);
                long r = this.minecraft.getChatListener().queueSize();
                AlphaCalculator alphaCalculator = bl ? AlphaCalculator.FULLY_VISIBLE : AlphaCalculator.timeBased(j);
                chatGraphicsAccess.updatePose((matrix3x2f) -> {
                    matrix3x2f.scale(f, f);
                    matrix3x2f.translate(4.0F, 0.0F);
                });
                this.forEachLine(alphaCalculator, (line, lx, gx) -> {
                    final int m1 = m - lx * p;
                    final int n1 = m - p;
                    chatGraphicsAccess.fill(-4, n, l + 4 + 4, m, ARGB.black(gx * h));
                });
                if (r > 0L) {
                    chatGraphicsAccess.fill(-2, m, l + 4, m + n, ARGB.black(h));
                }

                int s = this.forEachLine(alphaCalculator, new LineConsumer() {
                    boolean hoveredOverCurrentMessage;

                    public void accept(GuiMessage.Line line, int i, float f) {
                        int j = m - i * p;
                        int k = j - p;
                        int l = j - q;
                        boolean bl = chatGraphicsAccess.handleMessage(l, f * g, line.content());
                        this.hoveredOverCurrentMessage |= bl;
                        boolean bl2;
                        if (line.endOfEntry()) {
                            bl2 = this.hoveredOverCurrentMessage;
                            this.hoveredOverCurrentMessage = false;
                        } else {
                            bl2 = false;
                        }

                        GuiMessageTag guiMessageTag = line.tag();
                        if (guiMessageTag != null) {
                            chatGraphicsAccess.handleTag(-4, k, -2, j, f * g, guiMessageTag);
                            if (guiMessageTag.icon() != null) {
                                int m2 = line.getTagIconLeft(Mod.MC.font);
                                int n2 = l + n;
                                chatGraphicsAccess.handleTagIcon(m2, n2, bl2, guiMessageTag, guiMessageTag.icon());
                            }
                        }

                    }
                });
                if (r > 0L) {
                    int t = m + n;
                    Component component = Component.translatable("chat.queue", new Object[]{r}).setStyle(QUEUE_EXPAND_TEXT_STYLE);
                    chatGraphicsAccess.handleMessage(t - 8, 0.5F * g, component.getVisualOrderText());
                }

                if (bl) {
                    int t = k * p;
                    int u = s * p;
                    int v = this.chatScrollbarPos * u / k - m;
                    int w = u * u / t;
                    if (t != u) {
                        int x = v > 0 ? 170 : 96;
                        int y = this.newMessageSinceScroll ? 13382451 : 3355562;
                        int z = l + 4;
                        chatGraphicsAccess.fill(z, -v, z + 2, -v - w, ARGB.color(x, y));
                        chatGraphicsAccess.fill(z + 2, -v, z + 1, -v - w, ARGB.color(x, 13421772));
                    }
                }

                profilerFiller.pop();
            }
        }
    }

    private boolean isChatHidden() {
        return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
    }

    public void clearMessages(boolean bl) {
        this.minecraft.getChatListener().flushQueue();
        this.messageDeletionQueue.clear();
        this.trimmedMessages.clear();
        this.allMessages.clear();
        if (bl) {
            this.recentChat.clear();
            this.recentChat.addAll(this.minecraft.commandHistory().history());
        }

    }

    public void addMessage(Component component) {
        this.addMessage(component, (MessageSignature)null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
    }

    public void addMessage(Component component, @Nullable MessageSignature messageSignature, @Nullable GuiMessageTag guiMessageTag) {
        GuiMessage guiMessage = new GuiMessage(this.minecraft.gui.getGuiTicks(), component, messageSignature, guiMessageTag);
        this.logChatMessage(guiMessage);
        this.addMessageToDisplayQueue(guiMessage);
        this.addMessageToQueue(guiMessage);
    }

    private void logChatMessage(GuiMessage guiMessage) {
        String string = guiMessage.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String string2 = (String) Optionull.map(guiMessage.tag(), GuiMessageTag::logTag);
        if (string2 != null) {
            LOGGER.info("[{}] [CHAT] {}", string2, string);
        } else {
            LOGGER.info("[CHAT] {}", string);
        }

    }

    private void addMessageToDisplayQueue(GuiMessage guiMessage) {
        int i = Mth.floor((double)this.getWidth() / this.getScale());
        List<FormattedCharSequence> list = guiMessage.splitLines(this.minecraft.font, i);
        boolean bl = this.isChatFocused();

        for(int j = 0; j < list.size(); ++j) {
            FormattedCharSequence formattedCharSequence = (FormattedCharSequence)list.get(j);
            if (bl && this.chatScrollbarPos > 0) {
                this.newMessageSinceScroll = true;
                this.scrollChat(1);
            }

            boolean bl2 = j == list.size() - 1;
            this.trimmedMessages.addFirst(new GuiMessage.Line(guiMessage.addedTime(), formattedCharSequence, guiMessage.tag(), bl2));
        }

        while(this.trimmedMessages.size() > 100) {
            this.trimmedMessages.removeLast();
        }

    }

    private void addMessageToQueue(GuiMessage guiMessage) {
        this.allMessages.addFirst(guiMessage);

        while(this.allMessages.size() > 100) {
            this.allMessages.removeLast();
        }

    }

    private void processMessageDeletionQueue() {
        int i = this.minecraft.gui.getGuiTicks();
        this.messageDeletionQueue.removeIf((delayedMessageDeletion) -> {
            if (i >= delayedMessageDeletion.deletableAfter()) {
                return this.deleteMessageOrDelay(delayedMessageDeletion.signature()) == null;
            } else {
                return false;
            }
        });
    }

    public void deleteMessage(MessageSignature messageSignature) {
        DelayedMessageDeletion delayedMessageDeletion = this.deleteMessageOrDelay(messageSignature);
        if (delayedMessageDeletion != null) {
            this.messageDeletionQueue.add(delayedMessageDeletion);
        }

    }

    private @Nullable DelayedMessageDeletion deleteMessageOrDelay(MessageSignature messageSignature) {
        int i = this.minecraft.gui.getGuiTicks();
        ListIterator<GuiMessage> listIterator = this.allMessages.listIterator();

        while(listIterator.hasNext()) {
            GuiMessage guiMessage = (GuiMessage)listIterator.next();
            if (messageSignature.equals(guiMessage.signature())) {
                int j = guiMessage.addedTime() + 60;
                if (i >= j) {
                    listIterator.set(this.createDeletedMarker(guiMessage));
                    this.refreshTrimmedMessages();
                    return null;
                }

                return new DelayedMessageDeletion(messageSignature, j);
            }
        }

        return null;
    }

    private GuiMessage createDeletedMarker(GuiMessage guiMessage) {
        return new GuiMessage(guiMessage.addedTime(), DELETED_CHAT_MESSAGE, (MessageSignature)null, GuiMessageTag.system());
    }

    public void rescaleChat() {
        this.resetChatScroll();
        this.refreshTrimmedMessages();
    }

    private void refreshTrimmedMessages() {
        this.trimmedMessages.clear();

        for(GuiMessage guiMessage : Lists.reverse(this.allMessages)) {
            this.addMessageToDisplayQueue(guiMessage);
        }

    }

    public ArrayListDeque<String> getRecentChat() {
        return this.recentChat;
    }

    public void addRecentChat(String string) {
        if (!string.equals(this.recentChat.peekLast())) {
            if (this.recentChat.size() >= 100) {
                this.recentChat.removeFirst();
            }

            this.recentChat.addLast(string);
        }

        if (string.startsWith("/")) {
            this.minecraft.commandHistory().addCommand(string);
        }

    }

    public void resetChatScroll() {
        this.chatScrollbarPos = 0;
        this.newMessageSinceScroll = false;
    }

    public void scrollChat(int i) {
        this.chatScrollbarPos += i;
        int j = this.trimmedMessages.size();
        if (this.chatScrollbarPos > j - this.getLinesPerPage()) {
            this.chatScrollbarPos = j - this.getLinesPerPage();
        }

        if (this.chatScrollbarPos <= 0) {
            this.chatScrollbarPos = 0;
            this.newMessageSinceScroll = false;
        }

    }

    public boolean isChatFocused() {
        return this.minecraft.screen instanceof ChatScreen;
    }

    private int getWidth() {
        return getWidth((Double)this.minecraft.options.chatWidth().get());
    }

    private int getHeight() {
        return getHeight(this.isChatFocused() ? (Double)this.minecraft.options.chatHeightFocused().get() : (Double)this.minecraft.options.chatHeightUnfocused().get());
    }

    private double getScale() {
        return (Double)this.minecraft.options.chatScale().get();
    }

    public static int getWidth(double d) {
        int i = 320;
        int j = 40;
        return Mth.floor(d * (double)280.0F + (double)40.0F);
    }

    public static int getHeight(double d) {
        int i = 180;
        int j = 20;
        return Mth.floor(d * (double)160.0F + (double)20.0F);
    }

    public static double defaultUnfocusedPct() {
        int i = 180;
        int j = 20;
        return (double)70.0F / (double)(getHeight((double)1.0F) - 20);
    }

    public int getLinesPerPage() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        Objects.requireNonNull(this.minecraft.font);
        return (int)((double)9.0F * ((Double)this.minecraft.options.chatLineSpacing().get() + (double)1.0F));
    }

    public void saveAsDraft(String string) {
        boolean bl = string.startsWith("/");
        this.latestDraft = new Draft(string, bl ? ChatMethod.COMMAND : ChatMethod.MESSAGE);
    }

    public void discardDraft() {
        this.latestDraft = null;
    }

    public <T extends ChatScreen> T createScreen(ChatMethod chatMethod, ChatScreen.ChatConstructor<T> chatConstructor) {
        return (T)(this.latestDraft != null && chatMethod.isDraftRestorable(this.latestDraft) ? chatConstructor.create(this.latestDraft.text(), true) : chatConstructor.create(chatMethod.prefix(), false));
    }

    public void openScreen(ChatMethod chatMethod, ChatScreen.ChatConstructor<?> chatConstructor) {
        this.minecraft.setScreen(this.createScreen(chatMethod, chatConstructor));
    }

    public void preserveCurrentChatScreen() {
        Screen var2 = this.minecraft.screen;
        if (var2 instanceof ChatScreen chatScreen) {
            this.preservedScreen = chatScreen;
        }

    }

    public @Nullable ChatScreen restoreChatScreen() {
        ChatScreen chatScreen = this.preservedScreen;
        this.preservedScreen = null;
        return chatScreen;
    }

    public State storeState() {
        return new State(List.copyOf(this.allMessages), List.copyOf(this.recentChat), List.copyOf(this.messageDeletionQueue));
    }

    public void restoreState(State state) {
        this.recentChat.clear();
        this.recentChat.addAll(state.history);
        this.messageDeletionQueue.clear();
        this.messageDeletionQueue.addAll(state.delayedMessageDeletions);
        this.allMessages.clear();
        this.allMessages.addAll(state.messages);
        this.refreshTrimmedMessages();
    }

    static {
        DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});
        QUEUE_EXPAND_ID = Identifier.withDefaultNamespace("internal/expand_chat_queue");
        QUEUE_EXPAND_TEXT_STYLE = Style.EMPTY.withClickEvent(new ClickEvent.Custom(QUEUE_EXPAND_ID, Optional.empty())).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.queue.tooltip")));
    }

    @Environment(EnvType.CLIENT)
    static record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
    }

    @Environment(EnvType.CLIENT)
    public static class State {
        final List<GuiMessage> messages;
        final List<String> history;
        final List<DelayedMessageDeletion> delayedMessageDeletions;

        public State(List<GuiMessage> list, List<String> list2, List<DelayedMessageDeletion> list3) {
            this.messages = list;
            this.history = list2;
            this.delayedMessageDeletions = list3;
        }
    }



    public enum ChatMethod {
        MESSAGE("") {
            public boolean isDraftRestorable(Draft draft) {
                return true;
            }
        },
        COMMAND("/") {
            public boolean isDraftRestorable(Draft draft) {
                return this == draft.chatMethod;
            }
        };

        private final String prefix;

        ChatMethod(final String string2) {
            this.prefix = string2;
        }

        public String prefix() {
            return this.prefix;
        }

        public abstract boolean isDraftRestorable(Draft draft);
    }

    public record Draft(String text, ChatMethod chatMethod) { }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface AlphaCalculator {
        AlphaCalculator FULLY_VISIBLE = (line) -> 1.0F;

        static AlphaCalculator timeBased(int i) {
            return (line) -> {
                int j = i - line.addedTime();
                double d = (double)j / (double)200.0F;
                d = (double)1.0F - d;
                d *= (double)10.0F;
                d = Mth.clamp(d, (double)0.0F, (double)1.0F);
                d *= d;
                return (float)d;
            };
        }

        float calculate(GuiMessage.Line line);
    }

    @Environment(EnvType.CLIENT)
    static class DrawingBackgroundGraphicsAccess implements ChatGraphicsAccess {
        private final GuiGraphics graphics;
        private final ActiveTextCollector textRenderer;
        private ActiveTextCollector.Parameters parameters;

        public DrawingBackgroundGraphicsAccess(GuiGraphics guiGraphics) {
            this.graphics = guiGraphics;
            this.textRenderer = guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE, (Consumer)null);
            this.parameters = this.textRenderer.defaultParameters();
        }

        public void updatePose(Consumer<Matrix3x2f> consumer) {
            consumer.accept(this.graphics.pose());
            this.parameters = this.parameters.withPose(new Matrix3x2f(this.graphics.pose()));
        }

        public void fill(int i, int j, int k, int l, int m) {
            this.graphics.fill(i, j, k, l, m);
        }

        public boolean handleMessage(int i, float f, FormattedCharSequence formattedCharSequence) {
            this.textRenderer.accept(TextAlignment.LEFT, 0, i, this.parameters.withOpacity(f), formattedCharSequence);
            return false;
        }

        public void handleTag(int i, int j, int k, int l, float f, GuiMessageTag guiMessageTag) {
            int m = ARGB.color(f, guiMessageTag.indicatorColor());
            this.graphics.fill(i, j, k, l, m);
        }

        public void handleTagIcon(int i, int j, boolean bl, GuiMessageTag guiMessageTag, GuiMessageTag.Icon icon) {
        }
    }

    @Environment(EnvType.CLIENT)
    static class DrawingFocusedGraphicsAccess implements ChatGraphicsAccess, Consumer<Style> {
        private final GuiGraphics graphics;
        private final Font font;
        private final ActiveTextCollector textRenderer;
        private ActiveTextCollector.Parameters parameters;
        private final int globalMouseX;
        private final int globalMouseY;
        private final Vector2f localMousePos = new Vector2f();
        private @Nullable Style hoveredStyle;
        private final boolean changeCursorOnInsertions;

        public DrawingFocusedGraphicsAccess(GuiGraphics guiGraphics, Font font, int i, int j, boolean bl) {
            this.graphics = guiGraphics;
            this.font = font;
            this.textRenderer = guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR, this);
            this.globalMouseX = i;
            this.globalMouseY = j;
            this.changeCursorOnInsertions = bl;
            this.parameters = this.textRenderer.defaultParameters();
            this.updateLocalMousePos();
        }

        private void updateLocalMousePos() {
            this.graphics.pose().invert(new Matrix3x2f()).transformPosition((float)this.globalMouseX, (float)this.globalMouseY, this.localMousePos);
        }

        public void updatePose(Consumer<Matrix3x2f> consumer) {
            consumer.accept(this.graphics.pose());
            this.parameters = this.parameters.withPose(new Matrix3x2f(this.graphics.pose()));
            this.updateLocalMousePos();
        }

        public void fill(int i, int j, int k, int l, int m) {
            this.graphics.fill(i, j, k, l, m);
        }

        public void accept(Style style) {
            this.hoveredStyle = style;
        }

        public boolean handleMessage(int i, float f, FormattedCharSequence formattedCharSequence) {
            this.hoveredStyle = null;
            this.textRenderer.accept(TextAlignment.LEFT, 0, i, this.parameters.withOpacity(f), formattedCharSequence);
            if (this.changeCursorOnInsertions && this.hoveredStyle != null && this.hoveredStyle.getInsertion() != null) {
                this.graphics.requestCursor(CursorTypes.POINTING_HAND);
            }

            return this.hoveredStyle != null;
        }

        private boolean isMouseOver(int i, int j, int k, int l) {
            return ActiveTextCollector.isPointInRectangle(this.localMousePos.x, this.localMousePos.y, (float)i, (float)j, (float)k, (float)l);
        }

        public void handleTag(int i, int j, int k, int l, float f, GuiMessageTag guiMessageTag) {
            int m = ARGB.color(f, guiMessageTag.indicatorColor());
            this.graphics.fill(i, j, k, l, m);
            if (this.isMouseOver(i, j, k, l)) {
                this.showTooltip(guiMessageTag);
            }

        }

        public void handleTagIcon(int i, int j, boolean bl, GuiMessageTag guiMessageTag, GuiMessageTag.Icon icon) {
            int k = j - icon.height - 1;
            int l = i + icon.width;
            boolean bl2 = this.isMouseOver(i, k, l, j);
            if (bl2) {
                this.showTooltip(guiMessageTag);
            }

            if (bl || bl2) {
                icon.draw(this.graphics, i, k);
            }

        }

        private void showTooltip(GuiMessageTag guiMessageTag) {
            if (guiMessageTag.text() != null) {
                this.graphics.setTooltipForNextFrame(this.font, this.font.split(guiMessageTag.text(), 210), this.globalMouseX, this.globalMouseY);
            }

        }
    }

    @Environment(EnvType.CLIENT)
    static class ClickableTextOnlyGraphicsAccess implements ChatGraphicsAccess {
        private final ActiveTextCollector output;

        public ClickableTextOnlyGraphicsAccess(ActiveTextCollector activeTextCollector) {
            this.output = activeTextCollector;
        }

        public void updatePose(Consumer<Matrix3x2f> consumer) {
            ActiveTextCollector.Parameters parameters = this.output.defaultParameters();
            Matrix3x2f matrix3x2f = new Matrix3x2f(parameters.pose());
            consumer.accept(matrix3x2f);
            this.output.defaultParameters(parameters.withPose(matrix3x2f));
        }

        public void fill(int i, int j, int k, int l, int m) {
        }

        public boolean handleMessage(int i, float f, FormattedCharSequence formattedCharSequence) {
            this.output.accept(TextAlignment.LEFT, 0, i, formattedCharSequence);
            return false;
        }

        public void handleTag(int i, int j, int k, int l, float f, GuiMessageTag guiMessageTag) {
        }

        public void handleTagIcon(int i, int j, boolean bl, GuiMessageTag guiMessageTag, GuiMessageTag.Icon icon) {
        }
    }

    @Environment(EnvType.CLIENT)
    public interface ChatGraphicsAccess {
        void updatePose(Consumer<Matrix3x2f> consumer);

        void fill(int i, int j, int k, int l, int m);

        boolean handleMessage(int i, float f, FormattedCharSequence formattedCharSequence);

        void handleTag(int i, int j, int k, int l, float f, GuiMessageTag guiMessageTag);

        void handleTagIcon(int i, int j, boolean bl, GuiMessageTag guiMessageTag, GuiMessageTag.Icon icon);
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    interface LineConsumer {
        void accept(GuiMessage.Line line, int i, float f);
    }
}
