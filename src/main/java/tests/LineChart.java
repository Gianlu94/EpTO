package tests;

import application.Utils;
import com.opencsv.CSVWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;

/**
 * Created by gianluke on 21/09/17.
 */
public class LineChart extends JFrame {

	public LineChart(String title, String axisX, String axisY, String pathToCsv){
		super(title);
		XYDataset dataset = createDataset(pathToCsv);
		JFreeChart chart = createChart(title,axisX,axisY,dataset);
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

	private static JFreeChart createChart (String title, String axisX, String axisY, XYDataset dataset){
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

		return chart;
	}
}
