import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class StockTrader /*implements Runnable*/{
	private double cash;
	private double boughtPrice;
	private String tickerToBuy;
	
	public StockTrader(){
		cash = 1000;
		boughtPrice = 0;
	}
	
	public void run() throws IOException, InterruptedException{
		while(true){
			String [] comps = {"AAPL", "ATVI", "EA", "FB", "GOOG", "MSFT", "SBUX", "SNY", "TSLA", "TWTR" };
			double[ ][ ] bidAsk = new double[10][2];
			tickerToBuy = "";
			double maxRatio = 0;
			
			for(int i = 0; i < 10; i++){
				bidAsk[i][0] = getMinAsk(comps[i]);
				bidAsk[i][1] = getMaxBid(comps[i]);
				if(bidAsk[i][1] / bidAsk[i][0] > maxRatio){
					maxRatio = bidAsk[i][1] / bidAsk[i][0];
					tickerToBuy = comps[i];
				}
			}
			
			cash = getCash();
			double a = getMinAsk(tickerToBuy);
			int i = (int) (cash/a);
			buy(tickerToBuy, i, a);
			boughtPrice = a;
			
			Thread.sleep(60000);
			
			while(getMaxBid(tickerToBuy) < (boughtPrice * .96))
				;
			
			sellAll(tickerToBuy);
		}
	}
	
	public double getCash() throws IOException{
		Socket socket = new Socket("codebb.cloudapp.net", 17429);
        PrintWriter pout = new PrintWriter(socket.getOutputStream());
        BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pout.println("DefNotProBro" + " " + "krypt");
        
        pout.println("MY_CASH");
        
		pout.println("CLOSE_CONNECTION");
        pout.flush();
        String line;
        line = bin.readLine();
        line = line.substring(12);
        pout.close();
        bin.close();
        socket.close();
        return Double.parseDouble(line);
	}
	
	public double getMinAsk(String ticker) throws IOException{
		double min = 100000;
		Socket socket = new Socket("codebb.cloudapp.net", Integer.parseInt("17429"));
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
    	BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	pout.println("DefNotProBro" + " " + "krypt");
	  
    	pout.println("ORDERS "+ ticker);
    	pout.println("CLOSE_CONNECTION");
    	pout.flush();
    	String line;
		String holder = "";
		while ((line = bin.readLine()) != null) {
			holder = holder + line;
		}
		  
		String delims = "[ ]+";
		String[] temp = holder.split(delims);
		  
		for(int i = 0; i < temp.length; i++){
			if(temp[i].charAt(0) == 'A' && temp[i].charAt(1) == 'S' && Double.parseDouble(temp[i+2]) < min){
				 min = Double.parseDouble(temp[i+2]);
			 }
		}
		return min;
	}
	
	public double getMaxBid(String ticker) throws IOException{
		double max = 0;
		Socket socket = new Socket("codebb.cloudapp.net", Integer.parseInt("17429"));
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
    	BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	pout.println("DefNotProBro" + " " + "krypt");
	  
    	pout.println("ORDERS "+ ticker);
    	pout.println("CLOSE_CONNECTION");
    	pout.flush();
    	String line;
		String holder = "";
		while ((line = bin.readLine()) != null) {
			holder = holder + line;
		}
		  
		String delims = "[ ]+";
		String[] temp = holder.split(delims);
		  
		for(int i = 0; i < temp.length; i++){
			if(temp[i].charAt(0) == 'B' && temp[i].charAt(1) == 'I' && Double.parseDouble(temp[i+2]) > max){
				 max = Double.parseDouble(temp[i+2]);
			 }
		}
		return max;
	}
	
	public void buy(String ticker, int shares, double price) throws IOException{
		Socket socket = new Socket("codebb.cloudapp.net", Integer.parseInt("17429"));
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
    	BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	pout.println("DefNotProBro" + " " + "krypt");
	  
    	pout.println("BID "+ ticker + " " + price + " " + shares);
    	pout.println("CLOSE_CONNECTION");
    	pout.flush();
    	String line;
		while ((line = bin.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	public void sell(String ticker, double price, int shares) throws IOException{
		Socket socket = new Socket("codebb.cloudapp.net", Integer.parseInt("17429"));
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
    	BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	pout.println("DefNotProBro" + " " + "krypt");
	  
    	pout.println("ASK "+ ticker + " " + price + " " + shares);
    	pout.println("CLOSE_CONNECTION");
    	pout.flush();
    	String line;
		while ((line = bin.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	public void sellAll(String ticker) throws IOException{
		int numStocks = 0;
		Socket socket = new Socket("codebb.cloudapp.net", Integer.parseInt("17429"));
		PrintWriter pout = new PrintWriter(socket.getOutputStream());
    	BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	pout.println("DefNotProBro" + " " + "krypt");
	  
    	pout.println("MY_SECURITIES");
    	pout.println("CLOSE_CONNECTION");
    	pout.flush();
    	String line;
		String holder = "";
		while ((line = bin.readLine()) != null) {
			holder = holder + line;
		}
		  
		String delims = "[ ]+";
		String[] temp = holder.split(delims);
		  
		for(int i = 0; i < temp.length; i++){
			if(temp[i].charAt(0) == ticker.charAt(0) && temp[i].charAt(1) == ticker.charAt(1)){
				 numStocks = Integer.parseInt(temp[i + 1]);
			 }
		}
		
		sell(ticker, getMinAsk(ticker), numStocks);
	}
	
	public String getT(){
		return tickerToBuy;
	}
}
