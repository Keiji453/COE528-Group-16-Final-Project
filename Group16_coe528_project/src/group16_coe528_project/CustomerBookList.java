/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package group16_coe528_project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author efekete
 */
public class CustomerBookList extends BookstoreState {
    
    private final TableView<Book> table = new TableView<Book>();
    
    static double totalCost;
    
    @Override
    public void start(Stage stage) {
        // Gets Singleton Instance of BookList and UserList
        BookList books = BookList.getInstance();
        UserList users = UserList.getInstance();
        
        // Gets ArrayList for Books from the BookList
        final ArrayList<Book> bookTable = books.getList();
        
        // Sets size of Screne
        Scene scene = new Scene(new Group());
        stage.setTitle("Book Repository");
        stage.setWidth(440);
        stage.setHeight(600);
        
        // Sets status of customer
        String status;
        if (currentUser.getUserPoints() >= 1000) {
            status = "Gold";
        }
        else status = "Silver";
        
        // Displays welcome text
        Label heading = new Label();
        heading.setText("Welcome " + currentUser.getUserName() + ". You have " + currentUser.getUserPoints() + " points. Your status is " + status + ".");
        table.setEditable(true);
        
        // Sets up table for the books
        TableColumn bookColumn = new TableColumn("Name");
        TableColumn priceColumn = new TableColumn("Price");
        TableColumn selectColumn = new TableColumn("Selection");
        
        bookColumn.setMinWidth(150);
        priceColumn.setMinWidth(150);
        selectColumn.setMinWidth(100);
        
        bookColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("bookPrice"));
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));
        
        table.getColumns().addAll(bookColumn, priceColumn, selectColumn);
        
        // Makes columns non-selectable since the table uses checkboxes
        table.setSelectionModel(null);
        
        selectColumn.setStyle("-fx-alignment: CENTER;");
        
        // Fills book table
        for (Book book : bookTable) {
            table.getItems().addAll(book);

        }
        
        Button buttontBuy = new Button("Buy");
        Button buttontRedeem = new Button("Redeem");
        Button buttonLogout = new Button("Logout");

        buttontBuy.setMinWidth(90);
        buttontRedeem.setMinWidth(90);
        buttonLogout.setMinWidth(90);
        
        // When Buy button is pressed find total price and go to customer pay CustomerBuyScreen
buttontBuy.setOnAction(
            new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CustomerBookList.totalCost = 0;
                boolean noSelection = true;
                
                // Finds what books are selected and adds their cost to the total cost of the transaction
                for (int i = 0; i < bookTable.size(); i++) {
                    Book book = bookTable.get(i);
                            
                    if (book.getSelect().isSelected()) {
                        CustomerBookList.totalCost += book.getBookPrice();
                        noSelection = false;
                    }
                }
                
                if (noSelection == true) {
                    // Opens CustomerBookList again to redisplay book list
                    return;
                }
                else {
                    // Sets points of user after transaction
                    int currentPoints = BookstoreState.getCurrentUser().getUserPoints();
                    currentPoints += (int) (CustomerBookList.totalCost * 10);
                    BookstoreState.getCurrentUser().setUserPoints(currentPoints);

                    // Finds what books are selected and removes them 
                    for (int i = 0; i < bookTable.size(); i++) {
                        Book book = bookTable.get(i);

                        if (book.getSelect().isSelected()) {
                            books.removeBook(i);
                            i = -1;
                        }
                    }

                    // Opens customer buy screen
                    CustomerBuyScreen nextStage = new CustomerBuyScreen();
                    nextStage.start(stage);
                }
            }
        }
        );
        
        // When Redeem button is pressed find total price, calculate value of points, subtract value of points by total price and go to customer pay CustomerBuyScreen
        buttontRedeem.setOnAction(
            new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                CustomerBookList.totalCost = 0;
                boolean noSelection = true;
                
                // Finds what books are selected and adds their cost to the total cost of the transaction
                for (int i = 0; i < bookTable.size(); i++) {
                    Book book = bookTable.get(i);
                            
                    if (book.getSelect().isSelected()) {
                        CustomerBookList.totalCost += book.getBookPrice();
                        noSelection = false;
                    }
                }
                
                // returns if no selection made
                if (noSelection) {
                    return;
                }
                
                // Subtracts from the total cost based on the points the user has, also sets users points to zero
                CustomerBookList.totalCost *= 100;
                CustomerBookList.totalCost -= BookstoreState.getCurrentUser().getUserPoints();
                //totalCost -= LogIn.pointsToPasson / 100;
                if (CustomerBookList.totalCost <= 0) {
                    CustomerBookList.totalCost *= - 1;
                    BookstoreState.getCurrentUser().setUserPoints((int)CustomerBookList.totalCost);
                }else {
                   BookstoreState.getCurrentUser().setUserPoints(0); 
                }
                CustomerBookList.totalCost /= 100;
                
                // Finds what books are selected and removes them
                for (int i = 0; i < bookTable.size(); i++) {
                    Book book = bookTable.get(i);
                            
                    if (book.getSelect().isSelected()) {
                        books.removeBook(i);
                        i = -1;
                    }
                }
                
                // Opens customer buy screen
                CustomerBuyScreen nextStage = new CustomerBuyScreen();
                nextStage.start(stage);
            }
        }
        );
        
        // When Logout button is pressed log out of account
        buttonLogout.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                LogIn nextStage = new LogIn();
                nextStage.start(stage);
            }
        }
        );
        
        HBox bottom = new HBox();
        VBox list = new VBox();
        
        bottom.setSpacing(10);
        list.setSpacing(5);
        list.setPadding(new Insets(10, 0, 0, 10));
        
        bottom.getChildren().addAll(buttontBuy, buttontRedeem, buttonLogout);
        
        list.getChildren().addAll(heading, table, bottom);
        
        ((Group) scene.getRoot()).getChildren().addAll(list);
        
        stage.setScene(scene);
        stage.show();
    }
}
