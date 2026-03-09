import java.util.*;

public class Problem1 {

    private HashMap<String, Integer> usernameDB = new HashMap<>();
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();

    // Register new user
    public void registerUser(String username, int userId){
        usernameDB.put(username, userId);
    }

    // Check availability
    public boolean checkAvailability(String username) {

        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        return !usernameDB.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for(int i = 1; i <= 5; i++){
            String newName = username + i;

            if(!usernameDB.containsKey(newName)){
                suggestions.add(newName);
            }
        }

        String modified = username.replace("_",".");

        if(!usernameDB.containsKey(modified)){
            suggestions.add(modified);
        }

        return suggestions;
    }

    // Most attempted username
    public String getMostAttempted(){

        String popular = "";
        int max = 0;

        for(String user : attemptFrequency.keySet()){

            int count = attemptFrequency.get(user);

            if(count > max){
                max = count;
                popular = user;
            }
        }

        return popular;
    }

    // MAIN METHOD FOR TESTING
    public static void main(String[] args) {

        Problem1 system = new Problem1();

        system.registerUser("john_doe",101);
        system.registerUser("alice123",102);

        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        System.out.println(system.suggestAlternatives("john_doe"));

        System.out.println(system.getMostAttempted());
    }
}