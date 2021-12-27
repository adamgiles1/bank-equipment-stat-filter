package bankequipmentstatfilter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Singleton
@Slf4j
public class BankEquipmentStatFilterPanel extends PluginPanel
{
    private static final int COLUMN_SIZE = 5;
    private static final int ICON_WIDTH = 36;
    private static final int ICON_HEIGHT = 32;

    @Inject
    ItemManager itemManager;

    @Inject
    BankEquipmentStatFilterPlugin plugin;

    JPanel itemsPanel;

    JComboBox<EquipmentStat> statDropDown;

    JComboBox<EquipmentInventorySlot> slotDropDown;

    BankEquipmentStatFilterPanel()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        selectionPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        selectionPanel.setLayout(new GridLayout(3, 2));

        // Select Stat
        JLabel statLabel = new JLabel();
        statLabel.setText("Stat for equipment: ");
        selectionPanel.add(statLabel);

        statDropDown = new JComboBox<>(EquipmentStat.values());
        statDropDown.setFocusable(false);
        selectionPanel.add(statDropDown);

        // Select Slot
        JLabel slotLabel = new JLabel();
        slotLabel.setText("Slot for equipment: ");
        selectionPanel.add(slotLabel);

        slotDropDown = new JComboBox<>(EquipmentInventorySlot.values());
        slotDropDown.setFocusable(false);
        selectionPanel.add(slotDropDown);

        selectionPanel.add(new JLabel());

        // Button to search bank
        JButton filterButton = new JButton();
        filterButton.addActionListener((actionEvent) ->
        {
            plugin.bankFilter((EquipmentInventorySlot) slotDropDown.getSelectedItem(), (EquipmentStat) statDropDown.getSelectedItem());
            repaint();
        });
        filterButton.setText("View Items");
        filterButton.setFocusable(false);
        selectionPanel.add(filterButton);

        add(selectionPanel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(itemsPanel, BorderLayout.SOUTH);
    }

    public void displayItems(ItemWithStat[] items, EquipmentStat statType)
    {
        itemsPanel.removeAll();

        final int rowCorrection = items.length % COLUMN_SIZE > 0 ? 1 : 0;
        final int rowSize = items.length/COLUMN_SIZE + rowCorrection;
        itemsPanel.setLayout(new GridLayout(rowSize, COLUMN_SIZE, 1, 1));

        if (items.length > 0)
        {
            for (ItemWithStat item : items) {
                JPanel itemPanel = new JPanel();
                JLabel itemLabel = new JLabel();

                itemLabel.setHorizontalAlignment(SwingConstants.CENTER);
                itemLabel.setVerticalAlignment(SwingConstants.CENTER);
                AsyncBufferedImage icon = itemManager.getImage(item.getId());
                icon.addTo(itemLabel);
                itemLabel.setSize(icon.getWidth(), icon.getHeight());
                itemLabel.setMaximumSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
                itemLabel.setToolTipText(String.format("%s (+%s)", item.getName(), plugin.getItemStat(item.getStats(), statType)));

                itemPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

                itemPanel.add(itemLabel);
                itemsPanel.add(itemPanel);
            }

            padItemsPanel((rowSize * COLUMN_SIZE) % items.length);
        } else
        {
            displayMessage("No matching items found");
        }

        repaint();
        revalidate();
    }

    public void displayMessage(final String message)
    {
        itemsPanel.removeAll();
        itemsPanel.setLayout(new BorderLayout());

        final JTextArea textArea = new JTextArea();
        textArea.setText(message);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
        itemsPanel.add(textArea);

        repaint();
        revalidate();
    }

    private void padItemsPanel(final int padAmt)
    {
        for (int i = 0; i < padAmt; i++)
        {
            JPanel panel = new JPanel();
            panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            itemsPanel.add(panel);
        }
    }
}
