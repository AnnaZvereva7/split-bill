package edu.practicum.splitbill;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.practicum.splitbill.model.Participant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Calculation {
    TreeSet<Participant> participantsToPay=new TreeSet<>();
    TreeSet<Participant> participantsWhoPay=new TreeSet<>();
    TreeSet<Participant> participants;
    Path filePath = Path.of("src/main/resources/static/input-check");
    List<String[]> list = new ArrayList<>();
    Map<String, Participant> participantsDebt = new HashMap<>();


    public void findDebts() throws Exception {
        readLineByLine();
        String[] firstLine = list.get(0);
        for (int i = 2; i < firstLine.length; i++) {
            String name = firstLine[i].trim();
            Participant participant = new Participant(name);
            double expenses = 0;
            for (int j = 1; j < list.size(); j++) {
                if (!list.get(j)[i].isBlank()) {
                    expenses += round(Double.parseDouble(list.get(j)[i].trim()));
                }
            }
            participant.setDebt(expenses);
            participantsDebt.put(name, participant);
        }

        for (int j = 1; j < list.size(); j++) {
            String[] line = list.get(j);
            String name = line[0].trim();
            double debt = participantsDebt.get(name).getDebt();
            for (int i = 2; i < list.size() + 1; i++) {
                if (!line[i].isBlank()) {
                    debt -= round(Double.parseDouble(line[i].trim()));
                }
            }
            participantsDebt.get(name).setDebt(debt);
        }
        for (Participant participant : participantsDebt.values()) {
            if (participant.getDebt() <= 0) {
                participantsToPay.add(participant);
            } else if (participant.getDebt() > 0) {
                participantsWhoPay.add(participant);
            }
        }

        participants = new TreeSet<>(participantsDebt.values());

        System.out.println(participantsToPay);
        System.out.println(participantsWhoPay);

    }

    public double round(double amount) {
       return Math.round(amount*100)/100;
    }

    public void readLineByLine() throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    list.add(line);
                }
            }
        }
    }

    public String[] getEmptyLine (int lineSize) {
        String[] line = new String[lineSize];
        for (int i = 1; i < lineSize; i++) {
            line[i] = String.valueOf(0);
        }
        return line;
    }
    public List<String[]> getSumToPay(int lineSize) {

        List<String[]> tableOfPayment = new ArrayList<>();
        String[] names = new String[lineSize];
        int j = 1;
        for (Participant participant : participants) {
            names[j] = participant.getName();
            j += 1;
        }
        tableOfPayment.add(names);
        for (Participant participant : participantsToPay) {
            String[] line = getEmptyLine(lineSize);
            line[0] = participant.getName();
            tableOfPayment.add(line);
        }
        int i = 1;
        while (participantsWhoPay.size() > 0) {
            String[] line = getEmptyLine(lineSize);
            line[0] = participantsWhoPay.last().getName();

            double balanceToPay=-participantsToPay.first().getDebt();
            double balanceWhoPay=participantsWhoPay.last().getDebt();

            if ( balanceWhoPay> balanceToPay) {
                while (balanceWhoPay > balanceToPay) {
                    line[i] = String.format("%.2f",balanceToPay);
                    i += 1;
                    balanceWhoPay=balanceWhoPay-balanceToPay;
                    participantsWhoPay.last().setDebt(balanceWhoPay);
                    participantsToPay.remove(participantsToPay.first());
                    if(participantsToPay.isEmpty()) {
                        break;
                    } else {
                        balanceToPay=-participantsToPay.first().getDebt();
                    }
                }
                if(participantsToPay.isEmpty()) {tableOfPayment.add(line);}
                else {
                    line[i] = String.format("%.2f",balanceWhoPay);
                    balanceToPay-= balanceWhoPay;
                    participantsToPay.first().setDebt(-balanceToPay);
                    participantsWhoPay.remove(participantsWhoPay.last());
                    tableOfPayment.add(line);
                }

            } else {
                line[i] = String.format("%.2f",participantsWhoPay.last().getDebt());
                participantsToPay.first().setDebt(-balanceToPay+balanceWhoPay);
                participantsWhoPay.remove(participantsWhoPay.last());
                tableOfPayment.add(line);
            }

        }
        return tableOfPayment;
    }

    public void writeInFile() {
        String csv = "output-check2.csv";
        int lineSize = participantsDebt.size() + 1;
        List<String[]> tableToPay = getSumToPay(lineSize);
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            for(String [] line:tableToPay) {
                writer.writeNext(line);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
