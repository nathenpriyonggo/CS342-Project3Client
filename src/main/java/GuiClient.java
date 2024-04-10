
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{

	String clientName, prevComboChoice, uniqueName;
	TextField text_chat, text_username;
	Button button_send, button_username, button_textIcon, button_profileIcon, button_groupIcon;
	Client clientConnection;
	ListView<String> list_text, list_profiles;
	ComboBox<String> combo_send;
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Discord-style fonts
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Regular.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Medium.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Semibold.ttf"), 14);
		Font.loadFont(getClass().getResourceAsStream("gg-sans-2/gg sans Bold.ttf"), 14);

		clientConnection = new Client(data->{
				Platform.runLater(()->{
					Message msg = (Message) data;

					if (msg.isCheckUniqueName()) {
						uniqueName = msg.getData();
					}
					else {
						if (msg.isUpdateFriends()) {
							if (msg.getData().equals("addFriend")) {
								list_profiles.getItems().add(msg.getUsername());
							} else if (msg.getData().equals("removeFriend")) {
								list_profiles.getItems().remove(msg.getUsername());
							}
						} else {
							list_text.getItems().add(0, msg.getData());
						}

						combo_send.getItems().clear();
						combo_send.getItems().add("Public");
						combo_send.getItems().addAll(list_profiles.getItems());
						combo_send.getItems().remove(clientName);
						combo_send.setValue(prevComboChoice);
					}
			});
		});

		// Starting operations
		clientConnection.start();
		prevComboChoice = "Public";
		uniqueName = "";





		// All scene: Text Log List
		list_text = new ListView<String>();
		// All scene: Text scene hyperlink button
		button_textIcon = new Button("Texts");
		button_textIcon.setOnAction(e->{
			primaryStage.setScene(createTextItGui());
		});
		// All scene: Text scene hyperlink button
		button_profileIcon = new Button("Profiles");
		button_profileIcon.setOnAction(e->{
			primaryStage.setScene(createProfileGui());
		});
		// All scene: Text scene hyperlink button
		button_groupIcon = new Button("New Group");
		button_groupIcon.setOnAction(e->{
			primaryStage.setScene(createNewGroupGui());
		});


		// Username Scene: Username text field
		text_username = new TextField();
		text_username.setOnKeyPressed(e->{
			if (e.getCode() == KeyCode.ENTER) {

				button_username.setDisable(true);

				clientName = text_username.getText();
				text_username.clear();

				primaryStage.setTitle(clientName + "'s Text-it");
				primaryStage.setScene(createTextItGui());

				clientConnection.send(new Message(clientName, null,
						null, "isInfoName"));
			}
		});
		// Username Scene: Username button
		button_username = new Button("Confirm");
//		button_username.setOnAction(e->{
//
//			String name = text_username.getText();
//
//			try {
//				clientConnection.send(new Message(name, "",
//						"", "isCheckUniqueName"));
//			} catch (Exception temp) {}
//
//			boolean wait = true;
//
//			while (wait) {
//				if (Objects.equals(uniqueName, "true")) {
//					System.out.println("we in1");
//					clientName = name;
//					text_username.clear();
//
//					primaryStage.setTitle(clientName + "'s Text-it");
//					primaryStage.setScene(createTextItGui());
//
//					clientConnection.send(new Message(clientName, "",
//							"", "isInfoName"));
//					System.out.println("we in2");
//					wait = false;
//					System.out.println("we in3");
//				} else if (Objects.equals(uniqueName, "false")) {
//					text_username.setText("Username already exists...");
//					wait = false;
//				}
//				else {
//					System.out.println("while loop again");
//				}
//			}
//		});


		// Text it Scene: Chat text field
		text_chat = new TextField();
		text_chat.setOnKeyPressed(e->{
			if (e.getCode() == KeyCode.ENTER) {
				Message newMessage;

				if (Objects.equals(combo_send.getValue(), "Public")) {
					newMessage = new Message(clientName, null,
							text_chat.getText(), "isPublicText");
				}
				else {
					newMessage = new Message(clientName, combo_send.getValue(),
							text_chat.getText(), "isPrivateText");
				}

				prevComboChoice = combo_send.getValue();
				clientConnection.send(newMessage);
				text_chat.clear();
			}
		});
		// Text it Scene: Send button
		button_send = new Button("Send");
		button_send.setOnAction(e->{
			Message newMessage;

			if (Objects.equals(combo_send.getValue(), "Public")) {
				newMessage = new Message(clientName, null,
						text_chat.getText(), "isPublicText");
			}
			else {
				newMessage = new Message(clientName, combo_send.getValue(),
						text_chat.getText(), "isPrivateText");
			}

			prevComboChoice = combo_send.getValue();
			clientConnection.send(newMessage);
			text_chat.clear();
		});
		// Text it Scene: Profile List
		list_profiles = new ListView<String>();
		// Text it Scene:
		combo_send = new ComboBox<>();






		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		primaryStage.setScene(createUsernameGui());
		primaryStage.setTitle("Text-it");
		primaryStage.show();
	}
	

	/*
	Username scene code
	 */
	public Scene createUsernameGui() {

		VBox rowCenter = new VBox(10, text_username, button_username);
		rowCenter.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(rowCenter);

		return new Scene(pane, 500, 400);
	}

	/*
	Text-it scene code
	 */
	public Scene createTextItGui() {

		HBox colText = new HBox(10, combo_send, text_chat, button_send);
		colText.setAlignment(Pos.CENTER);
		HBox colApps = new HBox(10, button_profileIcon, button_textIcon, button_groupIcon);
		colApps.setAlignment(Pos.CENTER);

		VBox rowCenter = new VBox(10, colText, list_text, colApps);

		rowCenter.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(rowCenter);

		return new Scene(pane, 500, 400);
	}

	/*
	Profile scene code
	 */
	public Scene createProfileGui() {

		Label label = new Label("profiles and friends");

		HBox colApps = new HBox(10, button_profileIcon, button_textIcon, button_groupIcon);

		VBox rowCenter = new VBox(10, label, list_profiles, colApps);
		rowCenter.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(rowCenter);

		return new Scene(pane, 500, 400);
	}

	/*
	New Group scene code
	 */
	public Scene createNewGroupGui() {

		Label label = new Label("New group");

		HBox colApps = new HBox(10, button_profileIcon, button_textIcon, button_groupIcon);

		VBox rowCenter = new VBox(10, label, colApps);
		rowCenter.setStyle("-fx-font-family: 'gg sans Semibold'");

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(rowCenter);

		return new Scene(pane, 500, 400);
	}

}
