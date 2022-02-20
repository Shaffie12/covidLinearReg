import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;

public class Main
{
    //main
    public static void main(String[] args)
    {
        //attempts to make graph, if file isnt found then error message is printed.
        try
        {
            Chart chart = new Chart("Charts");
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);



        }
        catch (Exception e)
        {
            System.out.println("couldnt read file");
            e.getMessage();
        }


    }




}

