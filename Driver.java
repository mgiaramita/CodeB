
import java.io.IOException;

public class Driver {
	public static void main(String[] args) throws InterruptedException{
		StockTrader st = new StockTrader();
		try {
			st.run();
		} catch (IOException e) {
			try {
				st.sellAll(st.getT());
			} catch (IOException e2) {}
			try {
				st.run();
			} catch (IOException e1) {}
		}
	}
}
