import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class VotingManage extends JFrame {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/voting_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chitr@9354";

    private Connection connection;

    public VotingManage() {
        initializeDatabase();
        initComponents();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            createTableIfNotExists();
            insertDefaultCandidates();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS candidates " +
                    "(id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), party_symbol VARCHAR(50), votes INT)";
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDefaultCandidates() {
        try (Statement statement = connection.createStatement()) {
            String checkCandidatesQuery = "SELECT COUNT(*) AS count FROM candidates";
            ResultSet resultSet = statement.executeQuery(checkCandidatesQuery);
            resultSet.next();
            int candidateCount = resultSet.getInt("count");

            if (candidateCount == 0) {
                String insertCandidatesQuery = "INSERT IGNORE INTO candidates (name, party_symbol, votes) VALUES " +
                        "('Bhartiya Janta Party','Lotus', 0), " +
                        "('Aam Aadmi Party','Broom', 0), " +
                        "('Congress','Hand', 0), " +
                        "('Samajwadi Party','Cycle', 0)";
                statement.executeUpdate(insertCandidatesQuery);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setTitle("Online Voting System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JButton listCandidatesButton = new JButton("List Candidates");
        JButton voteButton = new JButton("Vote for Candidate");
        JButton viewResultsButton = new JButton("View Results");
        JButton displaySymbolsButton = new JButton("Display Party Symbols");
        JButton resetVotesButton = new JButton("Reset Votes");
        JButton exitButton = new JButton("Exit");

        listCandidatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listCandidates();
            }
        });

        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voteForCandidate();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewResults();
            }
        });

        displaySymbolsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPartySymbols();
            }
        });

        resetVotesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetVotes();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeConnection();
                System.exit(0);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        panel.add(listCandidatesButton);
        panel.add(voteButton);
        panel.add(viewResultsButton);
        panel.add(displaySymbolsButton);
        panel.add(resetVotesButton);
        panel.add(exitButton);

        getContentPane().add(panel);
    }

    private void listCandidates() {
        try (Statement statement = connection.createStatement()) {
            String listCandidatesQuery = "SELECT * FROM candidates";
            ResultSet resultSet = statement.executeQuery(listCandidatesQuery);

            StringBuilder candidatesInfo = new StringBuilder("Candidates and Party Symbols:\n");
            while (resultSet.next()) {
                candidatesInfo.append(resultSet.getInt("id")).append(". ")
                        .append(resultSet.getString("name")).append(" (Party Symbol: ")
                        .append(resultSet.getString("party_symbol")).append(")\n");
            }

            JOptionPane.showMessageDialog(this, candidatesInfo.toString(), "List Candidates", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void voteForCandidate() {
        String candidateIdStr = JOptionPane.showInputDialog(this, "Enter the candidate ID you want to vote for:");
        try {
            int candidateId = Integer.parseInt(candidateIdStr);
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE candidates SET votes = votes + 1 WHERE id = ?")) {
                preparedStatement.setInt(1, candidateId);
                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Vote successfully cast for candidate ID " + candidateId);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid candidate ID. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid candidate ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewResults() {
        try (Statement statement = connection.createStatement()) {
            String viewResultsQuery = "SELECT * FROM candidates";
            ResultSet resultSet = statement.executeQuery(viewResultsQuery);

            StringBuilder resultsInfo = new StringBuilder("Results:\n");
            while (resultSet.next()) {
                resultsInfo.append(resultSet.getString("name")).append(" (Party Symbol: ")
                        .append(resultSet.getString("party_symbol")).append("): ")
                        .append(resultSet.getInt("votes")).append(" votes\n");
            }

            JOptionPane.showMessageDialog(this, resultsInfo.toString(), "View Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayPartySymbols() {
        try (Statement statement = connection.createStatement()) {
            String displaySymbolsQuery = "SELECT name, party_symbol FROM candidates";
            ResultSet resultSet = statement.executeQuery(displaySymbolsQuery);

            StringBuilder symbolsInfo = new StringBuilder("Party Symbols:\n");
            while (resultSet.next()) {
                symbolsInfo.append(resultSet.getString("name")).append(": ").append(resultSet.getString("party_symbol")).append("\n");
            }

            JOptionPane.showMessageDialog(this, symbolsInfo.toString(), "Display Party Symbols", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetVotes() {
        try (Statement statement = connection.createStatement()) {
            String resetVotesQuery = "UPDATE candidates SET votes = 0";
            int rowsUpdated = statement.executeUpdate(resetVotesQuery);

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "All votes reset to 0.", "Reset Votes", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset votes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new VotingManage().setVisible(true);
            }
        });
    }
}
