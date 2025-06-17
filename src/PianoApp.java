import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;

public class PianoApp extends JFrame {
    private static final int TOTAL_KEYS = 88;
    private static final int START_NOTE = 21; // A0
    private PianoKey[] keys;
    private Synthesizer synth;
    private MidiChannel channel;
    private PianoKey lastPressedKey = null;

    public PianoApp() {
        setTitle("Project Melody");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(30, 30, 35));
        getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(30, 30, 35)));

        ImageIcon icon = new ImageIcon("icon.jpg");
        setIconImage(icon.getImage());
        // Create a timer to refresh the piano panel
        Timer refreshTimer = new Timer(1000 / 60, e -> {
            if (isVisible()) {
                repaint();
            }
        });
        refreshTimer.start();

        // Create title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(new Color(35, 35, 40));
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 50)));



        // Add components
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        
        initializeMIDI();
        createPianoKeys();
        
        setLocationRelativeTo(null);
        setSize(1240, 250);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutPianoKeys();
            }
        });

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (lastPressedKey != null) {
                    releaseKey(lastPressedKey);
                    lastPressedKey = null;
                }
            }
        });
    }

    private void initializeMIDI() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channel = synth.getChannels()[0];
            // Set the instrument to Acoustic Grand Piano
            channel.programChange(0);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void createPianoKeys() {
        JPanel pianoPanel = new JPanel();
        pianoPanel.setLayout(null);
        pianoPanel.setBackground(new Color(25, 25, 30));
        add(pianoPanel, BorderLayout.CENTER);

        keys = new PianoKey[TOTAL_KEYS];
        for (int i = 0; i < TOTAL_KEYS; i++) {
            int noteNumber = START_NOTE + i;
            boolean isBlack = isBlackKey(noteNumber);
            keys[i] = new PianoKey(noteNumber, isBlack);
            pianoPanel.add(keys[i]);
            if (isBlack) {
                pianoPanel.setComponentZOrder(keys[i], 0);
            }
        }

        MouseAdapter pianoMouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point point = e.getPoint();
                    Component component = SwingUtilities.getDeepestComponentAt(pianoPanel, point.x, point.y);
                    if (component instanceof PianoKey) {
                        PianoKey currentKey = (PianoKey) component;
                        if (lastPressedKey != null) {
                            releaseKey(lastPressedKey);
                        }
                        pressKey(currentKey);
                        lastPressedKey = currentKey;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (lastPressedKey != null) {
                        releaseKey(lastPressedKey);
                        lastPressedKey = null;
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    Point point = e.getPoint();
                    PianoKey currentKey = null;

                    Component component = SwingUtilities.getDeepestComponentAt(pianoPanel, point.x, point.y);
                    if (component instanceof PianoKey) {
                        currentKey = (PianoKey) component;
                    }

                    if (currentKey != lastPressedKey) {
                        if (lastPressedKey != null) {
                            releaseKey(lastPressedKey);
                        }
                        if (currentKey != null) {
                            pressKey(currentKey);
                        }
                        lastPressedKey = currentKey;
                    }
                }
            }
        };

        pianoPanel.addMouseListener(pianoMouseAdapter);
        pianoPanel.addMouseMotionListener(pianoMouseAdapter);
    }

    private void layoutPianoKeys() {
        int panelWidth = getContentPane().getWidth();
        int panelHeight = getContentPane().getHeight();

        int whiteKeyCount = 0;
        for (PianoKey key : keys) {
            if (!key.isBlackKey()) {
                whiteKeyCount++;
            }
        }

        if (whiteKeyCount == 0) return;

        double whiteKeyWidth = (double) panelWidth / whiteKeyCount;
        double whiteKeyHeight = panelHeight * 0.9;
        double blackKeyWidth = whiteKeyWidth * 0.6;
        double blackKeyHeight = whiteKeyHeight * 0.6;

        double currentX = 0;
        for (int i = 0; i < TOTAL_KEYS; i++) {
            if (!keys[i].isBlackKey()) {
                keys[i].setBounds((int) currentX, 0, (int) whiteKeyWidth, (int) whiteKeyHeight);
                currentX += whiteKeyWidth;
            }
        }

        currentX = 0;
        for (int i = 0; i < TOTAL_KEYS; i++) {
            if (keys[i].isBlackKey()) {
                keys[i].setBounds((int) (currentX - blackKeyWidth / 2), 0, (int) blackKeyWidth, (int) blackKeyHeight);
            } else {
                currentX += whiteKeyWidth;
            }
        }
    }

    private void pressKey(PianoKey key) {
        if (key != null) {
            key.setPressed(true);
            key.repaint();
            channel.noteOn(key.getNoteNumber(), 100);
        }
    }

    private void releaseKey(PianoKey key) {
        if (key != null) {
            key.setPressed(false);
            key.repaint();
            channel.noteOff(key.getNoteNumber());
        }
    }

    private boolean isBlackKey(int noteNumber) {
        int note = noteNumber % 12;
        return note == 1 || note == 3 || note == 6 || note == 8 || note == 10;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PianoApp().setVisible(true);
        });
    }
}