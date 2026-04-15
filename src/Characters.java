import java.util.ArrayList;
import java.util.List;

public class Characters {

    public static List<NPC> loadAll(GamePanel gp) {
        List<NPC> npcs = new ArrayList<>();

        npcs.add(new NPC(gp, "Professor Alfred", 9, 2, "res/player/chief-rei.png",
                World.ALPHA_VILLAGE,
                new String[]{
                        "Welcome to the beta test. Your first mission is to reach Alpha Village and challenge the Alpha Beast.",
                        "Your G-Watch has the map — follow the route. Since this is still in beta, you might encounter bugs.",
                        "One more thing: you will feel pain, just like in real life. But remember — you will not die.",
                        "Good luck. The future of gaming and your legacy are in your hands."
                }
        ));

        npcs.add(new NPC(gp, "Chief Rei", 10, 5, "res/player/chief-rei.png",
                World.HOUSE,
                new String[]{
                        "Welcome, traveler. I am Chief Rei, guardian of this village. What is it you seek?",
                        "The Alpha Beast? Then follow the Mystic Forest. The path will test you before you reach the Alpha. Be prepared."
                }
        ));

        npcs.add(new NPC(gp, "Utin", 10, 2, "res/player/chief-rei.png", World.BETA_CITY,
                new String[]{
                        "Beta City — full test ahead. Defeat the Ace Trainer for a Challenge Ticket, then head to the Tournament Hall."
                }));
        return npcs;
    }
}
