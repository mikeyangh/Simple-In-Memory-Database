import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;



public class Solution {
    public static void main(String args[] ) throws Exception {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT */
        Map<String, String> map = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();
        List<List<String>> transactions = new ArrayList<>();
        
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            String command = input.nextLine();
            String[] fields = command.split(" ");
            if (fields.length == 1) {
                if (fields[0].equals("END")) {
                    System.out.println("END");
                    break;
                }
                controlTransaction(command, map, countMap, transactions);
            }
            else if (!transactions.isEmpty() && (fields[0].equals("SET") || fields[0].equals("UNSET"))) {
                String key = fields[1];
                if (map.containsKey(key)) {
                    command += " " + map.get(key);
                }
                transactions.get(transactions.size()-1).add(command);
                execute(command, map, countMap);
            }
            else {
                execute(command, map, countMap);
            }
        }
        input.close();
    }
    
    public static void reverseExecute(String command, Map<String, String> map, Map<String, Integer> countMap) {
        String[] fields = command.split(" ");
        if (fields[0].equals("SET")) {
            String key = fields[1];
            String val = fields[2];
            if (fields.length == 3) {
                countMap.put(val, countMap.get(val)-1);
                map.remove(key);
            } else {
                String oldVal = fields[3];
                countMap.put(val, countMap.get(val)-1);
                countMap.put(oldVal, countMap.get(oldVal)+1);
                map.put(key, oldVal);
            }
        } else if (fields[0].equals("UNSET")) {
            String key = fields[1];
            if (fields.length == 3) {
                String oldVal = fields[2];
                countMap.put(oldVal, countMap.get(oldVal)+1);
                map.put(key, oldVal);
            }
        } else {
            System.out.println("Invalid!");
        }
    }
    
    
    public static void controlTransaction(String command, Map<String, String> map, Map<String, Integer> countMap, List<List<String>> transactions) {
        System.out.println(command);
        String[] fields = command.split(" ");
        if (fields[0].equals("BEGIN")) {
            List<String> transaction = new ArrayList<>();
            transactions.add(transaction);
            
        } else if (fields[0].equals("COMMIT")) {
            if (transactions.isEmpty()) {
                System.out.println("> NO TRANSACTION");
                return;
            }
            transactions.clear();
        } else if (fields[0].equals("ROLLBACK")) {
            if (transactions.isEmpty()) {
                System.out.println("> NO TRANSACTION");
                return;
            }
            List<String> transaction = transactions.get(transactions.size()-1);
            transactions.remove(transactions.size()-1);
            for (int i = transaction.size()-1; i >= 0; i--) {
                reverseExecute(transaction.get(i), map, countMap);
            }
        } else {
            System.out.println("Invalid command!");
        }
    }
    
    
    public static void execute(String command, Map<String, String> map, Map<String, Integer> countMap) {
        String[] fields = command.split(" ");

        if (fields[0].equals("SET")) {
            System.out.println(fields[0] + " " + fields[1] + " " + fields[2]);
            String oldVal = null;
            String key = fields[1];
            String newVal = fields[2];
            if (map.containsKey(key)) {
                oldVal = map.get(key);
                countMap.put(oldVal, countMap.get(oldVal)-1);
            }
            map.put(key, newVal);
            if (countMap.containsKey(newVal)) {
                countMap.put(newVal, countMap.get(newVal)+1);
            } else {
                countMap.put(fields[2], 1);
            }        
        } else if (fields[0].equals("GET")) {
            System.out.println(command);
            String key = fields[1];
            System.out.print("> ");
            if (map.containsKey(key)) {
                System.out.println(map.get(key));
            } else {
                System.out.println("NULL");
            }
            
        } else if (fields[0].equals("UNSET")) {
            System.out.println(fields[0] + " " + fields[1]);
            String key = fields[1];
            if (!map.containsKey(key)) {
                return;
            }
            String oldVal = map.get(key);
            countMap.put(oldVal, countMap.get(oldVal)-1);
            map.remove(key);
            
        } else if (fields[0].equals("NUMEQUALTO")) {
            System.out.println(command);
            String key = fields[1];
            System.out.print("> ");
            if (!countMap.containsKey(key)) {
                System.out.println(0);
            } else {
                System.out.println(countMap.get(key));
            } 
            
        } else {
            System.out.println("Invalid command!");
        }
    }
}