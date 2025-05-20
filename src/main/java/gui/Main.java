package gui;

import javax.swing.text.SimpleAttributeSet;
import tile.GoldDoor;
import tile.Stairs;
import tile.Start;
import tile.Gold;
import tile.Empty;
import tile.Wall;
import tile.Tile;
import tile.character.Enemy;
import tile.character.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Enhanced Main GUI class with improved visualizations
 *
 * @version 2.0
 * @author tp275, yourusername
 */
public class Main extends JFrame {

    // Components
    private JTextPane log;
    private JPanel mapPanel;
    private JPanel dungeonImagePanel;
    private JLabel dungeonStat, floorStat, levelStat, hpStat, xpStat, goldStat;
    private JButton up, down, left, right, help, quit;
    private JProgressBar hpBar, xpBar;
    private JPanel minimap;

    // Game state
    private Player player;
    private Logger logger;

    // Visual elements
    private final Color BACKGROUND_COLOR = new Color(32, 32, 40);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color PANEL_COLOR = new Color(48, 48, 56);
    private final Color BUTTON_COLOR = new Color(70, 70, 80);
    private final Color BUTTON_HOVER_COLOR = new Color(90, 90, 100);
    private final Color HP_COLOR = new Color(220, 80, 80);
    private final Color XP_COLOR = new Color(80, 220, 120);
    private final Color GOLD_COLOR = new Color(255, 215, 0);

    // Tile colors for enhanced map
    private final Map<String, Color> tileColors = new HashMap<>();

    // Animation related fields
    private Timer animationTimer;
    private JLabel playerAnimationLabel;
    private ImageIcon[] playerAnimationFrames;
    private int currentFrame = 0;

    // Sound effects
    private boolean soundEnabled = true;

    public Main() {
        setTitle("Djeneric Dungeon Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 700);
        setLocationRelativeTo(null);

        initializeTileColors();
        initializeLogger();
        initializeUI();
        initializeGame();
        initializeAnimations();
    }

    private void initializeTileColors() {
        tileColors.put("Wall", new Color(80, 80, 90));
        tileColors.put("Empty", new Color(48, 48, 56));
        tileColors.put("Start", new Color(100, 180, 255));
        tileColors.put("Stairs", new Color(200, 130, 255));
        tileColors.put("Gold", GOLD_COLOR);
        tileColors.put("Gold Door", new Color(180, 150, 80));
        tileColors.put("Enemy", new Color(220, 80, 80));
        tileColors.put("Player", new Color(80, 220, 120));
    }

    private void initializeLogger() {
        try {
            logger = Logger.getLogger("gamelog");
            String timestamp = new SimpleDateFormat("M-d_HHmmss").format(Calendar.getInstance().getTime());
            FileHandler handler = new FileHandler("gamelog_" + timestamp + ".log");
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
        } catch (IOException e) {
            System.out.println("IOException while initializing logger");
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        // Set the look and feel to a darker theme
        try {
            // Try to use Nimbus with custom colors for a modern look
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());

                    // Customize Nimbus colors
                    UIManager.put("nimbusBase", PANEL_COLOR);
                    UIManager.put("nimbusBlueGrey", BACKGROUND_COLOR);
                    UIManager.put("control", BACKGROUND_COLOR);
                    UIManager.put("text", TEXT_COLOR);
                    UIManager.put("nimbusLightBackground", PANEL_COLOR);
                    UIManager.put("nimbusSelectionBackground", new Color(120, 140, 180));

                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, use the default look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Set up the main content panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(mainPanel);

        // Add the main panels to the layout
        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // Create the map panel (will be populated later)
        mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());
        mapPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEXT_COLOR),
                "Map",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16),
                TEXT_COLOR));
        mapPanel.setBackground(PANEL_COLOR);
        mapPanel.setPreferredSize(new Dimension(300, 300));
        mapPanel.setMinimumSize(new Dimension(300, 300));

        // Create the minimap panel for the detailed tile view
        minimap = new JPanel();
        minimap.setBackground(PANEL_COLOR);
        minimap.setPreferredSize(new Dimension(280, 280));
        mapPanel.add(minimap, BorderLayout.CENTER);

        leftPanel.add(mapPanel);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(createStatsPanel());

        return leftPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEXT_COLOR),
                "Stats",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16),
                TEXT_COLOR));
        statsPanel.setBackground(PANEL_COLOR);

        // Create a subpanel for each stat
        Font font = new Font("SansSerif", Font.BOLD, 16);

        // Location stats
        JPanel locationPanel = new JPanel(new GridLayout(1, 4));
        locationPanel.setBackground(PANEL_COLOR);
        JLabel dungeonLabel = new JLabel("Dungeon:");
        dungeonLabel.setFont(font);
        dungeonLabel.setForeground(TEXT_COLOR);
        locationPanel.add(dungeonLabel);

        dungeonStat = new JLabel("1");
        dungeonStat.setFont(font);
        dungeonStat.setForeground(TEXT_COLOR);
        locationPanel.add(dungeonStat);

        JLabel floorLabel = new JLabel("Floor:");
        floorLabel.setFont(font);
        floorLabel.setForeground(TEXT_COLOR);
        locationPanel.add(floorLabel);

        floorStat = new JLabel("1");
        floorStat.setFont(font);
        floorStat.setForeground(TEXT_COLOR);
        locationPanel.add(floorStat);

        statsPanel.add(locationPanel);

        // Level stat
        JPanel levelPanel = new JPanel(new GridLayout(1, 2));
        levelPanel.setBackground(PANEL_COLOR);
        JLabel levelLabel = new JLabel("Level:");
        levelLabel.setFont(font);
        levelLabel.setForeground(TEXT_COLOR);
        levelPanel.add(levelLabel);

        levelStat = new JLabel("1");
        levelStat.setFont(font);
        levelStat.setForeground(TEXT_COLOR);
        levelPanel.add(levelStat);

        statsPanel.add(levelPanel);

        // HP stat with progress bar
        JPanel hpPanel = new JPanel(new GridLayout(2, 1));
        hpPanel.setBackground(PANEL_COLOR);
        JPanel hpLabelPanel = new JPanel(new GridLayout(1, 2));
        hpLabelPanel.setBackground(PANEL_COLOR);

        JLabel hpLabel = new JLabel("HP:");
        hpLabel.setFont(font);
        hpLabel.setForeground(TEXT_COLOR);
        hpLabelPanel.add(hpLabel);

        hpStat = new JLabel("0");
        hpStat.setFont(font);
        hpStat.setForeground(TEXT_COLOR);
        hpLabelPanel.add(hpStat);

        hpPanel.add(hpLabelPanel);

        hpBar = new JProgressBar(0, 100);
        hpBar.setValue(100);
        hpBar.setForeground(HP_COLOR);
        hpBar.setBackground(PANEL_COLOR);
        hpBar.setBorderPainted(false);
        hpBar.setStringPainted(true);
        hpPanel.add(hpBar);

        statsPanel.add(hpPanel);

        // XP stat with progress bar
        JPanel xpPanel = new JPanel(new GridLayout(2, 1));
        xpPanel.setBackground(PANEL_COLOR);
        JPanel xpLabelPanel = new JPanel(new GridLayout(1, 2));
        xpLabelPanel.setBackground(PANEL_COLOR);

        JLabel xpLabel = new JLabel("XP:");
        xpLabel.setFont(font);
        xpLabel.setForeground(TEXT_COLOR);
        xpLabelPanel.add(xpLabel);

        xpStat = new JLabel("0");
        xpStat.setFont(font);
        xpStat.setForeground(TEXT_COLOR);
        xpLabelPanel.add(xpStat);

        xpPanel.add(xpLabelPanel);

        xpBar = new JProgressBar(0, 100);
        xpBar.setValue(0);
        xpBar.setForeground(XP_COLOR);
        xpBar.setBackground(PANEL_COLOR);
        xpBar.setBorderPainted(false);
        xpBar.setStringPainted(true);
        xpPanel.add(xpBar);

        statsPanel.add(xpPanel);

        // Gold stat
        JPanel goldPanel = new JPanel(new GridLayout(1, 2));
        goldPanel.setBackground(PANEL_COLOR);
        JLabel goldLabel = new JLabel("Gold:");
        goldLabel.setFont(font);
        goldLabel.setForeground(GOLD_COLOR);
        goldPanel.add(goldLabel);

        goldStat = new JLabel("0");
        goldStat.setFont(font);
        goldStat.setForeground(GOLD_COLOR);
        goldPanel.add(goldStat);

        statsPanel.add(goldPanel);

        // Add spacer
        statsPanel.add(Box.createVerticalStrut(10));

        // Add sound toggle
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        soundPanel.setBackground(PANEL_COLOR);
        JToggleButton soundToggle = new JToggleButton("Sound: ON");
        soundToggle.setSelected(true);
        soundToggle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        soundToggle.setBackground(BUTTON_COLOR);
        soundToggle.setForeground(TEXT_COLOR);
        soundToggle.addActionListener(e -> {
            soundEnabled = !soundEnabled;
            soundToggle.setText("Sound: " + (soundEnabled ? "ON" : "OFF"));
        });
        soundPanel.add(soundToggle);

        statsPanel.add(soundPanel);

        return statsPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Create the dungeon image panel
        dungeonImagePanel = new JPanel(new BorderLayout());
        dungeonImagePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEXT_COLOR),
                "Dungeon",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16),
                TEXT_COLOR));
        dungeonImagePanel.setBackground(PANEL_COLOR);
        dungeonImagePanel.setPreferredSize(new Dimension(400, 400));

        // Add player animation label
        playerAnimationLabel = new JLabel();
        playerAnimationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playerAnimationLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        dungeonImagePanel.add(playerAnimationLabel, BorderLayout.CENTER);

        centerPanel.add(dungeonImagePanel, BorderLayout.CENTER);
        centerPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        Font font = new Font("SansSerif", Font.BOLD, 16);

        // Create direction buttons
        up = createButton("↑", font, 80, 60, "Move upward", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        down = createButton("↓", font, 80, 60, "Move downward", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        left = createButton("←", font, 80, 60, "Move left", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        right = createButton("→", font, 80, 60, "Move right", BUTTON_COLOR, BUTTON_HOVER_COLOR);

        // Create help and quit buttons
        help = createButton("Help", font, 100, 60, "Show help", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        quit = createButton("Quit", font, 100, 60, "Exit game", new Color(150, 70, 70), new Color(190, 80, 80));

        // Add buttons to the panel
        gbc.gridx = 1; gbc.gridy = 0; buttonsPanel.add(up, gbc);
        gbc.gridx = 0; gbc.gridy = 1; buttonsPanel.add(left, gbc);
        gbc.gridx = 1; gbc.gridy = 1; buttonsPanel.add(help, gbc);
        gbc.gridx = 2; gbc.gridy = 1; buttonsPanel.add(right, gbc);
        gbc.gridx = 1; gbc.gridy = 2; buttonsPanel.add(down, gbc);
        gbc.gridx = 3; gbc.gridy = 1; buttonsPanel.add(quit, gbc);

        // Add action listeners
        up.addActionListener(e -> handleMovement(new Point(-1, 0)));
        down.addActionListener(e -> handleMovement(new Point(1, 0)));
        left.addActionListener(e -> handleMovement(new Point(0, -1)));
        right.addActionListener(e -> handleMovement(new Point(0, 1)));

        help.addActionListener(this::showHelp);
        quit.addActionListener(e -> {
            logFileOnly("pressed quit");
            System.exit(0);
        });

        return buttonsPanel;
    }

    private JButton createButton(String text, Font font, int width, int height,
                                 String tooltip, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(new Dimension(width, height));
        button.setToolTipText(tooltip);
        button.setBackground(normalColor);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });

        return button;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setPreferredSize(new Dimension(500, 0));

        // Create the game log
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TEXT_COLOR),
                "Game Log",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16),
                TEXT_COLOR));

        log = new JTextPane();
        log.setEditable(false);
        log.setFont(new Font("SansSerif", Font.PLAIN, 16));
// Add this instead for word wrapping in JTextPane
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, "SansSerif");
        StyleConstants.setFontSize(attrs, 16);
        StyleConstants.setForeground(attrs, TEXT_COLOR);
        log.setParagraphAttributes(attrs, true);

// This enables word wrapping in JTextPane
        log.setEditorKit(new javax.swing.text.html.HTMLEditorKit());
        log.setContentType("text/html");
        log.setBackground(PANEL_COLOR);
        log.setForeground(TEXT_COLOR);
        log.setCaretColor(TEXT_COLOR);

        JScrollPane scrollPane = new JScrollPane(log);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    /**
     * Custom ScrollBar UI for a more modern look
     */
    private class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(100, 100, 110);
            this.thumbDarkShadowColor = new Color(80, 80, 90);
            this.thumbHighlightColor = new Color(120, 120, 130);
            this.thumbLightShadowColor = new Color(100, 100, 110);
            this.trackColor = PANEL_COLOR;
            this.trackHighlightColor = PANEL_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    private void initializeGame() {
        log("Welcome to the most fantastic *Djeneric Dungeon Crawler!*", GOLD_COLOR);
        log("You stride into the fiendishly not very difficult Caverns of Adoddle", Color.WHITE);
        player = new Player(1);
        updateMap();
        updatePicture();
        updateStats();
    }

    private void initializeAnimations() {
        // Create player animation frames (these would be loaded from resource files in a real implementation)
        playerAnimationFrames = new ImageIcon[4];

        // For now, we'll just create colored rectangles as placeholders
        for (int i = 0; i < playerAnimationFrames.length; i++) {
            BufferedImage img = new BufferedImage(60, 80, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            int brightness = 150 + (i * 20); // Animation effect - changing brightness
            g.setColor(new Color(80, brightness, 80));
            g.fillRect(0, 0, 60, 80);
            g.setColor(new Color(60, 60, 60));
            g.drawRect(0, 0, 59, 79);
            g.dispose();
            playerAnimationFrames[i] = new ImageIcon(img);
        }

        // Create animation timer
        animationTimer = new Timer(150, e -> {
            currentFrame = (currentFrame + 1) % playerAnimationFrames.length;
            playerAnimationLabel.setIcon(playerAnimationFrames[currentFrame]);
        });

        // Start animation
        animationTimer.start();
    }

    private void handleMovement(Point movement) {
        logFileOnly("pressed " + getDirectionName(movement));

        // Check the target location before moving
        Point targetPosition = new Point(player.getFloorLocation().x + movement.x, player.getFloorLocation().y + movement.y);
        Tile targetTile = player.getCurrentFloor().getTileByPoint(targetPosition);

        // Check if we're moving onto a gold door
        boolean movingToGoldDoor = false;
        int doorCost = 0;
        boolean doorWasOpen = false;

        if (targetTile instanceof GoldDoor) {
            GoldDoor door = (GoldDoor) targetTile;
            movingToGoldDoor = true;
            doorCost = door.getCost();
            doorWasOpen = door.isOpen();
        }

        // Try to move (will automatically open door if player has enough gold)
        boolean moved = player.updateLocation(movement);

        if (moved) {
            // Play sound effect for movement
            if (soundEnabled) {
                playSound("move");
            }

            // Add movement animation
            animateMovement(movement);

            // If we moved onto a gold door that wasn't open before, it must have been
            // automatically opened (since we successfully moved there)
            if (movingToGoldDoor && !doorWasOpen) {
                log("You spend " + doorCost + " gold to open the door. It swings open with a satisfying click!", GOLD_COLOR);
                if (soundEnabled) {
                    playSound("door");
                }
            }

            // Play the tile and update game state
            String tileResult = playTile();
            log(tileResult, getTileResultColor(tileResult));

            // Update visual elements
            updateMap();
            updateStats();
            updatePicture();
            checkFinished();
        } else {
            // If we tried to move to a gold door but couldn't, explain why
            if (movingToGoldDoor && !doorWasOpen) {
                log("This door requires " + doorCost + " gold to open. You only have " + player.getGold() + " gold. You cannot pass through.", GOLD_COLOR);
                if (soundEnabled) {
                    playSound("locked");
                }
            } else {
                log("You can't move here! Try again.", Color.RED);
                if (soundEnabled) {
                    playSound("bump");
                }
            }
        }
    }

    private String getDirectionName(Point movement) {
        if (movement.x == -1 && movement.y == 0) return "up";
        if (movement.x == 1 && movement.y == 0) return "down";
        if (movement.x == 0 && movement.y == -1) return "left";
        if (movement.x == 0 && movement.y == 1) return "right";
        return "unknown";
    }

    private void animateMovement(Point movement) {
        // This would be a more sophisticated animation in a real implementation
        // For now, we'll just flash the player animation
        Timer flashTimer = new Timer(50, null);
        final int[] count = {0};

        flashTimer.addActionListener(e -> {
            if (count[0] % 2 == 0) {
                playerAnimationLabel.setIcon(null);
            } else {
                playerAnimationLabel.setIcon(playerAnimationFrames[currentFrame]);
            }
            count[0]++;

            if (count[0] > 6) {
                flashTimer.stop();
                playerAnimationLabel.setIcon(playerAnimationFrames[currentFrame]);
            }
        });

        flashTimer.start();
    }

    private Color getTileResultColor(String result) {
        if (result.contains("gold")) return GOLD_COLOR;
        if (result.contains("hit")) return Color.RED;
        if (result.contains("defeated") || result.contains("victorious")) return new Color(80, 220, 120);
        return Color.WHITE;
    }

    private String playTile() {
        Tile currentTile = player.getFloorTile();
        String result = player.playTile(currentTile);

        // Play sound effects based on tile type
        if (soundEnabled) {
            if (currentTile instanceof Gold) {
                playSound("gold");
            } else if (currentTile instanceof Enemy) {
                playSound("battle");
            } else if (currentTile instanceof Stairs) {
                playSound("stairs");
            }
        }

        return result;
    }

    private void updateStats() {
        // Update basic stats
        dungeonStat.setText(String.valueOf(player.getCurrentDungeonID() + 1));
        floorStat.setText(String.valueOf(player.getCurrentFloorID() + 1));
        levelStat.setText(String.valueOf(player.getLevel()));
        hpStat.setText(String.valueOf(player.getHp()));
        xpStat.setText(String.valueOf(player.getXp()));
        goldStat.setText(String.valueOf(player.getGold()));

        // Update progress bars
        int maxHP = player.getLevel() * 70; // Based on the resetHP() formula in Character class
        hpBar.setMaximum(maxHP);
        hpBar.setValue(player.getHp());
        hpBar.setString(player.getHp() + "/" + maxHP);

        // Calculate XP progress based on the formula in PlayerStats.addXp()
        int level = player.getLevel();
        int xpForNextLevel = (int)((level + 5) + Math.pow(level, 2));
        xpBar.setMaximum(xpForNextLevel);
        xpBar.setValue(player.getXp());
        xpBar.setString(player.getXp() + "/" + xpForNextLevel);
    }

    private void updatePicture() {
        try {
            String imagePath = "/dungeon" + player.getCurrentDungeonID() + ".jpg";
            java.net.URL imageURL = getClass().getResource(imagePath);

            if (imageURL == null) {
                // If image not found, create a placeholder
                BufferedImage placeholder = createDungeonPlaceholder();
                ImageIcon icon = new ImageIcon(placeholder);

                // Add the image to the panel (under the player animation)
                JLabel imageLabel = new JLabel(icon);
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dungeonImagePanel.removeAll(); // Clear previous components
                dungeonImagePanel.add(imageLabel, BorderLayout.CENTER);
                dungeonImagePanel.add(playerAnimationLabel, BorderLayout.SOUTH);
            } else {
                // Load the actual image
                ImageIcon icon = new ImageIcon(imageURL);

                // Scale the image to fit the panel
                Image scaledImage = icon.getImage().getScaledInstance(
                        dungeonImagePanel.getWidth() - 20,
                        dungeonImagePanel.getHeight() - 80,
                        Image.SCALE_SMOOTH);

                // Add the image to the panel (under the player animation)
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dungeonImagePanel.removeAll(); // Clear previous components
                dungeonImagePanel.add(imageLabel, BorderLayout.CENTER);
                dungeonImagePanel.add(playerAnimationLabel, BorderLayout.SOUTH);
            }

            // Ensure player animation is visible
            playerAnimationLabel.setVisible(true);
            dungeonImagePanel.revalidate();
            dungeonImagePanel.repaint();

        } catch (Exception e) {
            log("Error loading image: " + e.getMessage(), Color.RED);
        }
    }

    private BufferedImage createDungeonPlaceholder() {
        // Create a placeholder image for dungeons without images
        BufferedImage placeholder = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();

        // Fill background with a gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(40, 40, 60),
                0, 300, new Color(20, 20, 30));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 400, 300);

        // Draw some dungeon features
        g2d.setColor(new Color(60, 60, 80));
        g2d.fillRect(50, 100, 300, 200);

        // Draw some stone texture
        g2d.setColor(new Color(70, 70, 90));
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * 300) + 50;
            int y = (int) (Math.random() * 200) + 100;
            int width = (int) (Math.random() * 40) + 20;
            int height = (int) (Math.random() * 20) + 10;
            g2d.fillRect(x, y, width, height);
        }

        // Add some ambient lighting
        g2d.setColor(new Color(100, 100, 150, 50));
        g2d.fillOval(150, 50, 100, 100);

        // Add dungeon name
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2d.drawString("Dungeon " + (player.getCurrentDungeonID() + 1), 130, 50);

        g2d.dispose();
        return placeholder;
    }

    private void updateMap() {
        // Clear the existing map
        minimap.removeAll();

        // Get the map data
        String rawMap = player.getPrintableMap();
        String[] mapLines = rawMap.split("\n");

        // Create a grid layout based on the map dimensions
        int rows = mapLines.length;
        int cols = rows > 0 ? mapLines[0].length() : 0;

        minimap.setLayout(new GridLayout(rows, cols, 1, 1));

        // Player position
        Point playerPos = player.getFloorLocation();

        // Create a colored tile for each character in the map
        for (int i = 0; i < rows; i++) {
            String line = mapLines[i];
            for (int j = 0; j < line.length(); j++) {
                char tileChar = line.charAt(j);

                JPanel tilePanel = new JPanel();
                tilePanel.setPreferredSize(new Dimension(20, 20));

                // Set the color and icon based on the tile type
                Color tileColor = PANEL_COLOR; // Default color
                String tooltip = "";

                switch (tileChar) {
                    case 'P': // Player
                        tileColor = tileColors.get("Player");
                        tooltip = "Player";

                        // Add an animated player marker
                        tilePanel.setLayout(new BorderLayout());
                        JLabel playerMarker = new JLabel("P");
                        playerMarker.setForeground(Color.WHITE);
                        playerMarker.setFont(new Font("SansSerif", Font.BOLD, 14));
                        playerMarker.setHorizontalAlignment(SwingConstants.CENTER);
                        tilePanel.add(playerMarker, BorderLayout.CENTER);
                        break;
                    case '-': // Wall
                        tileColor = tileColors.get("Wall");
                        tooltip = "Wall";
                        break;
                    case 's': // Start
                        tileColor = tileColors.get("Start");
                        tooltip = "Start";
                        break;
                    case 'x': // Stairs
                        tileColor = tileColors.get("Stairs");
                        tooltip = "Stairs to next level";
                        break;
                    case 'o': // Empty
                        tileColor = tileColors.get("Empty");
                        tooltip = "Empty tile";
                        break;
                    case 'e': // Enemy
                        tileColor = tileColors.get("Enemy");
                        tooltip = "Enemy";

                        // Add an enemy marker
                        tilePanel.setLayout(new BorderLayout());
                        JLabel enemyMarker = new JLabel("E");
                        enemyMarker.setForeground(Color.WHITE);
                        enemyMarker.setFont(new Font("SansSerif", Font.BOLD, 14));
                        enemyMarker.setHorizontalAlignment(SwingConstants.CENTER);
                        tilePanel.add(enemyMarker, BorderLayout.CENTER);
                        break;
                    case 'g': // Gold
                        tileColor = tileColors.get("Gold");
                        tooltip = "Gold";

                        // Add a gold marker
                        tilePanel.setLayout(new BorderLayout());
                        JLabel goldMarker = new JLabel("G");
                        goldMarker.setForeground(Color.BLACK);
                        goldMarker.setFont(new Font("SansSerif", Font.BOLD, 14));
                        goldMarker.setHorizontalAlignment(SwingConstants.CENTER);
                        tilePanel.add(goldMarker, BorderLayout.CENTER);
                        break;
                    case 'd': // Gold Door (closed)
                        tileColor = tileColors.get("Gold Door");
                        tooltip = "Gold Door (closed)";

                        // Add a door marker
                        tilePanel.setLayout(new BorderLayout());
                        JLabel doorMarker = new JLabel("D");
                        doorMarker.setForeground(Color.WHITE);
                        doorMarker.setFont(new Font("SansSerif", Font.BOLD, 14));
                        doorMarker.setHorizontalAlignment(SwingConstants.CENTER);
                        tilePanel.add(doorMarker, BorderLayout.CENTER);
                        break;
                    case 'D': // Gold Door (open)
                        tileColor = new Color(140, 120, 80); // Lighter color for open door
                        tooltip = "Gold Door (open)";

                        // Add an open door marker
                        tilePanel.setLayout(new BorderLayout());
                        JLabel openDoorMarker = new JLabel("O");
                        openDoorMarker.setForeground(Color.WHITE);
                        openDoorMarker.setFont(new Font("SansSerif", Font.BOLD, 14));
                        openDoorMarker.setHorizontalAlignment(SwingConstants.CENTER);
                        tilePanel.add(openDoorMarker, BorderLayout.CENTER);
                        break;
                }

                tilePanel.setBackground(tileColor);
                tilePanel.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 30), 1));

                // Add tooltip
                tilePanel.setToolTipText(tooltip);

                // Add a slight glow effect for the player's current position
                if (i == playerPos.x && j == playerPos.y) {
                    tilePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                }

                minimap.add(tilePanel);
            }
        }

        // Refresh the minimap
        minimap.revalidate();
        minimap.repaint();
    }

    private void checkFinished() {
        if (player.isVictorious()) {
            log("🎉 Congratulations, you have completed all dungeons! 🎉", GOLD_COLOR);

            // Disable movement buttons
            up.setEnabled(false);
            down.setEnabled(false);
            left.setEnabled(false);
            right.setEnabled(false);

            // Show victory animation/screen
            showVictoryScreen();
        }
    }

    private void showVictoryScreen() {
        // Create a victory overlay
        JPanel victoryPanel = new JPanel(new BorderLayout());
        victoryPanel.setBackground(new Color(0, 0, 0, 150));
        victoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create victory message
        JLabel victoryLabel = new JLabel("VICTORY!");
        victoryLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        victoryLabel.setForeground(GOLD_COLOR);
        victoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create stats summary
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        statsPanel.setOpaque(false);

        JLabel levelLabel = new JLabel("Final Level: " + player.getLevel());
        levelLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsPanel.add(levelLabel);

        JLabel goldLabel = new JLabel("Gold Collected: " + player.getGold());
        goldLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        goldLabel.setForeground(GOLD_COLOR);
        goldLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsPanel.add(goldLabel);

        // Add some flair
        JLabel congratsLabel = new JLabel("You have conquered the dungeons!");
        congratsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        congratsLabel.setForeground(TEXT_COLOR);
        congratsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statsPanel.add(congratsLabel);

        // Add a button to play again
        JButton playAgainButton = createButton("Play Again", new Font("SansSerif", Font.BOLD, 20),
                200, 60, "Start a new game", new Color(80, 180, 80), new Color(100, 220, 100));
        playAgainButton.addActionListener(e -> {
            // Reset the game
            initializeGame();
            enableButtons();

            // Remove victory screen
            dungeonImagePanel.remove(victoryPanel);
            dungeonImagePanel.revalidate();
            dungeonImagePanel.repaint();
        });

        // Create a panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(playAgainButton);

        // Add components to victory panel
        victoryPanel.add(victoryLabel, BorderLayout.NORTH);
        victoryPanel.add(statsPanel, BorderLayout.CENTER);
        victoryPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add to the dungeon image panel
        dungeonImagePanel.add(victoryPanel, BorderLayout.CENTER);
        dungeonImagePanel.revalidate();
        dungeonImagePanel.repaint();

        // Play victory sound
        if (soundEnabled) {
            playSound("victory");
        }
    }

    private void enableButtons() {
        up.setEnabled(true);
        down.setEnabled(true);
        left.setEnabled(true);
        right.setEnabled(true);
        help.setEnabled(true);
    }

    private void showHelp(ActionEvent e) {
        logFileOnly("pressed help");

        // Create a help dialog with styled content
        JDialog helpDialog = new JDialog(this, "Game Help", true);
        helpDialog.setSize(500, 400);
        helpDialog.setLocationRelativeTo(this);

        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBackground(PANEL_COLOR);
        helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create help content
        JTextPane helpText = new JTextPane();
        helpText.setEditable(false);
        helpText.setBackground(PANEL_COLOR);
        helpText.setForeground(TEXT_COLOR);

        // Set styled content
        StyledDocument doc = helpText.getStyledDocument();

        // Create styles
        Style titleStyle = helpText.addStyle("Title", null);
        StyleConstants.setFontFamily(titleStyle, "SansSerif");
        StyleConstants.setFontSize(titleStyle, 20);
        StyleConstants.setBold(titleStyle, true);
        StyleConstants.setForeground(titleStyle, GOLD_COLOR);

        Style headingStyle = helpText.addStyle("Heading", null);
        StyleConstants.setFontFamily(headingStyle, "SansSerif");
        StyleConstants.setFontSize(headingStyle, 16);
        StyleConstants.setBold(headingStyle, true);
        StyleConstants.setForeground(headingStyle, new Color(180, 180, 220));

        Style normalStyle = helpText.addStyle("Normal", null);
        StyleConstants.setFontFamily(normalStyle, "SansSerif");
        StyleConstants.setFontSize(normalStyle, 14);
        StyleConstants.setForeground(normalStyle, TEXT_COLOR);

        try {
            // Add title
            doc.insertString(doc.getLength(), "Djeneric Dungeon Crawler Help\n\n", titleStyle);

            // Add controls section
            doc.insertString(doc.getLength(), "Controls\n", headingStyle);
            doc.insertString(doc.getLength(), "• Arrow buttons: Move your character\n", normalStyle);
            doc.insertString(doc.getLength(), "• Help: Show this help screen\n", normalStyle);
            doc.insertString(doc.getLength(), "• Quit: Exit the game\n\n", normalStyle);

            // Add gameplay section
            doc.insertString(doc.getLength(), "Gameplay\n", headingStyle);
            doc.insertString(doc.getLength(), "• Explore dungeons and defeat enemies to gain XP\n", normalStyle);
            doc.insertString(doc.getLength(), "• Collect gold (G) to open gold doors (D)\n", normalStyle);
            doc.insertString(doc.getLength(), "• Find stairs (x) to proceed to the next floor or dungeon\n", normalStyle);
            doc.insertString(doc.getLength(), "• Complete all 5 dungeons to win\n\n", normalStyle);

            // Add map legend section
            doc.insertString(doc.getLength(), "Map Legend\n", headingStyle);
            doc.insertString(doc.getLength(), "• P: Player\n", normalStyle);
            doc.insertString(doc.getLength(), "• -: Wall\n", normalStyle);
            doc.insertString(doc.getLength(), "• o: Empty tile\n", normalStyle);
            doc.insertString(doc.getLength(), "• e: Enemy\n", normalStyle);
            doc.insertString(doc.getLength(), "• g: Gold\n", normalStyle);
            doc.insertString(doc.getLength(), "• d: Gold Door (closed)\n", normalStyle);
            doc.insertString(doc.getLength(), "• D: Gold Door (open)\n", normalStyle);
            doc.insertString(doc.getLength(), "• x: Stairs\n", normalStyle);
            doc.insertString(doc.getLength(), "• s: Start\n", normalStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(helpText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        helpPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a close button
        JButton closeButton = createButton("Close", new Font("SansSerif", Font.BOLD, 16),
                100, 40, "Close help", BUTTON_COLOR, BUTTON_HOVER_COLOR);
        closeButton.addActionListener(event -> helpDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(closeButton);
        helpPanel.add(buttonPanel, BorderLayout.SOUTH);

        helpDialog.setContentPane(helpPanel);
        helpDialog.setVisible(true);
    }

    private void log(String message, Color color) {
        // Create styled document for colored text
        StyledDocument doc = (StyledDocument) log.getDocument();
        Style style = log.addStyle("ColorStyle", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.setCaretPosition(log.getDocument().getLength());
        logger.info(message);
    }

    private void log(String message) {
        log(message, TEXT_COLOR);
    }

    private void logFileOnly(String message) {
        logger.info(message);
    }

    /**
     * Play a sound effect
     *
     * @param soundType Type of sound to play
     */
    private void playSound(String soundType) {
        // In a real implementation, this would play actual sound files
        // For this example, we'll just log it
        logFileOnly("Playing sound: " + soundType);

        // A real implementation would load and play sounds like this:
        /*
        try {
            // Load the sound file
            java.net.URL soundURL = getClass().getResource("/sounds/" + soundType + ".wav");
            if (soundURL != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to set Nimbus look and feel for a modern appearance
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // If Nimbus is not available, fall back to the default look and feel
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            Main window = new Main();
            window.setVisible(true);
        });
    }
}