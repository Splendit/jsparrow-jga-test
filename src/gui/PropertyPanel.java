/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    PropertyPanel.java
 *    Copyright (C) 1999-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import core.OptionHandler;
import core.Utils;
import gui.GenericObjectEditorHistory.HistorySelectionEvent;
import gui.GenericObjectEditorHistory.HistorySelectionListener;

/**
 * Support for drawing a property value in a component.
 * 
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision$
 */
@SuppressWarnings("rawtypes")
public class PropertyPanel extends JPanel {

	/** for serialization */
	static final long serialVersionUID = 5370025273466728904L;

	/** The property editor */
	private final PropertyEditor mEditor;

	/** The currently displayed property dialog, if any */
	private PropertyDialog mPd;

	/** Whether the editor has provided its own panel */
	private boolean mHasCustomPanel = false;

	/** The custom panel (if any) */
	private JPanel mCustomPanel;

	/**
	 * Create the panel with the supplied property editor.
	 * 
	 * @param pe the PropertyEditor
	 */
	public PropertyPanel(PropertyEditor pe) {

		this(pe, false);
	}

	/**
	 * Create the panel with the supplied property editor, optionally ignoring any
	 * custom panel the editor can provide.
	 * 
	 * @param pe                the PropertyEditor
	 * @param ignoreCustomPanel whether to make use of any available custom panel
	 */
	public PropertyPanel(PropertyEditor pe, boolean ignoreCustomPanel) {

		mEditor = pe;

		if (!ignoreCustomPanel && mEditor instanceof CustomPanelSupplier) {
			setLayout(new BorderLayout());
			mCustomPanel = ((CustomPanelSupplier) mEditor).getCustomPanel();
			add(mCustomPanel, BorderLayout.CENTER);
			mHasCustomPanel = true;
		} else {
			createDefaultPanel();
		}
	}

	/**
	 * Creates the default style of panel for editors that do not supply their own.
	 */
	protected void createDefaultPanel() {

		setBorder(BorderFactory.createEtchedBorder());
		setToolTipText("Left-click to edit properties for this object, right-click/Alt+Shift+left-click for menu");
		setOpaque(true);
		final Component comp = this;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					if ((evt.getButton() == MouseEvent.BUTTON1) && !evt.isAltDown() && !evt.isShiftDown()) {
						showPropertyDialog();
					} else if ((evt.getButton() == MouseEvent.BUTTON3)
							|| ((evt.getButton() == MouseEvent.BUTTON1) && evt.isAltDown() && evt.isShiftDown())) {
						JPopupMenu menu = new JPopupMenu();
						JMenuItem item;

						if (mEditor.getValue() != null) {
							item = new JMenuItem("Show properties...");
							item.addActionListener((ActionEvent e) -> showPropertyDialog());
							menu.add(item);

							item = new JMenuItem("Copy configuration to clipboard");
							item.addActionListener((ActionEvent e) -> {
								Object value = mEditor.getValue();
								String str = "";
								if (value.getClass().isArray()) {
									str += value.getClass().getName();
									Object[] arr = (Object[]) value;
									for (Object v : arr) {
										String s = v.getClass().getName();
										if (v instanceof OptionHandler) {
											s += " " + Utils.joinOptions(((OptionHandler) v).getOptions());
										}
										str += " \"" + Utils.backQuoteChars(s.trim()) + "\"";
									}
								} else {
									str += value.getClass().getName();
									if (value instanceof OptionHandler) {
										str += " " + Utils.joinOptions(((OptionHandler) value).getOptions());
									}
								}
								StringSelection selection = new StringSelection(str.trim());
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(selection, selection);
							});
							menu.add(item);
						}

						item = new JMenuItem("Enter configuration...");
						item.addActionListener((ActionEvent e) -> {
							String str = JOptionPane.showInputDialog(comp, "Configuration (<classname> [<options>])");
							if (str != null && str.length() > 0) {
								try {
									String[] options = Utils.splitOptions(str);
									String classname = options[0];
									options[0] = "";
									Class c = Utils.forName(Object.class, classname, null).getClass();
									if (c.isArray()) {
										Object[] arr = (Object[]) Array.newInstance(c.getComponentType(),
												options.length - 1);
										for (int i = 1; i < options.length; i++) {
											String[] ops = Utils.splitOptions(options[i]);
											String cname = ops[0];
											ops[0] = "";
											arr[i - 1] = Utils.forName(Object.class, cname, ops);
										}
										mEditor.setValue(arr);
									} else {
										mEditor.setValue(Utils.forName(Object.class, classname, options));
									}
								} catch (Exception ex) {
									JOptionPane.showMessageDialog(comp, "Error parsing commandline:\n" + ex, "Error...",
											JOptionPane.ERROR_MESSAGE);
								}
							}
						});
						menu.add(item);

						if (mEditor.getValue() instanceof OptionHandler) {
							item = new JMenuItem("Edit configuration...");
							item.addActionListener((ActionEvent e) -> {
								String str = mEditor.getValue().getClass().getName();
								str += " " + Utils.joinOptions(((OptionHandler) mEditor.getValue()).getOptions());
								str = JOptionPane.showInputDialog(comp, "Configuration", str);
								if (str != null && str.length() > 0) {
									try {
										String[] options = Utils.splitOptions(str);
										String classname = options[0];
										options[0] = "";
										mEditor.setValue(Utils.forName(Object.class, classname, options));
									} catch (Exception ex) {
										JOptionPane.showMessageDialog(comp, "Error parsing commandline:\n" + ex,
												"Error...", JOptionPane.ERROR_MESSAGE);
									}
								}
							});
							menu.add(item);
						}

						if (mEditor instanceof GenericObjectEditor) {
							((GenericObjectEditor) mEditor).getHistory().customizePopupMenu(menu, mEditor.getValue(),
									(HistorySelectionEvent e) -> mEditor.setValue(e.getHistoryItem()));
						}

						menu.show(comp, evt.getX(), evt.getY());
					}
				}
			}
		});
		Dimension newPref = getPreferredSize();
		newPref.height = getFontMetrics(getFont()).getHeight() * 5 / 4;
		newPref.width = newPref.height * 5;
		setPreferredSize(newPref);

		mEditor.addPropertyChangeListener((PropertyChangeEvent evt) -> repaint());
	}

	/**
	 * Displays the property edit dialog for the panel.
	 */
	public void showPropertyDialog() {

		if (mEditor.getValue() != null) {
			if (mPd == null) {
				int x = getLocationOnScreen().x;
				int y = getLocationOnScreen().y;
				if (PropertyDialog.getParentDialog(this) != null) {
					mPd = new PropertyDialog(PropertyDialog.getParentDialog(this), mEditor, x, y);
				} else {
					mPd = new PropertyDialog(PropertyDialog.getParentFrame(this), mEditor, x, y);
				}
				mPd.setVisible(true);
			} else {
				mPd.setVisible(true);
			}
			// make sure that m_Backup is correctly initialized!
			mEditor.setValue(mEditor.getValue());
		}
	}

	/**
	 * Cleans up when the panel is destroyed.
	 */
	@Override
	public void removeNotify() {

		super.removeNotify();
		if (mPd != null) {
			mPd.dispose();
			mPd = null;
		}
	}

	/**
	 * Passes on enabled/disabled status to the custom panel (if one is set).
	 * 
	 * @param enabled true if this panel (and the custom panel is enabled)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (mHasCustomPanel) {
			mCustomPanel.setEnabled(enabled);
		}

	}

	/**
	 * Paints the component, using the property editor's paint method.
	 * 
	 * @param g the current graphics context
	 */
	@Override
	public void paintComponent(Graphics g) {

		if (!mHasCustomPanel) {
			Insets i = getInsets();
			Rectangle box = new Rectangle(i.left, i.top, getSize().width - i.left - i.right - 1,
					getSize().height - i.top - i.bottom - 1);

			g.clearRect(i.left, i.top, getSize().width - i.right - i.left, getSize().height - i.bottom - i.top);
			mEditor.paintValue(g, box);
		}
	}

	/**
	 * Adds the current editor value to the history.
	 * 
	 * @return true if successfully added (i.e., if editor is a GOE)
	 */
	public boolean addToHistory() {
		return addToHistory(mEditor.getValue());
	}

	/**
	 * Adds the specified value to the history.
	 * 
	 * @param obj the object to add to the history
	 * @return true if successfully added (i.e., if editor is a GOE)
	 */
	public boolean addToHistory(Object obj) {
		if ((mEditor instanceof GenericObjectEditor) && (obj != null)) {
			((GenericObjectEditor) mEditor).getHistory().add(obj);
			return true;
		}

		return false;
	}
}
