package gui;

import tile.Tile;
import tile.character.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends JFrame {

    private JTextArea log;
    private JLabel image;
    private JLabel map;
    private JLabel dungeonStat, floorStat, levelStat, hpStat, xpStat, goldStat;
    private JButton up, down, left, right, help, quit;
    private Player player;
    private Logger logger;

    public Main() {
        setTitle("Djeneric Dungeon Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 600);
        setLocationRelativeTo(null);

        initializeLogger();
        initializeUI();
        initializeGame();
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
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        mainPanel.add(createLeftPanel(), BorderLayout.WEST);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        map = new JLabel();
        map.setFont(new Font("Monospaced", Font.BOLD, 22));
        map.setVerticalAlignment(SwingConstants.TOP);
        map.setBorder(BorderFactory.createTitledBorder("Map"));
        map.setPreferredSize(new Dimension(300, 250));
        map.setMinimumSize(new Dimension(300, 250));
        map.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(map);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(createStatsPanel());

        return leftPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Stats"));
        Font font = new Font("Monospaced", Font.BOLD, 20);

        dungeonStat = createStatRow(statsPanel, "Dungeon:", "1", font);
        floorStat = createStatRow(statsPanel, "Floor:", "1", font);
        levelStat = createStatRow(statsPanel, "Level:", "1", font);
        hpStat = createStatRow(statsPanel, "HP:", "0", font);
        xpStat = createStatRow(statsPanel, "XP:", "0", font);
        goldStat = createStatRow(statsPanel, "Gold:", "0", font);

        return statsPanel;
    }

    private JLabel createStatRow(JPanel panel, String labelText, String valueText, Font font) {
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        panel.add(label);

        JLabel value = new JLabel(valueText);
        value.setFont(font);
        panel.add(value);

        return value;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);
        image.setPreferredSize(new Dimension(400, 300));
        image.setBorder(BorderFactory.createTitledBorder("Dungeon Image"));
        centerPanel.add(image, BorderLayout.CENTER);

        centerPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

        return centerPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        Font font = new Font("SansSerif", Font.BOLD, 16);

        up = createButton("Up", font, 100, 60, "Move your character upward", Color.LIGHT_GRAY, Color.GRAY);
        down = createButton("Down", font, 100, 60, "Move your character downward", Color.LIGHT_GRAY, Color.GRAY);
        left = createButton("Left", font, 100, 60, "Move your character left", Color.LIGHT_GRAY, Color.GRAY);
        right = createButton("Right", font, 100, 60, "Move your character right", Color.LIGHT_GRAY, Color.GRAY);
        help = createButton("Help", font, 100, 60, "Help", Color.LIGHT_GRAY, Color.ORANGE);
        quit = createButton("Quit", font, 100, 60, "Quit", Color.LIGHT_GRAY, Color.RED);

        gbc.gridx = 1; gbc.gridy = 0; buttonsPanel.add(up, gbc);
        gbc.gridx = 0; gbc.gridy = 1; buttonsPanel.add(left, gbc);
        gbc.gridx = 1; gbc.gridy = 1; buttonsPanel.add(help, gbc);
        gbc.gridx = 2; gbc.gridy = 1; buttonsPanel.add(right, gbc);
        gbc.gridx = 1; gbc.gridy = 2; buttonsPanel.add(down, gbc);
        gbc.gridx = 3; gbc.gridy = 1; buttonsPanel.add(quit, gbc);

        up.addActionListener(e -> { logFileOnly("pressed up"); play(new Point(-1, 0)); });
        down.addActionListener(e -> { logFileOnly("pressed down"); play(new Point(1, 0)); });
        left.addActionListener(e -> { logFileOnly("pressed left"); play(new Point(0, -1)); });
        right.addActionListener(e -> { logFileOnly("pressed right"); play(new Point(0, 1)); });
        help.addActionListener(e -> {
            logFileOnly("pressed help");
            log("Press the movement buttons to move around the current dungeon. There are 5 dungeons to fight through.\n");
        });
        quit.addActionListener(e -> {
            logFileOnly("pressed quit");
            System.exit(0);
        });

        return buttonsPanel;
    }

    private JButton createButton(String text, Font font, int width, int height, String tooltip, Color normalColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setPreferredSize(new Dimension(width, height));
        button.setToolTipText(tooltip);
        addHoverEffect(button, normalColor, hoverColor);
        return button;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Game Log"));

        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 16));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(log);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    private void initializeGame() {
        log("Welcome to the most fantastic *Djeneric Dungeon Crawler!*\n");
        log("You stride into the fiendishly not very difficult Caverns of Adoddle\n");
        player = new Player(1);
        updateMap();
        updatePicture();
        updateStats();
    }

    private void play(Point movement) {
        if (player.updateLocation(movement)) {
            log(playTile());
            updateMap();
            updateStats();
            updatePicture();
            checkFinished();
        } else {
            log("You can't move here! Try again.\n");
        }
    }

    private void updateMap() {
        String rawMap = player.getPrintableMap();
        String spacedMap = addSpacesBetweenChars(rawMap);
        map.setText("<html><pre>" + spacedMap + "</pre></html>");
    }

    private String addSpacesBetweenChars(String mapText) {
        StringBuilder spacedMap = new StringBuilder();
        String[] lines = mapText.split("\n");
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                spacedMap.append(line.charAt(i));
                if (i < line.length() - 1) spacedMap.append(' ');
            }
            spacedMap.append('\n');
        }
        return spacedMap.toString();
    }

    private String playTile() {
        Tile currentTile = player.getFloorTile();
        return player.playTile(currentTile) + "\n";
    }

    private void updateStats() {
        dungeonStat.setText(String.valueOf(player.getCurrentDungeonID() + 1));
        floorStat.setText(String.valueOf(player.getCurrentFloorID() + 1));
        levelStat.setText(String.valueOf(player.getLevel()));
        hpStat.setText(String.valueOf(player.getHp()));
        xpStat.setText(String.valueOf(player.getXp()));
        goldStat.setText(String.valueOf(player.getGold()));
    }

    private void updatePicture() {
        try {
            String imagePath = "/dungeon" + player.getCurrentDungeonID() + ".jpg";
            java.net.URL imageURL = getClass().getResource(imagePath);
            if (imageURL == null) {
                image.setIcon(null);
                log("Image not found: " + imagePath + "\n");
            } else {
                ImageIcon icon = new ImageIcon(imageURL);
                SwingUtilities.invokeLater(() -> {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    if (width > 0 && height > 0) {
                        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        image.setIcon(new ImageIcon(scaledImage));
                    } else {
                        image.setIcon(icon);
                    }
                });
            }
        } catch (Exception e) {
            log("Error loading image: " + e.getMessage() + "\n");
            image.setIcon(null);
        }
    }

    private void checkFinished() {
        if (player.isVictorious()) {
            log("Congratulations, you have completed all 5 dungeons!\n");
            up.setEnabled(false);
            down.setEnabled(false);
            left.setEnabled(false);
            right.setEnabled(false);
            help.setEnabled(false);
        }
    }

    private void log(String message) {
        log.append(message);
        log.setCaretPosition(log.getDocument().getLength());
        logger.info(message);
    }

    private void logFileOnly(String message) {
        logger.info(message);
    }

    private void addHoverEffect(JButton button, Color normal, Color hover) {
        button.setBackground(normal);
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normal);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main window = new Main();
            window.setVisible(true);
        });
    }
}
