package org.example;

import org.example.model.ConnectionModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import static org.example.model.CreateTables.createTables;
import static org.example.view.viewManager.startMenu;


//Lösenord:
//id 1: hej123
//id 2: 123lsn
//id 3: password123
//id 4: losenord10
//id 5: blommor23
//id 6: hund674
//id 7: 643soffan

public class Main extends ConnectionModel {

    public static int loggedInUserId = -1;
    public static void main(String[] args) throws IOException {
        InitializeDatabase();
        createTables();
        startMenu();
    }


    //läsa in databas från extern fil
    public class ReadDatabaseFromExternalFile {
        public static void main(String[] args) {
            try (BufferedReader reader = new BufferedReader(new FileReader("database.properties.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}