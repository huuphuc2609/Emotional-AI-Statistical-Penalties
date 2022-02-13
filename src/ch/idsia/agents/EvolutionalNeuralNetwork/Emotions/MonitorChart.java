/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.idsia.agents.EvolutionalNeuralNetwork.Emotions;

/**
 *
 * @author Phuc
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javafx.scene.chart.XYChart;

import javax.swing.Timer;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.RelativeDateFormat;
import org.jfree.data.Range;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example to show how we can create a dynamic chart.
*/
public class MonitorChart extends ApplicationFrame {

    /** The time series data. */
    private TimeSeries timeSeries;
    
    /** The XY series data. */
    private XYSeries xySeries1;
    private XYSeries xySeries2;
    private XYSeries xySeries3;

    /** The most recent value added. */
    private double lastValue1 = 100.0;
    private double lastValue2 = 100.0;
    private double lastValue3 = 100.0;

    /** Timer to refresh graph after every 1/4th of a second */
    //private Timer timer = new Timer(250, this);

    private String chartName = "Untitle chart";
    private String xAxisName = "x";
    private String yAxisName = "y";
    private int chartPanelWidth = 800;
    private int chartPanelHeight = 500;
    
    private XYSeriesCollection datasetXY1;
    private XYSeriesCollection datasetXY2;
    private XYSeriesCollection datasetXY3;
    private JFreeChart chart;
    
    private ValueAxis xaxis;
    private ValueAxis yaxis;
    private double showTime = 60000.0; //Domain axis would show data of 60 seconds for a time
    
    public int monitorYAxixMin = -4000;
    public int monitorYAxixMax = 8000;
    
    /**
     * Constructs a new dynamic chart application.
     *
     * @param title  the frame title.
     */
    public MonitorChart(final String title) {
        super(title);
    }
    
    public void initializeChart()
    {
        this.xySeries1 = new XYSeries("Score");
        this.xySeries2 = new XYSeries("Penalty");
        this.xySeries3 = new XYSeries("Fitness");

        datasetXY1 = new XYSeriesCollection(this.xySeries1);
        datasetXY2 = new XYSeriesCollection(this.xySeries2);
        datasetXY3 = new XYSeriesCollection(this.xySeries3);
        chart = createMonitorChart(datasetXY1);

        //Sets background color of chart
        chart.setBackgroundPaint(Color.LIGHT_GRAY);

        //Created JPanel to show graph on screen
        final JPanel content = new JPanel(new BorderLayout());

        //Created Chartpanel for chart area
        final ChartPanel chartPanel = new ChartPanel(chart);

        //Added chartpanel to main panel
        content.add(chartPanel);

        //Sets the size of whole window (JPanel)
        chartPanel.setPreferredSize(new java.awt.Dimension(getChartPanelWidth(), getChartPanelHeight()));

        //Puts the whole content on a Frame
        setContentPane(content);
    }    
    
    
    public void setPreferredSize(int w, int h)
    {
        chartPanelWidth = w;
        chartPanelHeight = h;
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    
    private void firstMonitorSeries(final XYPlot plot) {

        ValueAxis domain = plot.getDomainAxis();
        domain.setAutoRange(true);
        //domain.setRange(0, 1500);
        domain.setVerticalTickLabels(true);
        
        final ValueAxis yaxis = plot.getRangeAxis();
        //yaxis.setRange(-4000, 8000);
        yaxis.setAutoRange(true);
        
        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.GREEN);

        final NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
        yAxis1.setTickLabelPaint(Color.BLACK);
    }
    
    private void secondMonitorSeries(final XYPlot plot) {
        plot.setDataset(1, datasetXY2); // the second dataset (datasets are zero-based numbering)
        plot.mapDatasetToDomainAxis(1, 0); // same axis, different dataset
        plot.mapDatasetToRangeAxis(1, 0); // same axis, different dataset

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, Color.RED);
        plot.setRenderer(1, renderer);
       
    }

    private void thirdMonitorSeries(final XYPlot plot) {
        plot.setDataset(2, datasetXY3); // the third dataset (datasets are zero-based numbering)
        plot.mapDatasetToDomainAxis(2, 0); // same axis, different dataset
        plot.mapDatasetToRangeAxis(2, 0); // same axis, different dataset

        final XYItemRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(2, renderer);
    }
    
    private JFreeChart createMonitorChart(final XYDataset dataset) {
  
//        final JFreeChart result = ChartFactory.createTimeSeriesChart(
//            chartName,
//            xAxisName,
//            yAxisName,
//            datasetXY1,
//            true,
//            true,
//            false
//        );
        
        final JFreeChart result = ChartFactory.createXYLineChart(
            chartName, xAxisName, yAxisName, datasetXY1,
            PlotOrientation.VERTICAL, true, true, false);
        
        final XYPlot plot = result.getXYPlot();
        plot.getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        
        
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
               
        // first time series
        this.firstMonitorSeries(plot);

        // second time series
        this.secondMonitorSeries(plot);

        // third time series
        this.thirdMonitorSeries(plot);
        
        return result;
    }
    
    
    /**
     * Generates an random entry for a particular call made by time for every 1/4th of a second.
     *
     * @param e  the action event.
     */
//    public void actionPerformed(final ActionEvent e) {
//
//        final double factor = 0.9 + 0.2*Math.radom();
//        this.lastValue = this.lastValue * factor;
//
//        final Millisecond now = new Millisecond();
//        this.series.add(new Millisecond(), this.lastValue);
//
//        System.out.println("Current Time in Milliseconds = " + now.toString()+", Current Value : "+this.lastValue);
//    }
    
   
    public void updateXYSeries1(double val, int tick) {
        
        this.lastValue1 = val;
        this.xySeries1.add(tick, this.lastValue1);
    }
    public void updateXYSeries2(double val, int tick) {
        
        this.lastValue2 = val;
        this.xySeries2.add(tick, this.lastValue2);
    }
    public void updateXYSeries3(double val, int tick) {
        
        this.lastValue3 = val;
        this.xySeries3.add(tick, this.lastValue3);
    }

    /**
     * Starting point for the dynamic graph application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart("Dynamic Line And TimeSeries Chart");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

    /**
     * @return the chartName
     */
    public String getChartName() {
        return chartName;
    }

    /**
     * @param chartName the chartName to set
     */
    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    /**
     * @return the xAxisName
     */
    public String getxAxisName() {
        return xAxisName;
    }

    /**
     * @param xAxisName the xAxisName to set
     */
    public void setxAxisName(String xAxisName) {
        this.xAxisName = xAxisName;
    }

    /**
     * @return the yAxisName
     */
    public String getyAxisName() {
        return yAxisName;
    }

    /**
     * @param yAxisName the yAxisName to set
     */
    public void setyAxisName(String yAxisName) {
        this.yAxisName = yAxisName;
    }

    /**
     * @return the chartPanelWidth
     */
    public int getChartPanelWidth() {
        return chartPanelWidth;
    }

    /**
     * @param chartPanelWidth the chartPanelWidth to set
     */
    public void setChartPanelWidth(int chartPanelWidth) {
        this.chartPanelWidth = chartPanelWidth;
    }

    /**
     * @return the chartPanelHeight
     */
    public int getChartPanelHeight() {
        return chartPanelHeight;
    }

    /**
     * @param chartPanelHeight the chartPanelHeight to set
     */
    public void setChartPanelHeight(int chartPanelHeight) {
        this.chartPanelHeight = chartPanelHeight;
    }

    /**
     * @return the showTime
     */
    public double getShowTime() {
        return showTime;
    }

    /**
     * @param showTime the showTime to set
     */
    public void setShowTime(double showTime) {
        this.showTime = showTime;
    }

}  