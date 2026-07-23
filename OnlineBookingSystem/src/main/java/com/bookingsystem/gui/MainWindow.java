package com.bookingsystem.gui;

import com.bookingsystem.gui.components.*;
import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.service.ReservationService;

import javax.swing.*;
import java.awt.*;

/**
 * Application shell: builds the menu bar and the tabbed workspace, and
 * wires every panel to the shared {@link ReservationService} so an action
 * in one tab (booking a reservation, adding a customer, saving business
 * hours) is immediately reflected everywhere else.
 */
public class MainWindow extends JFrame implements LanguageManager.LanguageChangeListener,
        ThemeManager.ThemeChangeListener {

    private final ReservationService reservationService = new ReservationService();

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu languageMenu;
    private JMenu themeMenu;
    private JMenu helpMenu;

    private JMenuItem saveItem;
    private JMenuItem exportItem;
    private JMenuItem exitItem;
    private JMenuItem aboutItem;

    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private NewReservationPanel newReservationPanel;
    private ReservationsPanel reservationsPanel;
    private ServicesPanel servicesPanel;
    private CustomersPanel customersPanel;
    private BusinessHoursPanel businessHoursPanel;

    private JLabel statusBar;

    public MainWindow() {
        LanguageManager.getInstance().addListener(this);
        ThemeManager.getInstance().addListener(this);

        initComponents();
        applyLabels();
        applyOrientation(LanguageManager.getInstance().getCurrentLanguage());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                reservationService.save();
                System.exit(0);
            }
        });
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(920, 600));
    }

    private void initComponents() {
        dashboardPanel = new DashboardPanel(reservationService);
        newReservationPanel = new NewReservationPanel(reservationService, this::setStatus, this::onDataChanged);
        reservationsPanel = new ReservationsPanel(reservationService, this::setStatus);
        servicesPanel = new ServicesPanel(reservationService, this::setStatus, this::onDataChanged);
        customersPanel = new CustomersPanel(reservationService, this::setStatus, this::onDataChanged);
        businessHoursPanel = new BusinessHoursPanel(reservationService, this::setStatus, this::onDataChanged);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("", dashboardPanel);
        tabbedPane.addTab("", newReservationPanel);
        tabbedPane.addTab("", reservationsPanel);
        tabbedPane.addTab("", servicesPanel);
        tabbedPane.addTab("", customersPanel);
        tabbedPane.addTab("", businessHoursPanel);

        statusBar = new JLabel(" ");
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JPanel content = new JPanel(new BorderLayout());
        content.add(tabbedPane, BorderLayout.CENTER);
        content.add(statusBar, BorderLayout.SOUTH);
        setContentPane(content);

        buildMenuBar();
        setJMenuBar(menuBar);
    }

    private void onDataChanged() {
        newReservationPanel.refreshServiceCombo();
        newReservationPanel.refreshCustomerCombo();
        reservationsPanel.refresh();
        dashboardPanel.refresh();
        reservationService.save();
    }

    private void setStatus(String text) {
        statusBar.setText(text);
    }

    private void buildMenuBar() {
        menuBar = new JMenuBar();

        fileMenu = new JMenu();
        saveItem = new JMenuItem();
        saveItem.addActionListener(e -> {
            reservationService.save();
            setStatus(LanguageManager.getInstance().t("status.dataSaved"));
        });
        exportItem = new JMenuItem();
        exportItem.addActionListener(e -> tabbedPane.setSelectedComponent(reservationsPanel));
        exitItem = new JMenuItem();
        exitItem.addActionListener(e -> {
            reservationService.save();
            System.exit(0);
        });
        fileMenu.add(saveItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        languageMenu = new JMenu();
        ButtonGroup languageGroup = new ButtonGroup();
        for (Language lang : Language.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(lang.getDisplayName());
            item.setSelected(lang == LanguageManager.getInstance().getCurrentLanguage());
            item.addActionListener(e -> LanguageManager.getInstance().setLanguage(lang));
            languageGroup.add(item);
            languageMenu.add(item);
        }

        themeMenu = new JMenu();
        ButtonGroup themeGroup = new ButtonGroup();
        for (Theme theme : Theme.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem();
            item.setSelected(theme == ThemeManager.getInstance().getCurrentTheme());
            item.addActionListener(e -> ThemeManager.getInstance().applyTheme(theme));
            item.putClientProperty("themeRef", theme);
            themeGroup.add(item);
            themeMenu.add(item);
        }

        helpMenu = new JMenu();
        aboutItem = new JMenuItem();
        aboutItem.addActionListener(e -> onAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(languageMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);
    }

    private void onAbout() {
        LanguageManager lm = LanguageManager.getInstance();
        JOptionPane.showMessageDialog(this, lm.t("dialog.aboutText"), lm.t("dialog.aboutTitle"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        setTitle(lm.t("app.title"));

        fileMenu.setText(lm.t("menu.file"));
        saveItem.setText(lm.t("menu.file.save"));
        exportItem.setText(lm.t("menu.file.exportCsv"));
        exitItem.setText(lm.t("menu.file.exit"));

        languageMenu.setText(lm.t("menu.language"));
        themeMenu.setText(lm.t("menu.theme"));
        for (Component c : themeMenu.getMenuComponents()) {
            if (c instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem item = (JRadioButtonMenuItem) c;
                Theme theme = (Theme) item.getClientProperty("themeRef");
                if (theme != null) {
                    item.setText(lm.t(theme.getLabelKey()));
                }
            }
        }

        helpMenu.setText(lm.t("menu.help"));
        aboutItem.setText(lm.t("menu.help.about"));

        tabbedPane.setTitleAt(0, lm.t("tab.dashboard"));
        tabbedPane.setTitleAt(1, lm.t("tab.newBooking"));
        tabbedPane.setTitleAt(2, lm.t("tab.reservations"));
        tabbedPane.setTitleAt(3, lm.t("tab.services"));
        tabbedPane.setTitleAt(4, lm.t("tab.customers"));
        tabbedPane.setTitleAt(5, lm.t("tab.hours"));

        setStatus(lm.t("status.ready"));
    }

    private void applyOrientation(Language language) {
        ComponentOrientation orientation = language.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT;
        applyComponentOrientation(orientation);
        if (menuBar != null) {
            menuBar.applyComponentOrientation(orientation);
        }
        tabbedPane.applyComponentOrientation(orientation);
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void onLanguageChanged(Language newLanguage) {
        applyLabels();
        applyOrientation(newLanguage);
    }

    @Override
    public void onThemeChanged(Theme newTheme) {
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
}
