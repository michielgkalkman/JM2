/*
 * jMemorize - Learning made easy (and fun) - A Leitner flashcards tool
 * Copyright(C) 2004-2008 Riad Djemili and contributors
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package jmemorize.gui.swing.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import jmemorize.core.Card;
import jmemorize.core.Category;
import jmemorize.core.CategoryObserver;
import jmemorize.core.LC;
import jmemorize.core.Localization;
import jmemorize.gui.swing.ColorConstants;
import jmemorize.gui.swing.frames.MainFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

/**
 * This is the panel that is being displayed up the upper part of jMemorize all
 * of the time. It shows a visual representation of the current card
 * distribution among all decks in form of a 3D stacked bar chart.
 * 
 * @author djemili
 */
public class DeckChartPanel extends JPanel implements CategoryObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1362811549813961772L;

	/**
	 * A mouse listener for clicks on the chart. If a bar is clicked the view
	 * changes to the selected deck.
	 */
	private class MouseClicked implements ChartMouseListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jfree.chart.ChartMouseListener
		 */
		@Override
		public void chartMouseClicked(final ChartMouseEvent evt) {
			final ChartEntity entity = evt.getEntity();
			if (entity instanceof CategoryItemEntity) {
				final int cat = ((CategoryItemEntity) entity)
						.getCategoryIndex();
				m_frame.setDeck(cat - 1);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jfree.chart.ChartMouseListener
		 */
		@Override
		public void chartMouseMoved(final ChartMouseEvent arg0) {
			// do nothing
		}
	}

	private class MyBarRenderer extends StackedBarRenderer3D {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6637594343211110050L;
		private int m_deck = -2; // HACK
		private final Font m_defaultFont;
		private final Font m_boldFont;

		MyBarRenderer() {
			m_defaultFont = getBaseItemLabelFont();
			m_boldFont = getBaseItemLabelFont().deriveFont(Font.BOLD);
		}

		@Override
		public void drawItem(final Graphics2D g,
				final CategoryItemRendererState state,
				final Rectangle2D dataArea, final CategoryPlot plot,
				final CategoryAxis domainAxis, final ValueAxis rangeAxis,
				final CategoryDataset data, final int row, final int column,
				final int pass) {
			if (column - 1 == m_deck && m_category.getCards(m_deck).size() > 0) {
				setOutlinePaint(ColorConstants.SELECTION_COLOR, false);
				setBaseItemLabelFont(m_boldFont, false);
				setItemLabelFont(m_boldFont, false);
			} else {
				setOutlinePaint(Color.WHITE, false);
				setBaseItemLabelFont(m_defaultFont, false);
				setItemLabelFont(m_defaultFont, false);
			}

			// domainAxis.setCategoryMargin(0.2 + (0.011 * getMinNumDecks()));

			super.drawItem(g, state, dataArea, plot, domainAxis, rangeAxis,
					data, row, column, pass);
		}

		public void setSelectedDeck(final int level) {
			m_deck = level;

			notifyListeners(new RendererChangeEvent(this));
		}

		/*
		 * NOTE this is a workaround. JFreeChart appears to insist on drawing
		 * bars even if they are of zero width. To prevent the top of the
		 * stacked bar chart from being the wrong color, we need to skip bars
		 * that are zero width. Here, we intercept the draw call to remove any
		 * zero width blocks in the stack before drawing
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected void drawStackVertical(final List values,
				final Comparable category, final Graphics2D g2,
				final CategoryItemRendererState state,
				final Rectangle2D dataArea, final CategoryPlot plot,
				final CategoryAxis domainAxis, final ValueAxis rangeAxis,
				final CategoryDataset dataset) {
			final List<Object[]> prunedValues = new ArrayList<>(values);
			double lastValue = 0.0;

			final Iterator<Object[]> it = prunedValues.iterator();
			while (it.hasNext()) {
				final Object[] pair = it.next();
				final double thisValue = ((Double) pair[1]).doubleValue();
				final double delta = thisValue - lastValue;

				if (pair[0] != null) {
					if (delta == 0.0)
						it.remove();
				}

				lastValue = thisValue;
			}

			super.drawStackVertical(prunedValues, category, g2, state,
					dataArea, plot, domainAxis, rangeAxis, dataset);
		}
	}

	// TODO make minimum deck bars dependent on screen resolution

	private final static String DECK0_NAME = Localization
			.get("DeckChart.START_DECK"); //$NON-NLS-1$
	private final static String SUMMARY_BAR_NAME = Localization
			.get("DeckChart.SUMMARY"); //$NON-NLS-1$

	private final static String LEARNED_CARDS_ROW = Localization
			.get("DeckChart.LEARNED_CARDS");
	private final static String EXPIRED_CARDS_ROW = Localization
			.get("DeckChart.EXPIRED_CARDS");
	private final static String UNLEARNED_CARDS_ROW = Localization
			.get("DeckChart.UNLEARNED_CARDS");

	private Category m_category;

	private final MainFrame m_frame;

	private DefaultCategoryDataset m_dataset;
	private ChartPanel m_chartPanel;
	private MyBarRenderer m_barRenderer;

	public DeckChartPanel(final MainFrame mainFrame) {
		m_frame = mainFrame;

		initComponents();
	}

	public void setCategory(final Category category) {
		if (m_category != null) {
			m_category.removeObserver(this);
		}

		m_category = category;
		category.addObserver(this);

		createDataset();
	}

	public void setDeck(final int level) {
		m_barRenderer.setSelectedDeck(level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.CategoryObserver
	 */
	@Override
	public void onCategoryEvent(final int type, final Category category) {
		// ignore. mainframe already looks for important category changes
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jmemorize.core.CategoryObserver
	 */
	@Override
	public void onCardEvent(final int type, final Card card,
			final Category category, final int level) {
		updateBars();
	}

	private JFreeChart createChart() {
		m_dataset = createDefaultDataSet();

		final JFreeChart chart = ChartFactory.createStackedBarChart3D(null, // chart
																			// title
				null, // domain axis label
				Localization.get("DeckChart.CARDS"), // range axis label //$NON-NLS-1$
				m_dataset, // data
				PlotOrientation.VERTICAL, // the plot orientation
				true, // include legend
				true, // tooltips
				false // urls
				);

		// setup legend
		// TODO we used to do this for the old jfreechar version, but it's not
		// clear why.
		// can we get rid of it?
		final LegendTitle legend = chart.getLegend();
		// legend.setsetRenderingOrder(LegendRenderingOrder.REVERSE);
		legend.setItemFont(legend.getItemFont().deriveFont(11f));

		// setup plot
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		final TickUnitSource tickUnits = NumberAxis.createIntegerTickUnits();
		plot.getRangeAxis().setStandardTickUnits(tickUnits); // CHECK use locale
		plot.setForegroundAlpha(0.99f);

		// setup renderer
		m_barRenderer = new MyBarRenderer();
		m_barRenderer
				.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		m_barRenderer.setItemLabelsVisible(true);
		m_barRenderer.setPositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.CENTER, TextAnchor.CENTER));
		plot.setRenderer(m_barRenderer);
		setSeriesPaint();

		return chart;
	}

	private void createDataset() {
		m_dataset = createDefaultDataSet();
		updateBars();
		final CategoryPlot plot = (CategoryPlot) m_chartPanel.getChart()
				.getPlot();
		plot.setDataset(m_dataset);
	}

	private void initComponents() {
		// add the chart to a panel...
		m_chartPanel = new ChartPanel(createChart());
		m_chartPanel.addChartMouseListener(new MouseClicked());

		m_chartPanel.setMinimumDrawHeight(100);
		m_chartPanel.setMinimumDrawWidth(400);

		m_chartPanel.setMaximumDrawHeight(1600);
		m_chartPanel.setMaximumDrawWidth(10000);

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 2, 2, 2));
		add(m_chartPanel);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				if (m_category != null)
					updateBars();
			}
		});
	}

	private DefaultCategoryDataset createDefaultDataSet() {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		setValues(dataset, SUMMARY_BAR_NAME, 0, 0, 0);
		for (int i = 0; i < getMinNumDecks(); i++) {
			setValues(dataset, getDeckLabel(i), 0, 0, 0);
		}

		return dataset;
	}

	private void updateBars() // CHECK put Dataset as argument!?
	{
		updateSummaryBar();

		while (m_dataset.getColumnCount() > getNumDecks()) {
			m_dataset.removeColumn(m_dataset.getColumnCount() - 1);
		}

		for (int i = 0; i < getNumDecks() - 1; i++) {
			updateBar(i);
		}
	}

	private void updateSummaryBar() {
		final int learned = m_category.getLearnedCards().size();
		final int expired = m_category.getExpiredCards().size();
		final int unlearned = m_category.getUnlearnedCards().size();

		setValues(m_dataset, SUMMARY_BAR_NAME, unlearned, expired, learned);
	}

	private void updateBar(final int level) {
		if (level == 0) {
			final int unlearnedCards = m_category.getCards(level).size();
			setValues(m_dataset, DECK0_NAME, unlearnedCards, 0, 0);
		} else {
			final String deckLabel = getDeckLabel(level);
			if (level >= m_category.getNumberOfDecks()) {
				setValues(m_dataset, deckLabel, 0, 0, 0);
			} else {
				final int learnedCards = m_category.getLearnedCards(level)
						.size();
				final int expiredCards = m_category.getExpiredCards(level)
						.size();

				setValues(m_dataset, deckLabel, 0, expiredCards, learnedCards);
			}
		}
	}

	/**
	 * Sets the values for the column with given id. This method also handles
	 * the order in which the rows will appear in the column.
	 */
	private void setValues(final DefaultCategoryDataset dataset,
			final String column, final int unlearned, final int expired,
			final int learned) {
		// if you change the order of the rows, don't forget to also update the
		// method setSeriesPaint

		dataset.setValue(unlearned, UNLEARNED_CARDS_ROW, column);
		dataset.setValue(expired, EXPIRED_CARDS_ROW, column);
		dataset.setValue(learned, LEARNED_CARDS_ROW, column);
	}

	private String getDeckLabel(final int level) {
		return (level == 0) ? DECK0_NAME : Localization.get(LC.DECK)
				+ " " + level; //$NON-NLS-1$
	}

	private int getNumDecks() {
		return Math.max(m_category.getNumberOfDecks(), getMinNumDecks()) + 1;
	}

	/**
	 * The minimal amount of deck bars show at all time exclusive the summary
	 * bar.
	 */
	private int getMinNumDecks() {
		final int width = getSize().width - 250;
		return width / 135;
	}

	private void setSeriesPaint() {
		m_barRenderer.setSeriesPaint(0, ColorConstants.UNLEARNED_CARDS);
		m_barRenderer.setSeriesPaint(1, ColorConstants.EXPIRED_CARDS);
		m_barRenderer.setSeriesPaint(2, ColorConstants.LEARNED_CARDS);
	}
}
