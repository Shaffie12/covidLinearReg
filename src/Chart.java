import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.util.ShapeUtilities;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.io.IOException;
import org.apache.commons.math3.*;

import javax.swing.*;


public class Chart extends ApplicationFrame
{


    //makes the application frame with the graphs
    public Chart(String applicationTitle) throws IOException //code in here really needs to be separated into smaller parts
    {
        super(applicationTitle);
        Grapher2 deaths = new Grapher2("src/JavaData/deaths.csv");
        Grapher2 infections = new Grapher2("src/JavaData/infections.csv");
        List<Integer> deathBounds = makeDeathsBoundaries();
        List<Integer> infectionBounds = makeInfectionBoundaries();
        //deaths graph
        XYSeriesCollection datasetDeaths = new XYSeriesCollection();
        XYSeries baseDeaths = getSeries(false,"Deaths",deaths,deathBounds);
        XYSeries deathsRegression = getSeries(true,"Piecewise Linear Regression",deaths,deathBounds);
        XYSeries deathPrediction = getPredict(deaths,deathBounds);
        datasetDeaths.addSeries(baseDeaths);
        datasetDeaths.addSeries(deathsRegression);
        datasetDeaths.addSeries(deathPrediction);
        //infections graph
        XYSeriesCollection datasetInfections = new XYSeriesCollection();
        XYSeries baseInfections = getSeries(false,"Infections",infections,infectionBounds);
        XYSeries infectionsRegression = getSeries(true,"Piecewise Linear Regression",infections,infectionBounds);
        XYSeries infectionsPrediction = getPredict(infections,infectionBounds);
        datasetInfections.addSeries(baseInfections);
        datasetInfections.addSeries(infectionsRegression);
        datasetInfections.addSeries(infectionsPrediction);


        JFreeChart scatterDeaths = ChartFactory.createScatterPlot("Deaths/Day", "Days","Deaths",datasetDeaths,PlotOrientation.VERTICAL,true,true,false);
        JFreeChart scatterInfections = ChartFactory.createScatterPlot("Infections/Day","Days","Infections",datasetInfections,PlotOrientation.VERTICAL,true,true,false);

        XYPlot xyplot = (XYPlot) scatterDeaths.getPlot();
        XYItemRenderer r = xyplot.getRenderer();
        XYLineAndShapeRenderer ls = (XYLineAndShapeRenderer) r;
        ls.setSeriesShape(0,ShapeUtilities.createDownTriangle((0.7f)));
        ls.setSeriesShape(1,ShapeUtilities.createDiamond(1f));
        ls.setSeriesShape(2,ShapeUtilities.createUpTriangle(0.7f));
        ls.setSeriesLinesVisible(1,true);
        ls.setSeriesLinesVisible(2,true);

        XYPlot xyplot2 = (XYPlot) scatterInfections.getPlot();
        XYItemRenderer r2 = xyplot2.getRenderer();
        XYLineAndShapeRenderer ls2 = (XYLineAndShapeRenderer) r2;
        ls2.setSeriesShape(0,ShapeUtilities.createDownTriangle(0.7f));
        ls2.setSeriesShape(1,ShapeUtilities.createDiamond(1f));
        ls2.setSeriesShape(2,ShapeUtilities.createUpTriangle(0.7f));
        ls2.setSeriesLinesVisible(1,true);
        ls2.setSeriesLinesVisible(2,true);

        this.setLayout(new FlowLayout());
        ChartPanel cp = new ChartPanel(scatterDeaths);
        ChartPanel cp2 = new ChartPanel(scatterInfections);
        cp.setPreferredSize(new Dimension(600,400));
        cp2.setPreferredSize(new Dimension(600,400));
        this.getContentPane().add(cp2);
        this.getContentPane().add(cp);

    }


    //makes a series for the graph using arguments
    private XYSeries getSeries(boolean isRegressionSeries, String name, Grapher2 g, List<Integer> bounds) throws IOException //maybe modify this to take an argument and return data from a different place using that arg?
    {
        XYSeries s = new XYSeries(name);
        if(isRegressionSeries)
        {

            for(int i=1;i<bounds.size();i++)
            {
                for (int j = i - 1; j < i; j++)
                {
                    List<Point> points = g.getDataPointsInRange(bounds.get(j),bounds.get(i));
                    SimpleRegression sr = getLinearReg(bounds.get(j),bounds.get(i),g);
                    for (Point p : points)
                    {
                        if (bounds.contains(p.x))
                        {
                            double y= sr.getSlope()*p.x+sr.getIntercept(); //sets the y axis to 0 if it would go below 0
                            if(y<0)
                            {
                                y=0;
                            }
                            s.add(p.x, y);

                        }
                        if (i == bounds.size() - 1) {
                            double x = bounds.get(bounds.size() - 1);
                            double y = sr.getSlope() * x + sr.getIntercept();
                            s.add(x, y);

                        }

                    }

                }
            }

        }
        else
        {
            List<Point> points = g.getPoints();
            for(Point p:points)
            {
                s.add(p.x,p.y);
            }
        }
        return s;

    }

    //saves the points to a SimpleRegression obj which can calculate the intercept and slope for a set of points it has
    private SimpleRegression getLinearReg(int lowerBound, int upperBound,Grapher2 g) throws IOException
    {
        SimpleRegression reg = new SimpleRegression();
        for(Point p:g.getDataPointsInRange(lowerBound,upperBound))
        {
            reg.addData(p.x,p.y);
        }
        return reg;
    }

    //creates the points at which we want to plot the linear functions with 1 for each graph/dataset
    //this was simply done by looking at the trend in data and choosing the min/max points up to 5.
    //more boundaries can be added to increase the number of linear functions /accuracy of the piecewise linear funct
    private List<Integer> makeDeathsBoundaries()
    {
        List<Integer> bounds = new ArrayList<>();
        bounds.add(0);
        bounds.add(75);
        bounds.add(170);
        bounds.add(360);
        bounds.add(450);
        bounds.add(750);



        return bounds;
    }

    private List<Integer> makeInfectionBoundaries()
    {
        List<Integer> bounds = new ArrayList<>();
        bounds.add(0);
        bounds.add(250);
        bounds.add(360);
        bounds.add(500);
        bounds.add(650);
        bounds.add(750);

        return bounds;
    }

    //creates the prediction series points
    private XYSeries getPredict(Grapher2 g, List<Integer> bounds) throws IOException
    {
        XYSeries p = new XYSeries("Prediction");
        int ub = bounds.get(bounds.size() - 1);
        int lb = bounds.get(bounds.size() - 2);

        SimpleRegression r = getLinearReg(lb, ub, g);

        int x = bounds.get(bounds.size() - 1);
        p.add(x, r.getSlope() * x + r.getIntercept());
        p.add(bounds.get(bounds.size() - 1) + 100, r.getSlope() * (x+100) + r.getIntercept());
        return p;
    }

}
