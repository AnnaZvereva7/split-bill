package edu.practicum.splitbill;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class SplitBillApplication {

	public static void main(String[] args) throws Exception {
	Calculation calculation = new Calculation();
	calculation.findDebts();
	calculation.writeInFile();
	}

}
