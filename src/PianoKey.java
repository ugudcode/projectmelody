import javax.swing.*;
import java.awt.*;

public class PianoKey extends JPanel {

    private int noteNumber;
    private boolean isBlackKey;
    private boolean isPressed;

    public PianoKey(int noteNumber, boolean isBlackKey) {
        this.noteNumber = noteNumber;
        this.isBlackKey = isBlackKey;
        setOpaque(false);
    }

    public int getNoteNumber() {
        return noteNumber;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }


    public boolean isBlackKey() {
        return isBlackKey;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = isBlackKey ? 6 : 8;
        
        if (isBlackKey) {
            // Draw black key with slight gradient
            GradientPaint keyGradient = new GradientPaint(
                0, 0, isPressed ? new Color(80, 80, 85) : new Color(20, 20, 23),
                0, getHeight(), isPressed ? new Color(90, 90, 95) : new Color(25, 25, 28));
            g2.setPaint(keyGradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Subtle highlight on top edge
            g2.setColor(new Color(255, 255, 255, 20));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(2, 1, getWidth() - 2, 1);
        } else {
            // Draw shadow for white keys
            g2.setColor(new Color(45, 45, 50));
            g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, arc, arc);

            // Draw white key
            g2.setColor(isPressed ? new Color(200, 200, 205) : new Color(235, 235, 240));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            // Draw white key border
            GradientPaint borderGradient = new GradientPaint(
                0, 0, new Color(180, 180, 180),
                0, getHeight(), new Color(210, 210, 210));
            g2.setPaint(borderGradient);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            // Add subtle highlight
            GradientPaint highlightGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 30),
                0, getHeight() / 2, new Color(255, 255, 255, 0));
            g2.setPaint(highlightGradient);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 2, arc, arc);
        }

        // Draw note label with modern font
        if (!isBlackKey) {
            g2.setColor(new Color(80, 80, 85));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String noteName = getNoteNameFromNumber(noteNumber);
            FontMetrics fm = g2.getFontMetrics();
            int noteWidth = fm.stringWidth(noteName);
            g2.drawString(noteName, (getWidth() - noteWidth) / 2, getHeight() - 10);
        } else {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            String noteName = getNoteNameFromNumber(noteNumber);
            FontMetrics fm = g2.getFontMetrics();
            int noteWidth = fm.stringWidth(noteName);
            g2.drawString(noteName, (getWidth() - noteWidth) / 2, getHeight() - 15);
        }
    }

    private String getNoteNameFromNumber(int noteNumber) {
        String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        int octave = (noteNumber - 12) / 12;
        int note = noteNumber % 12;
        return noteNames[note] + octave;
    }
}