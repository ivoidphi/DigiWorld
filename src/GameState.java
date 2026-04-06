import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Player profile: name, chosen party (3 Mecha Beasts), and story progress.
 * Story advances on portal travel ({@link #onPortalTravel(int, int)}) and boss flags.
 */
public final class GameState {

    private String playerName = "Champion";
    private final List<MechaBeastId> partyIds = new ArrayList<>();
    private StoryStage stage = StoryStage.LAB_INTRO;
    private boolean tutorialBattleComplete;
    private boolean beastPickComplete;

    private boolean aldrichDefeated;
    private boolean jazzDefeated;
    private boolean trialmasterDefeated;
    private boolean glitchDefeated;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName != null && !playerName.isBlank()) {
            this.playerName = playerName.trim();
        }
    }

    public List<MechaBeastId> getPartyIds() {
        return Collections.unmodifiableList(partyIds);
    }

    public void clearParty() {
        partyIds.clear();
    }

    public boolean addPartyMember(MechaBeastId id) {
        if (partyIds.size() >= 3) return false;
        if (partyIds.contains(id)) return false;
        if (id == MechaBeastId.WOLTRIX) return false;
        partyIds.add(id);
        return true;
    }

    public boolean removePartyMember(MechaBeastId id) {
        return partyIds.remove(id);
    }

    public boolean isPartyFull() {
        return partyIds.size() >= 3;
    }

    public StoryStage getStage() {
        return stage;
    }

    public void setStage(StoryStage stage) {
        this.stage = stage;
    }

    public boolean isTutorialBattleComplete() {
        return tutorialBattleComplete;
    }

    public void setTutorialBattleComplete(boolean tutorialBattleComplete) {
        this.tutorialBattleComplete = tutorialBattleComplete;
    }

    public boolean isBeastPickComplete() {
        return beastPickComplete;
    }

    public void setBeastPickComplete(boolean beastPickComplete) {
        this.beastPickComplete = beastPickComplete;
    }

    public boolean isAldrichDefeated() {
        return aldrichDefeated;
    }

    public void setAldrichDefeated(boolean aldrichDefeated) {
        this.aldrichDefeated = aldrichDefeated;
    }

    public boolean isJazzDefeated() {
        return jazzDefeated;
    }

    public void setJazzDefeated(boolean jazzDefeated) {
        this.jazzDefeated = jazzDefeated;
    }

    public boolean isTrialmasterDefeated() {
        return trialmasterDefeated;
    }

    public void setTrialmasterDefeated(boolean trialmasterDefeated) {
        this.trialmasterDefeated = trialmasterDefeated;
    }

    public boolean isGlitchDefeated() {
        return glitchDefeated;
    }

    public void setGlitchDefeated(boolean glitchDefeated) {
        this.glitchDefeated = glitchDefeated;
    }

    /**
     * World graph: Alpha(0)→Beta(1)→Mystic(2)→House(3)→Alpha.
     * Mystic→House: need forest tutorial battle done.
     * Later: optional Alpha Beast (Aldrich) gate — see {@link #setAldrichDefeated(boolean)}.
     */
    public boolean canUsePortal(int fromWorld, int toWorld) {
        if (fromWorld == World.MYSTIC_FOREST && toWorld == World.HOUSE) {
            return tutorialBattleComplete;
        }
        if (fromWorld == World.BETA_CITY && toWorld == World.MYSTIC_FOREST
                && stage == StoryStage.BOSS_JAZZ && !jazzDefeated) {
            return false;
        }
        return true;
    }

    /**
     * Called once after a portal transition completes (after the new map is loaded).
     */
    public void onPortalTravel(int fromWorld, int toWorld) {
        if (fromWorld == World.BETA_CITY && toWorld == World.MYSTIC_FOREST && stage == StoryStage.ALPHA_VILLAGE) {
            stage = StoryStage.MYSTIC_TUTORIAL;
            return;
        }
        if (fromWorld == World.ALPHA_VILLAGE && toWorld == World.BETA_CITY
                && stage == StoryStage.MYSTIC_TUTORIAL && tutorialBattleComplete) {
            stage = StoryStage.BETA_CITY;
        }
        if (fromWorld == World.ALPHA_VILLAGE && toWorld == World.BETA_CITY
                && stage == StoryStage.BOSS_ALDRICH && aldrichDefeated) {
            stage = StoryStage.BETA_CITY;
        }
    }

    /** Story label for HUD. */
    public String getStageLabel() {
        return switch (stage) {
            case LAB_INTRO -> "Prologue";
            case ALPHA_VILLAGE -> "Stage 1 — Alpha Village";
            case MYSTIC_TUTORIAL -> "Mystic Forest";
            case BOSS_ALDRICH -> "Alpha Beast";
            case BETA_CITY -> "Stage 2 — Beta City";
            case BOSS_JAZZ -> "Ace Trainer";
            case TRIALMASTER -> "Tournament Trial";
            case COLLAPSE -> "Collapse";
            case BOSS_GLITCH -> "Glitch";
            case EPILOGUE -> "Epilogue";
        };
    }

    /** Level 5 party for early game; adjust per stage later. */
    public List<BattleCreature> buildPartyAtLevel(int level) {
        List<BattleCreature> list = new ArrayList<>();
        for (MechaBeastId id : partyIds) {
            list.add(BattleCreature.fromId(id, level));
        }
        return list;
    }
}
