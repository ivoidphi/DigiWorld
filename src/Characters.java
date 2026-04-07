import java.util.ArrayList;
import java.util.List;

public class Characters {

    public static List<NPC> loadAll(GamePanel gp, int worldIndex) {
        List<NPC> npcs = new ArrayList<>();

        switch (worldIndex) {

            case 0 -> { // Alpha Village
                npcs.add(new NPC(gp, "Prof. Alfred", 5, 3, "res/player/chief-rei.png",
                        new String[]{
                                "Welcome, traveler. I am Professor Alfred, guardian of this village.",
                                "You seek the Alpha Beast? Then follow the Mystic Forest.",
                                "The path will test you before you reach the Alpha. Be prepared."
                        }
                ));
            }

            case 1 -> { // Beta City
                npcs.add(new NPC(gp, "Chief Rei", 5, 3, "res/player/chief-rei.png",
                        new String[]{
                                "Welcome hooman",
                        }
                ));
            }

            case 2 -> { // Mystic Forest
                npcs.add(new NPC(gp, "Challenger", 5, 3, "res/player/chief-rei.png",
                        new String[]{
                                "Hi bietch!",
                        }
                ));
            }
        }

        return npcs;
    }
}