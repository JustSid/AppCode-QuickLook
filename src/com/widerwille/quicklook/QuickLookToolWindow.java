package com.widerwille.quicklook;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import org.intellij.images.editor.impl.ImageEditorManagerImpl;
import org.intellij.images.thumbnail.actionSystem.ThumbnailViewActions;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class QuickLookToolWindow
{
	private Project project;
	private ToolWindow toolWindow;
	private QuickLookManager manager;
	private QuickLookThumbnailCellRenderer cellRenderer;

	private JPanel content;
	private JList list;
	private JScrollPane scrollPane;

	private static String TOOLWINDOW_ID = "com.widerwille.quicklook.toolwindow";
	private static Icon icon = IconLoader.getIcon("/icons/quick-look.png");

	public QuickLookToolWindow(Project project, QuickLookManager manager)
	{
		this.project = project;
		this.manager = manager;

		ToolWindowManager windowManager = ToolWindowManager.getInstance(project);

		ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
		Content content = contentFactory.createContent(this.content, "", false);

		toolWindow = windowManager.registerToolWindow(TOOLWINDOW_ID, false, ToolWindowAnchor.LEFT, true);
		toolWindow.setIcon(icon);
		toolWindow.setStripeTitle("Quick Look");
		toolWindow.getContentManager().addContent(content);

		cellRenderer = new QuickLookThumbnailCellRenderer();

		list.setCellRenderer(cellRenderer);
		list.setVisibleRowCount(-1);
		list.setFixedCellWidth(76);
		list.setFixedCellHeight(96);

		scrollPane.setBorder(IdeBorderFactory.createBorder(SideBorder.TOP));

		ThumbnailsMouseAdapter mouseListener = new ThumbnailsMouseAdapter();
		list.addMouseListener(mouseListener);
	}

	public void dispose()
	{
		ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
		windowManager.unregisterToolWindow(TOOLWINDOW_ID);
	}

	public void setVisible(boolean visible)
	{
		toolWindow.setAvailable(visible, null);
	}


	public void refresh(ArrayList<QuickLookValueRenderer> renderers)
	{
		if(list != null)
		{
			DefaultListModel model = (DefaultListModel)list.getModel();
			model.clear();
			model.ensureCapacity(renderers.size());

			for(QuickLookValueRenderer renderer : renderers)
				model.addElement(renderer);
		}
	}

	private final class ThumbnailsMouseAdapter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			Point point = e.getPoint();
			int index = list.locationToIndex(point);
			if(index != -1)
			{
				Rectangle cellBounds = list.getCellBounds(index, index);
				if(!cellBounds.contains(point) && (KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK)
				{
					list.clearSelection();
					e.consume();
				}
			}
		}

		public void mouseClicked(MouseEvent e)
		{
			Point point = e.getPoint();
			int index = list.locationToIndex(point);

			if(index != -1)
			{
				Rectangle cellBounds = list.getCellBounds(index, index);
				if(!cellBounds.contains(point) && (KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK)
				{
					index = -1;
					list.clearSelection();
				}

				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				{
					DefaultListModel model = (DefaultListModel)list.getModel();
					QuickLookValueRenderer renderer = (QuickLookValueRenderer)model.get(index);
					BufferedImage image = renderer.getImageContent();

					if(image != null)
					{
						final JComponent comp = ImageEditorManagerImpl.createImageEditorUI(image);
						JBPopup popup = DebuggerUIUtil.createValuePopup(project, comp, null);
						JFrame frame = WindowManager.getInstance().getFrame(project);
						Dimension frameSize = frame.getSize();
						Dimension size = new Dimension(frameSize.width / 2, frameSize.height / 2);
						popup.setSize(size);

						if(comp instanceof Disposable)
							Disposer.register(popup, (Disposable) comp);

						popup.show(new RelativePoint(frame, new Point(size.width / 2, size.height / 2)));
					}
				}

				if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1)
				{
					if((KeyEvent.CTRL_DOWN_MASK & e.getModifiersEx()) != KeyEvent.CTRL_DOWN_MASK)
					{
						list.setSelectedIndex(index);
					}
					else
					{
						list.getSelectionModel().addSelectionInterval(index, index);
					}

					ActionManager actionManager = ActionManager.getInstance();
					ActionGroup actionGroup = (ActionGroup) actionManager.getAction(ThumbnailViewActions.GROUP_POPUP);
					ActionPopupMenu menu = actionManager.createActionPopupMenu(ThumbnailViewActions.ACTION_PLACE, actionGroup);
					JPopupMenu popupMenu = menu.getComponent();
					popupMenu.pack();
					popupMenu.show(e.getComponent(), e.getX(), e.getY());

					e.consume();
				}
			}
		}
	}
}
