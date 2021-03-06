import java.util.*;
import java.io.*;

public class NN_education {
    public final int nh = 4;
    public final double r = .1;
    public final int n = 5;
    public final int iterations = 200;

    public double[][] hw = new double[n][nh];
    public double[] ow = new double[nh];
    
    public static void main(String[] args) throws Exception{
        BufferedReader test = new BufferedReader(new FileReader(args[0]));
        String line = test.readLine();
        List<double[]> lines = new ArrayList<double[]>();

        int size = 0;
        line = test.readLine();
        while(line != null) {
            String[] words = line.split(",");
            double[] vals = new double[words.length];
            for(int i = 0; i < words.length; i ++) 
                vals[i] = Double.parseDouble(words[i]);
            lines.add(vals);
            size++;
            line = test.readLine();
        }
        
        NN_education nn = new NN_education();
        nn.BP(lines);
        System.out.println("TRAINING COMPLETED! NOW PREDICTING.");
        BufferedReader dev = new BufferedReader(new FileReader(args[1]));
        lines.clear();
        line = dev.readLine();
        line = dev.readLine();
        while(line != null) {
            String[] words = line.split(",");
            double[] vals = new double[words.length];
            for(int i = 0; i < words.length; i ++) 
                vals[i] = Double.parseDouble(words[i]);
            lines.add(vals);
            size++;
            line = dev.readLine();
        }

        nn.run(lines);
    }
    public void run(List<double[]> dev) {
        for(double[] ex : dev) {
            double[] hid = new double[nh];
            double sum;
            for(int h = 0; h < nh; h++) {
                sum = 0;
                for(int i = 0; i < n; i++) 
                    sum += hw[i][h] * ex[i]/100;
                hid[h] = 1/(1+Math.exp(-sum));
            }
            
            sum = 0;
            for(int h = 0; h < nh; h++) 
                sum += ow[h] * hid[h];
            
            System.out.println((double)Math.round(1/(1+Math.exp(-sum)) * 100));
        }
    }
    
    public void BP(List<double[]> test) {
        double[] hid = new double[nh];
        double[] hErr = new double[nh];
        double out;
        double oErr = 0;
        double error = 100000;
        double prevError = 100001;
        int count = 0;

        //intialize hw and ow
        Random rand = new Random();
        double sum;
        for(int i = 0; i < hw.length; i++) 
            for(int j = 0; j < hw[0].length; j++) 
                hw[i][j] = rand.nextDouble()/10;
        
        for(int i = 0; i < ow.length; i++)
            ow[i] = rand.nextDouble()/10;

        hid[0] = 1;
      
        while(prevError > error) {
            prevError = error;
            error = 0;
            for(double[] ex : test) {
                for(int h = 0; h < nh; h++) {
                    sum = 0;
                    for(int i = 0; i < n; i++) 
                        sum += hw[i][h] * ex[i]/100;
                    hid[h] = 1/(1+Math.exp(-sum));
                }
                
                sum = 0;
                for(int h = 0; h < nh; h++) 
                    sum += ow[h] * hid[h];

                out = 1/(1+Math.exp(-sum));
                error += (out - ex[n]/100) * (out - ex[n]/100);

                oErr = out * (1 - out) * (ex[n]/100 - out);
                
                for(int h = 0; h < nh; h++) {
                    hErr[h] = hid[h] * (1 - hid[h]) * ow[h] * oErr;
                    for(int i = 0; i < n; i++) 
                        hw[i][h] = hw[i][h] + r*hErr[h]*ex[i];
                    ow[h] = ow[h] + r*oErr*hid[h];
                }
            }
            if (count % 10 == 0) 
                System.out.println(error/2);
            count++;
        }
        for(int h = 0; h < nh; h++) {
            for(int i = 0; i < n; i++) 
                System.out.println(hw[i][h]);
            System.out.println(ow[h]);
        }
    }
}