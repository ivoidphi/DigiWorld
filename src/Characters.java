import java.util.ArrayList;
import java.util.List;

public class Characters {

    /**
     * Load NPCs for a specific world index.
     * Uses Branch 2's worldIndex field on NPC for filtering,
     * and Branch 1's per-world loading pattern.
     */
    public static List<NPC> loadAll(GamePanel gp, int worldIndex) {
        List<NPC> npcs = new ArrayList<>();

        switch (worldIndex) {

            case World.ALPHA_VILLAGE -> {
                npcs.add(new NPC(gp, "Professor Alfred", 5, 4, "res/player/chief-rei.png",
                        World.ALPHA_VILLAGE,
                        new String[]{
                                "Welcome to the beta test. Your first mission is to reach Alpha Village and challenge the Alpha Beast.",
                                "Your G-Watch has the map — follow the route. Since this is still in beta, you might encounter bugs.",
                                "One more thing: you will feel pain, just like in real life. But remember — you will not die.",
                                "Good luck. The future of gaming and your legacy are in your hands."
                        }
                ));
            }

            case World.BETA_CITY -> {
                npcs.add(new NPC(gp, "Utin", 5, 4, "res/player/chief-rei.png",
                        World.BETA_CITY,
                        new String[]{
                                "Beta City — full test ahead. Defeat the Ace Trainer for a Challenge Ticket, then head to the Tournament Hall."
                        }
                ));
            }

            case World.MYSTIC_FOREST -> {
                npcs.add(new NPC(gp, "Challenger", 5, 3, "res/player/chief-rei.png",
                        World.MYSTIC_FOREST,
                        new String[]{
                                "Hi bietch!"
                        }
                ));
            }

            case World.HOUSE_1 -> {
                npcs.add(new NPC(gp, "Chief Rei", 5, 4, "res/player/chief-rei.png",
                        World.HOUSE_1,
                        new String[]{
                                "Welcome, traveler. I am Chief Rei, guardian of this village. What is it you seek?",
                                "The Alpha Beast? Then follow the Mystic Forest. The path will test you before you reach the Alpha. Be prepared."
                        }
                ));
            }
        }

        return npcs;
    }
}