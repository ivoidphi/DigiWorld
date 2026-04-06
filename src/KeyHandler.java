import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean up, down, left, right;
    public boolean interact;
    public boolean interactPressed;

    public boolean enterPressed;
    public boolean escapePressed;
    public boolean battlePressed;
    public boolean n1Pressed, n2Pressed, n3Pressed, n4Pressed;
    public boolean rPressed;

    /** One-shot per key press (menus / beast pick) — avoids skipping when key is held. */
    public boolean navUpPressed, navDownPressed, navLeftPressed, navRightPressed;

    private boolean enterDown;
    private boolean escapeDown;
    private boolean battleDown;
    private boolean n1Down, n2Down, n3Down, n4Down;
    private boolean rDown;

    private boolean navUpLatch, navDownLatch, navLeftLatch, navRightLatch;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            up = true;
            if (!navUpLatch) {
                navUpPressed = true;
                navUpLatch = true;
            }
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            down = true;
            if (!navDownLatch) {
                navDownPressed = true;
                navDownLatch = true;
            }
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            left = true;
            if (!navLeftLatch) {
                navLeftPressed = true;
                navLeftLatch = true;
            }
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = true;
            if (!navRightLatch) {
                navRightPressed = true;
                navRightLatch = true;
            }
        }
        if (code == KeyEvent.VK_E) {
            if (!interact) interactPressed = true;
            interact = true;
        }
        if (code == KeyEvent.VK_ENTER) {
            if (!enterDown) enterPressed = true;
            enterDown = true;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            if (!escapeDown) escapePressed = true;
            escapeDown = true;
        }
        if (code == KeyEvent.VK_B) {
            if (!battleDown) battlePressed = true;
            battleDown = true;
        }
        if (code == KeyEvent.VK_1) {
            if (!n1Down) n1Pressed = true;
            n1Down = true;
        }
        if (code == KeyEvent.VK_2) {
            if (!n2Down) n2Pressed = true;
            n2Down = true;
        }
        if (code == KeyEvent.VK_3) {
            if (!n3Down) n3Pressed = true;
            n3Down = true;
        }
        if (code == KeyEvent.VK_4) {
            if (!n4Down) n4Pressed = true;
            n4Down = true;
        }
        if (code == KeyEvent.VK_R) {
            if (!rDown) rPressed = true;
            rDown = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            up = false;
            navUpLatch = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            down = false;
            navDownLatch = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            left = false;
            navLeftLatch = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            right = false;
            navRightLatch = false;
        }
        if (code == KeyEvent.VK_E)                               interact = false;
        if (code == KeyEvent.VK_ENTER)  enterDown = false;
        if (code == KeyEvent.VK_ESCAPE) escapeDown = false;
        if (code == KeyEvent.VK_B)      battleDown = false;
        if (code == KeyEvent.VK_1)      n1Down = false;
        if (code == KeyEvent.VK_2)      n2Down = false;
        if (code == KeyEvent.VK_3)      n3Down = false;
        if (code == KeyEvent.VK_4)      n4Down = false;
        if (code == KeyEvent.VK_R)      rDown = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void clearPressed() {
        interactPressed = false;
        enterPressed = false;
        escapePressed = false;
        battlePressed = false;
        n1Pressed = false;
        n2Pressed = false;
        n3Pressed = false;
        n4Pressed = false;
        rPressed = false;
        navUpPressed = false;
        navDownPressed = false;
        navLeftPressed = false;
        navRightPressed = false;
    }
}