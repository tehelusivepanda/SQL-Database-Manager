import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.proteanit.sql.DbUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.*;

public class DatabaseGui extends JFrame
{
    // Swing GUI components
    private JPanel mainFrame;
    private JComboBox driverBox;
    private JComboBox urlBox;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextPane queryField;
    private JButton connectButton;
    private JButton clearCommand;
    private JButton executeButton;
    private JTextArea connectionField;
    private JButton clearResults;
    private JTable resultsWindow;

    // Initial variable settings with default values
    public boolean isConnected = false;
    public String query = "SELECT * from riders";
    public String [] tokens;
    public Connection connection;
    public Statement statement;
    public ResultSet results;
    public PreparedStatement prepSt;

    public DatabaseGui(String title) throws SQLException, ClassNotFoundException
    {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainFrame);
        this.pack();

        connectionField.setText("No connection.");

        MysqlDataSource dataSource = new MysqlDataSource();

        String [] drivers = {"mysql-connection-java-8.0.23.jar", "mssql-jdbc-9.2.1.jre11.jar"};
        for (String driver : drivers)
        {
            driverBox.addItem(driver);
        }

        String [] urls = {"jdbc:mysql://127.0.0.1:3306/project3"};
        for (String url : urls)
        {
            urlBox.addItem(url);
        }

        connectButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                try {
                    MysqlDataSource dataSource = new MysqlDataSource();
                    dataSource.setURL(urlBox.getSelectedItem().toString());
                    dataSource.setUser(usernameField.getText());
                    dataSource.setPassword(passwordField.getText());

                    connection = dataSource.getConnection();
                    connectionField.setText("Connected to " + urlBox.getSelectedItem().toString());
                    connectionField.setSize(200,5);
                    connectionField.setToolTipText("Full URL: " + urlBox.getSelectedItem().toString());

                    queryField.setEditable(true);
                    usernameField.setEditable(false);
                    passwordField.setEditable(false);
                    connectButton.setEnabled(false);

                    System.out.println("Connection Successful!");
                    isConnected = true;

                    statement = connection.createStatement();

                    System.out.println("dataSource is " + dataSource);
                    System.out.println("conection is " + connection);
                    System.out.println("statement is " + statement);
                }
                catch (SQLException e) {
                    JOptionPane.showMessageDialog(mainFrame, "Username or password invalid.");
                    e.printStackTrace();
                }
            }
        });

        clearCommand.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                queryField.setText("");
            }
        });

        executeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if (!isConnected)
                    return;

                try {
                    // If the field is NOT empty
                    if (!queryField.getText().isEmpty())
                    {
                        query = queryField.getText();
                    }

                    // Four types of mySQL commands, Select, Insert, Drop, Update
                    if (query.charAt(0) == 'S' || query.charAt(0) == 's')
                    {
                        querySelect(query);
                    }
                    else if (query.charAt(0) == 'I' || query.charAt(0) == 'i')
                    {
                        queryModify(query);
                    }
                    else if (query.charAt(0) == 'D' || query.charAt(0) == 'd')
                    {
                        queryModify(query);
                    }
                    else if (query.charAt(0) == 'U' || query.charAt(0) == 'u')
                    {
                        queryModify(query);
                    }
                    // Default if empty
                    else
                    {
                        prepSt = connection.prepareStatement(query);

                        results = prepSt.executeQuery(query);

                        resultsWindow.setModel(DbUtils.resultSetToTableModel(results));
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, e.toString());
                    e.printStackTrace();
                }
            }
        });

        clearResults.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                resultsWindow.setModel(new DefaultTableModel());
            }
        });
    }

    // For handling select
    public void querySelect(String query)
    {
        try {
            prepSt = connection.prepareStatement(query);

            results = prepSt.executeQuery(query);

            resultsWindow.setModel(DbUtils.resultSetToTableModel(results));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(mainFrame, e.toString());
            e.printStackTrace();
        }
    }

    // Can handle Add, Update, AND Drop!
    public void queryModify(String query)
    {
        try {
            prepSt = connection.prepareStatement(query);

            prepSt.executeUpdate(query);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(mainFrame, e.toString());
            e.printStackTrace();
        }
    }

    // Driver
    public static void main(String [] args) throws SQLException, ClassNotFoundException
    {
        JFrame frame = new DatabaseGui("SQL Client App - (DEV - CNT 4714 - Spring 2021)");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}