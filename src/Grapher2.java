import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.util.List;


//class to open file and create graph axis
public class Grapher2 extends JPanel {
    //vars

    //2d array that holds the appropriate data from the frame
    //list of Point objects (hold x,y coords)
    //the name of the file associated with any given grapher2 object
    private String[][] data;
    private List<Point> graphPoints;
    String file;



    //constructors
    Grapher2(String filename)
    {
        this.graphPoints = new ArrayList<>();
        this.file=filename;
    }

    //methods

    //read the file , insert an array of 3 elements into each index (1 for each column)
    public void getData() throws IOException
    {
        setLen();
        Scanner s = new Scanner(new File(file));
        s.useDelimiter("\n");
        int i=0;
        while (i<data.length)
        {
            String line = s.next();
            Scanner s2 = new Scanner(line);
            s2.useDelimiter(",");
            for(int j=0;j<3;j++)
            {
                String val = s2.next();
                data[i][j]=val;
            }
            i++;
        }

    }

    //read file and make string array of equal size
    private void setLen() throws IOException
    {
        Scanner s = new Scanner(new File(file));
        int length=0;
        s.useDelimiter("\n");
        while(s.hasNext())
        {
            length+=1;
            s.next();
        }
        data=new String[length][3];

    }


    //converts data from the array member into Point objects where x = days since earliest day, y = deaths/infections
    public List<Point> getPoints() throws IOException
    {
        if(data==null)
        {
            getData();
        }
        for (int i = data.length - 1; i > 0; i--)
        {
            int x1 = (int) DateParser.getDateDiff(data[data.length - 1][0], data[i][0]);
            int y1 = Integer.parseInt(data[i][1]);
            graphPoints.add(new Point(x1, y1));
        }
        return graphPoints;
    }

    //return a portion of the points list given 2 boundaries
    public List<Point> getDataPointsInRange(int lowerBound, int upperBound) throws IOException
    {

        List<Point> pointsInRange = new ArrayList<>();
        if(graphPoints.size()<1) {
            getPoints();
        }

        for (Point p : graphPoints)
        {


            if (p.x >= lowerBound && p.x < upperBound)
            {

                pointsInRange.add(p);
            }
        }

        return pointsInRange;
    }



}
