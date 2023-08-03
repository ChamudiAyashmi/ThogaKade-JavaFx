package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colSalary;
    public TableView tblCustomer;

    public void btnAddOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        Customer customer = new Customer(txtId.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText()));
        try {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to add this customer?", ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement pstm = connection.prepareStatement("INSERT INTO customer VALUES (?,?,?,?)");
                pstm.setObject(1, customer.getId());
                pstm.setObject(2, customer.getName());
                pstm.setObject(3, customer.getAddress());
                pstm.setObject(4, customer.getSalary());

                if (pstm.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Added !").show();
                    clearTextField();
                    loadTable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Something went wrong !").show();
                }
                txtId.setText("");
                txtName.setText("");
                txtAddress.setText("");
                txtSalary.setText("");
            }

        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
            new Alert(Alert.AlertType.ERROR, exception.getMessage()).show();
        }
    }
    public void btnCancelOnAction(ActionEvent actionEvent) {
        txtId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtSalary.setText("");
    }
    public void btnUpdateOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String SQL = "Update Customer set name=?,address=?,salary=? where id=?";
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        preparedStatement.setObject(1,txtName.getText());
        preparedStatement.setObject(2,txtAddress.getText());
        preparedStatement.setObject(3,txtSalary.getText());
        preparedStatement.setObject(4,txtId.getText());

        if (preparedStatement.executeUpdate()>0){
            new Alert(Alert.AlertType.INFORMATION, "Update success !").show();
            clearTextField();
            loadTable();
        }else {
            new Alert(Alert.AlertType.ERROR, "Update Failed !").show();
        }
    }
    public void btnSearchOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String SQL="Select * From Customer where id= '"+txtId.getText()+"'";
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement=connection.createStatement();
        ResultSet rst=statement.executeQuery(SQL);
        if(rst.next()){
            Customer customer= new Customer(txtId.getText(),rst.getString("name"),rst.getString("address"),rst.getDouble("salary"));
            txtName.setText(customer.getName());
            txtAddress.setText(customer.getAddress());
            txtSalary.setText(customer.getSalary()+"");
        }else {
            new Alert(Alert.AlertType.ERROR, "Customer not found !").show();
        }
    }
    public void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String SQL="Delete from Customer where id='"+txtId.getText()+"'";
        Connection connection = DBConnection.getInstance().getConnection();
        Statement statement=connection.createStatement();
        statement.executeUpdate(SQL);
        int num=statement.executeUpdate(SQL);
        if (num>=0){
            new Alert(Alert.AlertType.INFORMATION, "Deleted success !").show();
            clearTextField();
            loadTable();
        }

    }
    public void txtIdSearchOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        btnSearchOnAction(actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTable();
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observable,
                                                                              oldValue,
                                                                              newValue) -> {
            if(newValue!=null){
                setTableValuesToTxt((Customer) newValue);
            }

        });
    }
    public void loadTable(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        String SQL = "Select * from customer";
        ObservableList<Customer> list =FXCollections.observableArrayList();
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet=statement.executeQuery(SQL);
            while (resultSet.next()){
                Customer customer=new Customer(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getDouble(4));
                list.add(customer);
            }
            tblCustomer.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void setTableValuesToTxt(Customer newValue) {
        txtId.setText(newValue.getId());
        txtName.setText(newValue.getName());
        txtAddress.setText(newValue.getAddress());
        txtSalary.setText(String.valueOf((newValue.getSalary())));
    }
    public void clearTextField(){
        txtId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtSalary.setText("");
    }
}
