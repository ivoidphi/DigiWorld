import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Turn-based Mecha Beast battle: Fight / Catch / Switch / Run, energy, cooldowns, skills.
 */
public class BattleSequence {

    private static final int INTRO_FRAMES = 60;

    private final GamePanel gp;
    private final GameState gameState;
    private final BattleEngine engine = new BattleEngine();

    private boolean active;
    private Trainer playerTrainer;
    private BattleCreature enemy;
    private boolean trainerBattle;
    private boolean allowRun;
    private boolean tutorialHenshin;
    private boolean henshinDone;
    private int expReward;

    private BattlePhase phase = BattlePhase.INTRO;
    private int introTimer;
    private int mainMenuIndex;
    private int skillCursor;
    private int switchCursor;

    private final Deque<String> messageQueue = new ArrayDeque<>();
    private String displayedLine = "";
    private Runnable pendingWhenQueueEmpty;

    public BattleSequence(GamePanel gp, GameState gameState) {
        this.gp = gp;
        this.gameState = gameState;
    }

    public boolean isActive() {
        return active;
    }

    /** Wild encounter — can catch and run (unless blocked by story later). */
    public void startWildBattle(BattleCreature wild, int expReward, boolean tutorialHenshin) {
        if (active) return;
        List<BattleCreature> party = gameState.buildPartyAtLevel(5);
        if (party.isEmpty()) {
            party = List.of(BattleCreature.fromId(MechaBeastId.VINERATOPS, 5));
        }
        playerTrainer = new Trainer(gameState.getPlayerName(), party);
        initBattle(wild, false, true, expReward, tutorialHenshin);
    }

    /** Trainer battle — no catch, no run. */
    public void startTrainerBattle(BattleCreature opponent, int expReward) {
        if (active) return;
        List<BattleCreature> party = gameState.buildPartyAtLevel(5);
        if (party.isEmpty()) {
            party = List.of(BattleCreature.fromId(MechaBeastId.KYOFLARE, 5));
        }
        playerTrainer = new Trainer(gameState.getPlayerName(), party);
        initBattle(opponent, true, false, expReward, false);
    }

    private void initBattle(BattleCreature opp, boolean trainer, boolean runAllowed, int xp, boolean tutHenshin) {
        active = true;
        enemy = opp;
        trainerBattle = trainer;
        allowRun = runAllowed;
        tutorialHenshin = tutHenshin;
        henshinDone = !tutHenshin;
        expReward = xp;

        phase = BattlePhase.INTRO;
        introTimer = INTRO_FRAMES;
        mainMenuIndex = 0;
        skillCursor = 0;
        switchCursor = 0;
        messageQueue.clear();
        displayedLine = "";
        pendingWhenQueueEmpty = null;

        enemy.healFully();
        for (BattleCreature c : playerTrainer.getParty()) {
            c.healFully();
        }

        if (trainer) {
            enqueueLine("Opposing " + enemy.getNickname() + " appeared!");
        } else {
            enqueueLine("Wild " + enemy.getNickname() + " appeared!");
        }
    }

    public void endBattle() {
        active = false;
        messageQueue.clear();
        displayedLine = "";
        pendingWhenQueueEmpty = null;
    }

    public void update(KeyHandler key) {
        if (!active) return;

        switch (phase) {
            case INTRO -> updateIntro(key);
            case HENSHIN_WAIT -> updateHenshin(key);
            case MAIN_COMMAND -> updateMainCommand(key);
            case SKILL_SELECT -> updateSkillSelect(key);
            case SWITCH_SELECT -> updateSwitchSelect(key);
            case MESSAGE -> updateMessage(key);
            case PLAYER_WIN, PLAYER_LOSS, RAN_AWAY, CAUGHT -> updateTerminal(key);
        }
    }

    private void updateIntro(KeyHandler key) {
        introTimer--;
        if (introTimer <= 0 || key.enterPressed) {
            if (tutorialHenshin && !henshinDone) {
                phase = BattlePhase.HENSHIN_WAIT;
                displayedLine = "";
                messageQueue.clear();
            } else {
                henshinDone = true;
                phase = BattlePhase.MAIN_COMMAND;
                displayedLine = "";
                messageQueue.clear();
            }
        }
    }

    private void updateHenshin(KeyHandler key) {
        if (key.n1Pressed) {
            henshinDone = true;
            beginMessagePhase();
            BattleCreature p = playerTrainer.getActiveCreature();
            enqueueLine("BEAST CARD ON! HENSHIN!");
            enqueueLine(p.getHenshinAnnouncement());
            pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
        }
    }

    private void updateMainCommand(KeyHandler key) {
        if (key.navUpPressed) mainMenuIndex = (mainMenuIndex + 3) % 4;
        if (key.navDownPressed) mainMenuIndex = (mainMenuIndex + 1) % 4;

        if (!key.enterPressed) return;

        switch (mainMenuIndex) {
            case 0 -> phase = BattlePhase.SKILL_SELECT;
            case 1 -> tryCatch();
            case 2 -> {
                if (playerTrainer.getParty().size() > 1) {
                    switchCursor = 0;
                    phase = BattlePhase.SWITCH_SELECT;
                } else {
                    beginMessagePhase();
                    enqueueLine("No other Mecha Beast to switch to!");
                    pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
                }
            }
            case 3 -> tryRun();
        }
    }

    private void tryRun() {
        if (!allowRun) {
            beginMessagePhase();
            enqueueLine("You can't run from a trainer battle!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
            return;
        }
        BattleCreature p = playerTrainer.getActiveCreature();
        if (engine.attemptWildRun(p, enemy)) {
            beginMessagePhase();
            enqueueLine("Got away safely!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.RAN_AWAY;
        } else {
            beginMessagePhase();
            enqueueLine("Can't escape!");
            pendingWhenQueueEmpty = this::enemyTurn;
        }
    }

    private void tryCatch() {
        if (trainerBattle) {
            beginMessagePhase();
            enqueueLine("You can't catch another trainer's beast!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
            return;
        }
        if (enemy.isFainted()) {
            beginMessagePhase();
            enqueueLine("There is nothing to catch!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
            return;
        }
        beginMessagePhase();
        enqueueLine("You used a Beast Card!");
        if (engine.attemptCatch(enemy)) {
            enqueueLine(enemy.getNickname() + " was caught!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.CAUGHT;
        } else {
            enqueueLine("Oh no! The beast broke free!");
            pendingWhenQueueEmpty = this::enemyTurn;
        }
    }

    private void updateSkillSelect(KeyHandler key) {
        if (key.escapePressed) {
            phase = BattlePhase.MAIN_COMMAND;
            return;
        }
        if (key.navUpPressed) skillCursor = (skillCursor + 2) % 3;
        if (key.navDownPressed) skillCursor = (skillCursor + 1) % 3;
        if (key.n1Pressed) skillCursor = 0;
        if (key.n2Pressed) skillCursor = 1;
        if (key.n3Pressed) skillCursor = 2;

        if (key.enterPressed) {
            usePlayerSkill(skillCursor);
        }
    }

    private void updateSwitchSelect(KeyHandler key) {
        List<BattleCreature> party = playerTrainer.getParty();
        if (key.escapePressed) {
            phase = BattlePhase.MAIN_COMMAND;
            return;
        }
        if (key.navUpPressed) switchCursor = (switchCursor + party.size() - 1) % party.size();
        if (key.navDownPressed) switchCursor = (switchCursor + 1) % party.size();

        if (key.enterPressed) {
            if (party.get(switchCursor).isFainted()) {
                return;
            }
            playerTrainer.setActiveIndex(switchCursor);
            beginMessagePhase();
            enqueueLine("Go! " + playerTrainer.getActiveCreature().getNickname() + "!");
            pendingWhenQueueEmpty = this::enemyTurn;
        }
    }

    private void usePlayerSkill(int index) {
        BattleCreature p = playerTrainer.getActiveCreature();
        if (!p.canUseSkill(index)) {
            beginMessagePhase();
            enqueueLine("That skill can't be used!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.SKILL_SELECT;
            return;
        }

        if (p.status.paralyzeTurns > 0 && engine.getRandom().nextDouble() < 0.5) {
            beginMessagePhase();
            enqueueLine(p.getNickname() + " is paralyzed! It can't move!");
            pendingWhenQueueEmpty = this::enemyTurn;
            return;
        }

        BattleSkillDefinition sk = p.getSkill(index);
        p.paySkillCost(index);
        beginMessagePhase();
        enqueueLine(p.getNickname() + " used " + sk.name + "!");

        applyHits(p, enemy, sk);

        if (enemy.isFainted()) {
            enqueueLine(enemy.getNickname() + " was defeated!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.PLAYER_WIN;
            return;
        }

        applySkillSideEffects(p, enemy, sk);
        pendingWhenQueueEmpty = this::enemyTurn;
    }

    private void applyHits(BattleCreature attacker, BattleCreature target, BattleSkillDefinition sk) {
        double crit = engine.critChanceForSkill(sk);
        Random r = engine.getRandom();

        switch (sk.special) {
            case TWIN_STRIKE_50 -> {
                dealOneHit(attacker, target, sk, 1.0, crit, true);
                if (!target.isFainted()) dealOneHit(attacker, target, sk, 0.5, crit, false);
            }
            case MULTI_EXTRA_3_20 -> {
                dealOneHit(attacker, target, sk, 1.0, crit, true);
                for (int i = 0; i < 3 && !target.isFainted(); i++) {
                    dealOneHit(attacker, target, sk, 0.20, crit, false);
                }
            }
            case THUNDER_BARRAGE -> {
                for (int i = 0; i < 5 && !target.isFainted(); i++) {
                    double m = i == 0 ? 1.0 : 0.30;
                    dealOneHit(attacker, target, sk, m, crit, i == 0);
                }
            }
            case DOUBLE_STRIKE_HALF -> {
                dealOneHit(attacker, target, sk, 1.0, crit, true);
                if (!target.isFainted() && r.nextBoolean()) {
                    dealOneHit(attacker, target, sk, 0.5, crit, false);
                }
            }
            default -> dealOneHit(attacker, target, sk, 1.0, crit, true);
        }

        if (target.isFainted()) return;
        if (sk.special == SkillSpecial.AFTERSHOCK_30) {
            int ex = engine.aftershockDamage(target, false);
            if (ex > 0) {
                target.applyDamage(ex);
                enqueueLine("Aftershock hit!");
            }
        } else if (sk.special == SkillSpecial.AFTERSHOCK_100) {
            int ex = engine.aftershockDamageGuaranteed(target);
            target.applyDamage(ex);
            enqueueLine("Aftershock!");
        }
    }

    private void dealOneHit(BattleCreature atk, BattleCreature def, BattleSkillDefinition sk, double pMult,
                           double critChance, boolean showEffectiveness) {
        if (def.isFainted()) return;
        int dmg = engine.computeDamage(atk, def, sk, pMult, critChance);
        def.applyDamage(dmg);
        if (showEffectiveness) {
            double eff = engine.typeMultiplier(sk.element, def);
            if (eff > 1.0) enqueueLine("It's super effective!");
            else if (eff < 1.0 && eff > 0) enqueueLine("It's not very effective...");
        }
    }

    private void applySkillSideEffects(BattleCreature user, BattleCreature foe, BattleSkillDefinition sk) {
        switch (sk.special) {
            case BURN_10_T3 -> {
                if (engine.rollBurn(10)) {
                    foe.status.burnTurns = 3;
                    enqueueLine(foe.getNickname() + " was burned!");
                }
            }
            case BURN_30_T3 -> {
                if (engine.rollBurn(30)) {
                    foe.status.burnTurns = 3;
                    enqueueLine(foe.getNickname() + " was burned!");
                }
            }
            case HEAL_SELF_10 -> user.healPercentOfMax(0.10);
            case HEAL_SELF_15 -> user.healPercentOfMax(0.15);
            case ATK_DOWN_20_T1 -> foe.status.applyAttackDebuff(0.8, 1);
            case DEF_DOWN_20_T1 -> foe.status.applyDefenseDebuff(0.8, 1);
            case ATK_DOWN_50_T1 -> foe.status.applyAttackDebuff(0.5, 1);
            case PARALYZE_30_T2 -> {
                if (engine.rollParalyze(30)) {
                    foe.status.paralyzeTurns = 2;
                    enqueueLine(foe.getNickname() + " was paralyzed!");
                }
            }
            case PARALYZE_40_T2 -> {
                if (engine.rollParalyze(40)) {
                    foe.status.paralyzeTurns = 2;
                    enqueueLine(foe.getNickname() + " was paralyzed!");
                }
            }
            case FEAR_50_T1 -> {
                if (engine.rollFear(50)) {
                    foe.status.fearTurns = 1;
                    enqueueLine(foe.getNickname() + " is stricken with fear!");
                }
            }
            default -> {
            }
        }
    }

    private void enemyTurn() {
        BattleCreature p = playerTrainer.getActiveCreature();
        if (enemy.isFainted()) {
            phase = BattlePhase.PLAYER_WIN;
            return;
        }

        int burn = enemy.status.tickBurn(enemy.getMaxHp());
        if (burn > 0) {
            enemy.applyDamage(burn);
            beginMessagePhase();
            enqueueLine(enemy.getNickname() + " is hurt by its burn!");
            if (enemy.isFainted()) {
                enqueueLine(enemy.getNickname() + " was defeated!");
                pendingWhenQueueEmpty = () -> phase = BattlePhase.PLAYER_WIN;
                return;
            }
            pendingWhenQueueEmpty = () -> continueEnemyTurn(p);
        } else {
            continueEnemyTurn(p);
        }
    }

    private void continueEnemyTurn(BattleCreature p) {
        if (enemy.status.fearTurns > 0) {
            enemy.status.fearTurns--;
            beginMessagePhase();
            enqueueLine(enemy.getNickname() + " is too afraid to attack!");
            pendingWhenQueueEmpty = () -> endRound(p);
            return;
        }
        if (enemy.status.paralyzeTurns > 0 && engine.getRandom().nextDouble() < 0.5) {
            beginMessagePhase();
            enqueueLine(enemy.getNickname() + " is paralyzed! It can't move!");
            pendingWhenQueueEmpty = () -> endRound(p);
            return;
        }

        int si = pickEnemySkill();
        if (si < 0) {
            beginMessagePhase();
            enqueueLine(enemy.getNickname() + " has no usable skills!");
            pendingWhenQueueEmpty = () -> endRound(p);
            return;
        }

        BattleSkillDefinition sk = enemy.getSkill(si);
        enemy.paySkillCost(si);
        beginMessagePhase();
        enqueueLine((trainerBattle ? "Foe " : "Wild ") + enemy.getNickname() + " used " + sk.name + "!");

        applyHits(enemy, p, sk);
        applySkillSideEffects(enemy, p, sk);

        if (p.isFainted()) {
            enqueueLine(p.getNickname() + " was defeated!");
            pendingWhenQueueEmpty = () -> phase = BattlePhase.PLAYER_LOSS;
            return;
        }

        pendingWhenQueueEmpty = () -> endRound(p);
    }

    private int pickEnemySkill() {
        List<Integer> ok = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            if (enemy.canUseSkill(i)) ok.add(i);
        }
        if (ok.isEmpty()) return -1;
        return ok.get(engine.getRandom().nextInt(ok.size()));
    }

    private void endRound(BattleCreature p) {
        p.regenerateEnergy();
        enemy.regenerateEnergy();
        p.tickCooldowns();
        enemy.tickCooldowns();
        p.status.clearTurnDebuffs();
        enemy.status.clearTurnDebuffs();
        p.status.tickParalyzeAndFear();
        enemy.status.tickParalyzeAndFear();

        int pb = p.status.tickBurn(p.getMaxHp());
        if (pb > 0) {
            p.applyDamage(pb);
            beginMessagePhase();
            enqueueLine(p.getNickname() + " is hurt by its burn!");
            if (p.isFainted()) {
                pendingWhenQueueEmpty = () -> phase = BattlePhase.PLAYER_LOSS;
                return;
            }
            pendingWhenQueueEmpty = () -> phase = BattlePhase.MAIN_COMMAND;
        } else {
            phase = BattlePhase.MAIN_COMMAND;
        }
    }

    private void beginMessagePhase() {
        messageQueue.clear();
        displayedLine = "";
        pendingWhenQueueEmpty = null;
        phase = BattlePhase.MESSAGE;
    }

    private void enqueueLine(String line) {
        messageQueue.addLast(line);
        if (displayedLine.isEmpty()) {
            displayedLine = messageQueue.pollFirst();
        }
    }

    private void updateMessage(KeyHandler key) {
        if (!key.enterPressed) return;
        if (!messageQueue.isEmpty()) {
            displayedLine = messageQueue.pollFirst();
        } else {
            displayedLine = "";
            if (pendingWhenQueueEmpty != null) {
                Runnable r = pendingWhenQueueEmpty;
                pendingWhenQueueEmpty = null;
                r.run();
            }
        }
    }

    private void updateTerminal(KeyHandler key) {
        if (!key.enterPressed) return;
        if (phase == BattlePhase.PLAYER_WIN) {
            playerTrainer.getActiveCreature().addExperience(expReward);
            if (tutorialHenshin) {
                gameState.setTutorialBattleComplete(true);
            }
        }
        endBattle();
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = gp.screenWidth;
        int h = gp.screenHeight;

        GradientPaint gpaint = new GradientPaint(0, 0, new Color(18, 22, 40), 0, h, new Color(8, 10, 20));
        g2.setPaint(gpaint);
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(55, 65, 100));
        g2.fillRoundRect(w - 360, 28, 340, 128, 14, 14);
        g2.setColor(new Color(20, 24, 38));
        g2.fillRoundRect(w - 352, 36, 324, 112, 10, 10);
        drawCreature(g2, enemy, w - 336, 44, true, false);

        g2.setColor(new Color(55, 65, 100));
        g2.fillRoundRect(20, h - 308, 340, 148, 14, 14);
        g2.setColor(new Color(20, 24, 38));
        g2.fillRoundRect(28, h - 300, 324, 132, 10, 10);
        drawCreature(g2, playerTrainer.getActiveCreature(), 40, h - 288, false, true);

        int boxY = h - 168;
        int boxPad = 20;
        g2.setColor(new Color(12, 14, 22));
        g2.fillRoundRect(boxPad, boxY, w - boxPad * 2, 150, 12, 12);
        g2.setColor(new Color(30, 36, 52));
        g2.fillRoundRect(boxPad + 4, boxY + 4, w - (boxPad + 4) * 2, 142, 8, 8);
        g2.setColor(new Color(235, 238, 248));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        int textMaxW = w - boxPad * 2 - 32;
        UiText.drawWrapped(g2, displayedLine, boxPad + 16, boxY + 28, textMaxW, 22, 4);

        int mx = 28;
        int my = h - 400;

        switch (phase) {
            case INTRO -> {
                g2.setColor(new Color(180, 190, 210));
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
                g2.drawString("ENTER — continue", boxPad + 16, boxY + 128);
            }
            case HENSHIN_WAIT -> {
                g2.setColor(new Color(180, 190, 210));
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
                g2.drawString("Press 1 — BEAST CARD ON! HENSHIN!", boxPad + 16, boxY + 128);
            }
            case MAIN_COMMAND -> drawMainMenu(g2, mx, my);
            case SKILL_SELECT -> drawSkills(g2, mx, my);
            case SWITCH_SELECT -> drawSwitch(g2, mx, my);
            case MESSAGE -> {
                g2.setColor(new Color(160, 170, 195));
                g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                g2.drawString("ENTER — continue", w - 220, boxY + 132);
            }
            case PLAYER_WIN -> {
                g2.setColor(new Color(200, 220, 255));
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
                g2.drawString("Victory! ENTER  (" + expReward + " EXP)", boxPad + 16, boxY + 120);
            }
            case PLAYER_LOSS -> {
                g2.setColor(new Color(255, 200, 200));
                g2.drawString("Defeat… ENTER", boxPad + 16, boxY + 120);
            }
            case RAN_AWAY, CAUGHT -> {
                g2.setColor(new Color(180, 190, 210));
                g2.drawString("ENTER — close", boxPad + 16, boxY + 120);
            }
        }
    }

    private void drawMainMenu(Graphics2D g2, int x, int y) {
        String[] opts = {"FIGHT", "CATCH", "SWITCH", "RUN"};
        for (int i = 0; i < 4; i++) {
            drawOpt(g2, opts[i], x, y + i * 44, mainMenuIndex == i, 44);
        }
        g2.setColor(new Color(160, 170, 195));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g2.drawString("UP / DOWN   ENTER", x, y + 4 * 44 + 8);
    }

    private void drawSkills(Graphics2D g2, int x, int y) {
        BattleCreature p = playerTrainer.getActiveCreature();
        int rowH = 54;
        for (int i = 0; i < 3; i++) {
            BattleSkillDefinition s = p.getSkill(i);
            int cd = p.getCooldown(i);
            String cdLabel = cd > 0 ? (cd + " turn" + (cd > 1 ? "s" : "")) : "—";
            drawSkillRow(g2, x, y + i * rowH, i + 1, s.name, s.energyCost, s.cooldownTurns, cdLabel, skillCursor == i, rowH);
        }
        g2.setColor(new Color(200, 210, 235));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g2.drawString("Energy: " + p.getCurrentEnergy() + " / " + p.getMaxEnergy(), x, y + 3 * rowH + 14);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2.setColor(new Color(160, 170, 195));
        g2.drawString("ESC back   1 / 2 / 3   ENTER", x, y + 3 * rowH + 32);
    }

    private void drawSkillRow(Graphics2D g2, int x, int y, int num, String name, int energyCost,
                              int baseCd, String cdRemaining, boolean sel, int rowH) {
        int boxW = Math.min(gp.screenWidth - 48, 560);
        if (sel) {
            g2.setColor(new Color(75, 88, 120));
            g2.fillRoundRect(x - 4, y - 4, boxW, rowH - 4, 8, 8);
            g2.setColor(new Color(255, 245, 200));
        } else {
            g2.setColor(new Color(48, 54, 72));
            g2.fillRoundRect(x - 4, y - 4, boxW, rowH - 4, 8, 8);
            g2.setColor(new Color(228, 232, 245));
        }
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g2.drawString(num + ". " + name, x + 8, y + 16);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g2.setColor(sel ? new Color(220, 228, 245) : new Color(185, 192, 210));
        String baseCdStr = baseCd > 0 ? (baseCd + " turns") : "—";
        String detail = "Energy Cost: " + energyCost
                + "   |   Cooldown: " + baseCdStr
                + "   |   CD remaining: " + cdRemaining;
        UiText.drawWrapped(g2, detail, x + 10, y + 34, boxW - 24, 14, 2);
    }

    private void drawSwitch(Graphics2D g2, int x, int y) {
        List<BattleCreature> party = playerTrainer.getParty();
        for (int i = 0; i < party.size(); i++) {
            BattleCreature c = party.get(i);
            String t = c.getNickname() + (c.isFainted() ? " (KO)" : "");
            drawOpt(g2, t, x, y + i * 44, switchCursor == i, 40);
        }
    }

    private void drawCreature(Graphics2D g2, BattleCreature c, int x, int y, boolean right, boolean showEnergy) {
        g2.setColor(new Color(240, 242, 255));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g2.drawString(c.getNickname() + "  Lv." + c.getLevel(), x, y);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g2.setColor(new Color(200, 205, 220));
        g2.drawString(c.getPrimaryType().displayName(), x, y + 18);
        int barW = 260;
        int barH = 11;
        int by = y + 26;
        double ratio = c.getMaxHp() <= 0 ? 0 : (double) c.getCurrentHp() / c.getMaxHp();
        g2.setColor(new Color(35, 38, 48));
        g2.fillRoundRect(x, by, barW, barH, 5, 5);
        g2.setColor(ratio > 0.5 ? new Color(80, 210, 120) : ratio > 0.2 ? new Color(230, 210, 80) : new Color(230, 80, 80));
        g2.fillRoundRect(x, by, (int) (barW * ratio), barH, 5, 5);
        g2.setColor(new Color(230, 232, 245));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        g2.drawString("HP " + c.getCurrentHp() + " / " + c.getMaxHp(), x + 6, by + 9);

        if (showEnergy) {
            int ey = by + 18;
            double er = c.getMaxEnergy() <= 0 ? 0 : (double) c.getCurrentEnergy() / c.getMaxEnergy();
            g2.setColor(new Color(35, 38, 48));
            g2.fillRoundRect(x, ey, barW, barH, 5, 5);
            g2.setColor(new Color(100, 180, 255));
            g2.fillRoundRect(x, ey, (int) (barW * er), barH, 5, 5);
            g2.setColor(new Color(230, 232, 245));
            g2.drawString("Energy " + c.getCurrentEnergy() + " / " + c.getMaxEnergy(), x + 6, ey + 9);
        }
    }

    private void drawOpt(Graphics2D g2, String t, int x, int y, boolean sel, int h) {
        int boxW = Math.min(400, gp.screenWidth - 56);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        if (sel) {
            g2.setColor(new Color(255, 210, 100));
            g2.fillRoundRect(x - 4, y - 18, boxW, h, 8, 8);
            g2.setColor(new Color(15, 18, 28));
        } else {
            g2.setColor(new Color(65, 72, 95));
            g2.fillRoundRect(x - 4, y - 18, boxW, h, 8, 8);
            g2.setColor(new Color(235, 238, 248));
        }
        g2.drawString(t, x + 8, y);
    }
}
