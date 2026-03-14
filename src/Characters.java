import java.util.ArrayList;
import java.util.List;

public class Characters {

    public static List<NPC> loadAll(GamePanel gp) {
        List<NPC> npcs = new ArrayList<>();

        npcs.add(new NPC(gp, "Prof. Alfred", 9, 2, "res/player/chief-rei.png",
                World.ALPHA_VILLAGE,
                new String[]{
                        "Welcome, traveler. I am Professor Alfred, guardian of this village.",
                        "You seek the Alpha Beast? Then follow the Mystic Forest.",
                        "The path will test you before you reach the Alpha. Be prepared."
                }
        ));

        npcs.add(new NPC(gp, "Chief Rei", 10, 5, "res/player/chief-rei.png",
                World.HOUSE,
                new String[]{
                        "Welcome, home b*tch",
                }
        ));

        npcs.add(new  NPC(gp, "Utin", 10, 2, "res/player/chief-rei.png", World.BETA_CITY,
                new String[]{
                    "Hi bietch!",
                }));
        return npcs;
    }
}