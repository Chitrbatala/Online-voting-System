import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OnlineVotingSystem  {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/voting_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chitr@9354";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            initializeDatabase(connection);

            Scanner scanner = new Scanner(System.in);
            int choice;

            System.out.println("Welcome to the Online Voting System!");
            do {
                System.out.println("1. List Candidates and Party Symbols");
                System.out.println("2. Vote for Candidate");
                System.out.println("3. View Results");
                System.out.println("4. Display Party Symbols");
                System.out.println("5. Reset Votes");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        listCandidates(connection);
                        break;
                    case 2:
                        voteForCandidate(connection);
                        break;
                    case 3:
                        viewResults(connection);
                        break;
                    case 4:
                        displayPartySymbols(connection);
                        break;
                    case 5:
                        resetVotes(connection);
                        break;
                    case 6:
                        System.out.println("Exiting the voting system. Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } while (choice != 6);

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS candidates " +
                    "(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), party_symbol VARCHAR(50), votes INT)";
            statement.executeUpdate(createTableQuery);

            // Check if candidates already exist
            String checkCandidatesQuery = "SELECT COUNT(*) AS count FROM candidates";
            ResultSet resultSet = statement.executeQuery(checkCandidatesQuery);
            resultSet.next();
            int candidateCount = resultSet.getInt("count");

            if (candidateCount == 0) {
                // Insert candidates only if they don't exist
                String insertCandidatesQuery = "INSERT IGNORE INTO candidates (name, party_symbol, votes) VALUES " +
                        " ('Bhartiya Janta Party','Lotus', 0), " +
                        "('Aam Aadmi Party','Broom', 0), " +
                        "  ('Congress','Hand', 0), " +
                        " ('Samajwadi Party','Cycle', 0)";
                statement.executeUpdate(insertCandidatesQuery);
            }
        }
    }

    private static void listCandidates(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String listCandidatesQuery = "SELECT * FROM candidates";
            ResultSet resultSet = statement.executeQuery(listCandidatesQuery);

            System.out.println("Candidates and Party Symbols:");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("id") + ". " +
                        resultSet.getString("name") + " (Party Symbol: " + resultSet.getString("party_symbol") + ")");
            }
        }
    }

    private static void voteForCandidate(Connection connection) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the candidate ID you want to vote for: ");
        int candidateId = scanner.nextInt();

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET votes = votes + 1 WHERE id = ?")) {
            preparedStatement.setInt(1, candidateId);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Vote successfully cast for candidate ID " + candidateId);
            } else {
                System.out.println("Invalid candidate ID. Please try again.");
            }
        }
    }

    private static void viewResults(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String viewResultsQuery = "SELECT * FROM candidates";
            ResultSet resultSet = statement.executeQuery(viewResultsQuery);

            System.out.println("Results:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + " (Party Symbol: " + resultSet.getString("party_symbol") +
                        "): " + resultSet.getInt("votes") + " votes");
            }
        }
    }

    private static void displayPartySymbols(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String displaySymbolsQuery = "SELECT name, party_symbol FROM candidates";
            ResultSet resultSet = statement.executeQuery(displaySymbolsQuery);

            System.out.println("Party Symbols:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name") + ": " + resultSet.getString("party_symbol"));
            }
        }
    }

    private static void resetVotes(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String resetVotesQuery = "UPDATE candidates SET votes = 0";
            int rowsUpdated = statement.executeUpdate(resetVotesQuery);

            if (rowsUpdated > 0) {
                System.out.println("All votes reset to 0.");
            } else {
                System.out.println("Failed to reset votes.");
            }
        }
    }
}