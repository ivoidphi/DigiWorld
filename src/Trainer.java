import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Trainer owns a party; active slot is the creature currently fighting. */
public class Trainer {

    private final String name;
    private final List<BattleCreature> party;
    private int activeIndex;

    public Trainer(String name, List<BattleCreature> party) {
        if (party == null || party.isEmpty()) {
            throw new IllegalArgumentException("Party must contain at least one creature.");
        }
        this.name = name;
        this.party = new ArrayList<>(party);
        this.activeIndex = 0;
    }

    public String getName() {
        return name;
    }

    public BattleCreature getActiveCreature() {
        return party.get(activeIndex);
    }

    public List<BattleCreature> getParty() {
        return Collections.unmodifiableList(party);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int index) {
        if (index >= 0 && index < party.size() && !party.get(index).isFainted()) {
            activeIndex = index;
        }
    }

    public boolean switchTo(int index) {
        if (index < 0 || index >= party.size()) return false;
        if (party.get(index).isFainted()) return false;
        activeIndex = index;
        return true;
    }

    public boolean allFainted() {
        for (BattleCreature c : party) {
            if (!c.isFainted()) return false;
        }
        return true;
    }
}
