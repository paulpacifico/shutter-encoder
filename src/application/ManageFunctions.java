/*******************************************************************************************
* Copyright (C) 2026 PACIFICO PAUL
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
********************************************************************************************/

package application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ManageFunctions {

	public static JDialog frame;
	
    private final boolean[] sectionMarkers;
    private final Map<String, Boolean> sectionStates = new LinkedHashMap<>();
    private final Map<String, List<JCheckBox>> sectionCheckboxes = new LinkedHashMap<>();
    private final List<JCheckBox> allCheckboxes = new ArrayList<>();
    public static String[] selectedFunctions;
    public static JCheckBox selectAll = new JCheckBox(Shutter.language.getProperty("setAll"), true);

    private JPanel listPanel;

    public ManageFunctions() {
    	
        boolean[] markers = new boolean[Shutter.functionsList.length];
        for (int i = 0; i < Shutter.functionsList.length; i++) {
        	markers[i] = Shutter.functionsList[i].contains(":");
        }
        
        sectionMarkers = markers;
       
        frame = new JDialog(Shutter.frame, true);
        frame.setTitle(Shutter.language.getProperty("grpChooseFunction"));
        frame.setBackground(Utils.bg32);
        frame.setSize(420, 660);
        frame.setMinimumSize(new Dimension(420, 480));
        frame.setLocationRelativeTo(Shutter.frame);
        frame.setModal(true);
        
        if (System.getProperty("os.name").contains("Mac") == false)
			frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("contents/icon.png")).getImage());
        
        setComponents();
        
        frame.setVisible(true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void setComponents() {
    	
        JPanel root = new JPanel(new BorderLayout());
        frame.setContentPane(root);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
        listPanel.setBackground(Utils.bg32);
        setList();

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);
        
        selectAll.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        selectAll.addActionListener(e -> {
            for (JCheckBox cb : allCheckboxes) cb.setSelected(selectAll.isSelected());
            listPanel.repaint();
        });
        
        JButton apply = new JButton(Shutter.language.getProperty("btnApply"));
        apply.setFont(new Font(Shutter.boldFont, Font.PLAIN, 12));
        apply.addActionListener(e -> {
        	
            List<String> selected = new ArrayList<>();
            
            for (Map.Entry<String, List<JCheckBox>> entry : sectionCheckboxes.entrySet())
            {
                List<JCheckBox> group = entry.getValue();
                boolean anyChecked = group.stream().anyMatch(JCheckBox::isSelected);
                
                if (anyChecked && !entry.getKey().isEmpty())
                	selected.add("- " + entry.getKey() + Shutter.language.getProperty("colon"));
                
                for (JCheckBox cb : group)
                {
                	if (cb.isSelected())
                		selected.add(cb.getText());
                }
            }
            
            selected.add("- " + Shutter.language.getProperty("btnManage").toUpperCase() + " -");
            
            selectedFunctions = selected.toArray(new String[0]);
            
            if (selectedFunctions != null)
            {
            	Shutter.comboFonctions.setModel(new DefaultComboBoxModel(selectedFunctions));
            	frame.dispose();
            }
            
        });
        
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Utils.bg32);
        bar.setBorder(new EmptyBorder(10, 10, 10, 10));
        bar.add(selectAll, BorderLayout.WEST);
        bar.add(apply, BorderLayout.CENTER);
        root.add(bar, BorderLayout.SOUTH);
    }

    private void setList() {
    	
        listPanel.removeAll();
        sectionCheckboxes.clear();
        allCheckboxes.clear();

        String currentSection = "";
        List<JCheckBox> currentGroup = new ArrayList<>();
        sectionCheckboxes.put(currentSection, currentGroup);
        sectionStates.putIfAbsent(currentSection, false);

        for (int i = 0; i < Shutter.functionsList.length; i++)
        {
            String item = Shutter.functionsList[i];
            boolean isSection = sectionMarkers != null && i < sectionMarkers.length && sectionMarkers[i];

            if (isSection)
            {
                currentSection = item.replace("- ", "").replace(Shutter.language.getProperty("colon"), "");
                currentGroup = new ArrayList<>();
                sectionCheckboxes.put(currentSection, currentGroup);
                sectionStates.putIfAbsent(currentSection, false);
                listPanel.add(setRow(currentSection, currentGroup));
            }
            else
            {
            	if (item.contains(Shutter.language.getProperty("btnManage").toUpperCase()) == false)
            	{            		
            		boolean isSelected = selectedFunctions == null || Arrays.asList(selectedFunctions).contains(item);
            		JCheckBox cb = new JCheckBox(item, isSelected);            		
                    cb.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));
                    
                    cb.addActionListener(e -> {
                        if (!cb.isSelected()) {
                            selectAll.setSelected(false);
                        }
                    });
                    
                    currentGroup.add(cb);
                    allCheckboxes.add(cb);

                    JPanel row = new JPanel(new BorderLayout());
                    row.setOpaque(false);
                    row.setBorder(new EmptyBorder(1, 44, 1, 12));
                    row.setName("item__" + currentSection);
                    row.setVisible(!Boolean.TRUE.equals(sectionStates.get(currentSection)));
                    row.add(cb, BorderLayout.WEST);
                    listPanel.add(row);
            	}                
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel setRow(String sectionLabel, List<JCheckBox> group) {
    	
        JLabel arrow = new JLabel(Boolean.TRUE.equals(sectionStates.get(sectionLabel)) ? "▶" : "▼");
        arrow.setFont(new Font(Shutter.mainFont, Font.PLAIN, 12));

        JLabel label = new JLabel(sectionLabel);
        label.setForeground(Utils.themeColor);
        label.setFont(new Font(Shutter.mainFont, Font.BOLD, 13));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));        
        left.setOpaque(false);
        left.add(arrow);
        left.add(label);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
                new EmptyBorder(6, 12, 6, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setBackground(Utils.bg32);
        row.add(left, BorderLayout.WEST);

        MouseAdapter toggle = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                boolean collapsed = !Boolean.TRUE.equals(sectionStates.get(sectionLabel));
                sectionStates.put(sectionLabel, collapsed);
                arrow.setText(collapsed ? "▶" : "▼");

                List<JPanel> rows = new ArrayList<>();
                for (Component c : listPanel.getComponents())
                    if (("item__" + sectionLabel).equals(c.getName()))
                        rows.add((JPanel) c);

                if (collapsed) {
                    // Measure full height before animating to zero
                    for (JPanel r : rows) r.setVisible(true);
                    listPanel.revalidate();
                    int fullH = rows.isEmpty() ? 0 : rows.get(0).getPreferredSize().height;
                    int[] cur = { fullH };
                    javax.swing.Timer t = new javax.swing.Timer(12, null);
                    t.addActionListener(tick -> {
                        cur[0] = Math.max(0, cur[0] - Math.max(2, cur[0] / 4));
                        for (JPanel r : rows) {
                            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, cur[0]));
                            r.setPreferredSize(new Dimension(r.getWidth(), cur[0]));
                        }
                        listPanel.revalidate();
                        listPanel.repaint();
                        if (cur[0] == 0) { t.stop(); for (JPanel r : rows) r.setVisible(false); }
                    });
                    t.start();
                } else {
                    for (JPanel r : rows) {
                        r.setMaximumSize(null);
                        r.setPreferredSize(null);
                        r.setVisible(true);
                    }
                    listPanel.revalidate();
                    int fullH = rows.isEmpty() ? 0 : rows.get(0).getPreferredSize().height;
                    int[] cur = { 0 };
                    javax.swing.Timer t = new javax.swing.Timer(12, null);
                    t.addActionListener(tick -> {
                        cur[0] = Math.min(fullH, cur[0] + Math.max(2, (fullH - cur[0]) / 4));
                        for (JPanel r : rows) {
                            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, cur[0]));
                            r.setPreferredSize(new Dimension(r.getWidth(), cur[0]));
                        }
                        listPanel.revalidate();
                        listPanel.repaint();
                        if (cur[0] >= fullH) { t.stop(); for (JPanel r : rows) { r.setMaximumSize(null); r.setPreferredSize(null); } }
                    });
                    t.start();
                }
            }
        };
        row.addMouseListener(toggle);
        left.addMouseListener(toggle);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return row;
    }

}