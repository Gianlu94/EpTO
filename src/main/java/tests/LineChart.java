package tests;

import application.Utils;
import com.opencsv.CSVWriter;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by gianluke on 21/09/17.
 */
public class LineChart extends JFrame {

	public LineChart(String title, String axisX, String axisY, String [] legendToDisplay, String pathToCsv){
		super(title);
		XYDataset dataset = createDataset(pathToCsv);
		JFreeChart chart = createChart(title,axisX,axisY,legendToDisplay,dataset);
		//chart.addLegend(new LegendTitle());
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}

	private static XYDataset createDataset(String pathToCsv){
		XYSeries series = new XYSeries("First");
		XYSeriesCollection dataset = new XYSeriesCollection();

		series = Utils.fillSeries(pathToCsv);
		dataset.addSeries(series);
		return dataset;
	}

	private static JFreeChart createChart (String title, String axisX, String axisY, String [] legendToDisplay,
	                                       XYDataset dataset){
		// create the chart
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, // chart title
				axisX, // x axis label
				axisY, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
		);

		LegendItemCollection chartLegend = new LegendItemCollection();
		Shape boxLegend = new Rectangle(10,10);
		for (String legendItem : legendToDisplay){
			chartLegend.add(new LegendItem(legendItem, null, null, null, boxLegend, Color.black));
			//chartLegend.add(new LegendItem(legendItem, null, null, null, boxLegend, Color.BLACK));
		}


		XYPlot plot = chart.getXYPlot();
		plot.setFixedLegendItems(chartLegend);

		return chart;
	}
}
