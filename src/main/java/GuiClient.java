
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/*
Gui Client class
 */
public class GuiClient extends Application{

	String clientName, prevComboChoice, uniqueName;
	Label label_title, label_question, label_textIt, label_friendsList, label_groupList,
			label_profile, label_newGroupIt, label_friendsToAddIt;
	TextField text_chat, text_username, text_groupName;
	Button button_send, button_username, button_messageIcon, button_profileIcon,
			button_groupIcon, button_groupName;
	ImageView img_profile, img_message, img_group, img_profilePic;
	Client clientConnection;
	ListView<Label> list_profiles, list_groups, list_messages;
	ListView<RadioButton> list_groupChooseFriend;
	ChoiceBox<String> choiceBox_send;
	
	
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

		// Callback extension
		clientConnection = new Client(data->{
			Platform.runLater(()->{
				Message msg = (Message) data;

				// Input message is unique name check
				if (msg.isCheckUniqueName()) {

					// Initiate client code if unique name
					if (Objects.equals(msg.getData(), "true")) {
						button_username.setDisable(true);

						clientName = msg.getUsername();
						text_username.clear();

						primaryStage.setTitle(clientName + "'s Text-it");
						primaryStage.setScene(createTextItGui());

						// Send infoName message to let other clients know new friend has appeared
						clientConnection.send(new Message(clientName, null,
								null, "isInfoName"));
					}
					// Loop back if used name
					else {
						text_username.setText("Username already exists...");
					}
				}
				else {
					// Input message is update friends message
					if (msg.isUpdateFriends()) {
						// Update by adding friend
						if (msg.getData().equals("addFriend")) {
							// Add new friend to friend list
							Label newLabel = new Label(msg.getUsername());
							newLabel.setFont(Font.font("gg sans Semibold", 14));
							newLabel.setTextFill(Color.web("#353935"));
							list_profiles.getItems().add(newLabel);
							// Add new friend to candidate friends for making a new group list
							RadioButton newRadio = new RadioButton(msg.getUsername());
							newRadio.setFont(Font.font("gg sans Semibold", 14));
							newRadio.setTextFill(Color.web("#353935"));
							list_groupChooseFriend.getItems().add(newRadio);
						}
						// Update by removing friend
						else if (msg.getData().equals("removeFriend")) {
							// Remove friend from friend list
							for (Label label : list_profiles.getItems()) {
								if (Objects.equals(label.getText(), msg.getUsername())) {
									list_profiles.getItems().remove(label);
									break;
								}
							}
							// Remove friend from candidate of friend list in making new group
							for (RadioButton radio : list_groupChooseFriend.getItems()) {
								if (Objects.equals(radio.getText(), msg.getUsername())) {
									list_groupChooseFriend.getItems().remove(radio);
									break;
								}
							}
						}
					}
					// Input message is update group list message
					else if (msg.isUpdateGroupList()) {
						Label newLabel = new Label(msg.getData());
						newLabel.setFont(Font.font("gg sans Semibold", 14));
						newLabel.setTextFill(Color.web("#353935"));
						list_groups.getItems().add(newLabel);
					}
					// Input message is public or either private text
					else {
						Label newLabel = new Label(msg.getData());

						// Set design if public text
						if (msg.isPublicText()) {

							newLabel.setFont(Font.font("gg sans Semibold", 14));
							newLabel.setTextFill(Color.web("#097969"));
						}
						// Set design if private text
						else if (msg.isPrivateText()) {
							newLabel.setFont(Font.font("gg sans Semibold", 14));
							newLabel.setTextFill(Color.web("#800080"));
						}
						// Set design if notification
						else {
							newLabel.setFont(Font.font("gg sans Bold", 14));
							newLabel.setTextFill(Color.web("#353935"));
						}


						list_messages.getItems().add(0, newLabel);
					}

					// Update combo box lists
					choiceBox_send.getItems().clear();
					choiceBox_send.getItems().add("Public");
					for (Label label : list_profiles.getItems()) {
						if (!Objects.equals(label.getText(), clientName)) {
							choiceBox_send.getItems().add("to: " + label.getText());
						}
					}
					choiceBox_send.setValue(prevComboChoice);
				}
			});
		});

		// Starting operations
		clientConnection.start();
		prevComboChoice = "Public";
		uniqueName = "";




		// All scene: Profile image view
		Image profile = new Image("App Icons/icon_profile.png");
		img_profile = new ImageView(profile);
		img_profile.setPreserveRatio(true);
		img_profile.setFitWidth(50);
		img_profile.setFitWidth(50);
		// All scene: Message image view
		Image message = new Image("App Icons/icon_message.png");
		img_message = new ImageView(message);
		img_message.setPreserveRatio(true);
		img_message.setFitWidth(50);
		img_message.setFitWidth(50);
		// All scene: Group image view
		Image group = new Image("App Icons/icon_group.png");
		img_group = new ImageView(group);
		img_group.setPreserveRatio(true);
		img_group.setFitWidth(50);
		img_group.setFitWidth(50);
		// All scene: Text scene hyperlink button
		button_messageIcon = new Button();
		button_messageIcon.setGraphic(img_message);
		button_messageIcon.setOnAction(e->{
			primaryStage.setScene(createTextItGui());
		});
		// All scene: Text scene hyperlink button
		button_profileIcon = new Button();
		button_profileIcon.setGraphic(img_profile);
		button_profileIcon.setOnAction(e->{
			primaryStage.setScene(createProfileGui());
		});
		// All scene: Text scene hyperlink button
		button_groupIcon = new Button();
		button_groupIcon.setGraphic(img_group);
		button_groupIcon.setOnAction(e->{
			primaryStage.setScene(createNewGroupGui());
		});



		// Username Scene: title label
		label_title = new Label("Welcome to text-it!");
		label_title.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 50;" +
				"-fx-font-family: 'gg sans Bold';");
		// Username Scene: question below title label
		label_question = new Label("What should we name you today?");
		label_question.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 20;" +
				"-fx-font-family: 'gg sans Medium';");
		// Username Scene: Username text field
		text_username = new TextField();
		text_username.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 16;" +
				"-fx-font-family: 'gg sans Medium';" +
				"-fx-max-width: 250;" +
				"-fx-alignment: center");
		// Username Scene: Username button
		button_username = new Button("name-it!");
		button_username.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 20;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-background-color: #CF9FFF;");
		button_username.setOnAction(e->{
			clientConnection.send(new Message(text_username.getText(), "",
					"", "isCheckUniqueName"));
		});



		// Text it Scene: text it title label
		label_textIt = new Label("text-it!");
		label_textIt.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 50;" +
				"-fx-font-family: 'gg sans Bold';" +
				"-fx-alignment: center");
		// Text it Scene: send combo box
		choiceBox_send = new ChoiceBox<>();
		choiceBox_send.setStyle("-fx-font-family: 'gg sans Medium';" +
				"-fx-font-size: 15;" +
				"-fx-font-style: italic;");
		// Text it Scene: Chat text field
		text_chat = new TextField();
		text_chat.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 15;" +
				"-fx-font-family: 'gg sans Medium';" +
				"-fx-pref-width: 250");
		// Text it Scene: Send button
		button_send = new Button("send-it! ");
		button_send.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 15;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-background-color: #CF9FFF;");
		button_send.setOnAction(e->{
			Message newMessage;

			if (Objects.equals(choiceBox_send.getValue(), "Public")) {
				newMessage = new Message(clientName, null,
						text_chat.getText(), "isPublicText");
			}
			else {
				String name = choiceBox_send.getValue();
				String extractedName = choiceBox_send.getValue().substring(
						name.indexOf(":") + 2);
				newMessage = new Message(clientName, extractedName,
						text_chat.getText(), "isPrivateText");
			}
			prevComboChoice = choiceBox_send.getValue();
			clientConnection.send(newMessage);
			text_chat.clear();
		});
		// Text it scene: Text Log List
		list_messages = new ListView<Label>();
		list_messages.setMaxHeight(200);




		// Profile Scene: Profile pic image view
		Image pp = new Image("App Icons/icon_profilePic.png");
		img_profilePic = new ImageView(pp);
		img_profilePic.setPreserveRatio(true);
		img_profilePic.setFitWidth(70);
		img_profilePic.setFitWidth(70);
		// Profile Scene: Profile title label
		label_profile = new Label();
		label_profile.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 40;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-alignment: center");
		// Profile Scene: Profile List
		list_profiles = new ListView<Label>();
		list_profiles.setMaxHeight(150);
		// Profile Scene: Group List
		list_groups = new ListView<Label>();
		list_groups.setMaxHeight(150);
		// Profile Scene: Friend list Label
		label_friendsList = new Label("your Friends:");
		label_friendsList.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 20;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-alignment: center");
		// Profile Scene: Group list Label
		label_groupList = new Label("our Groups:");
		label_groupList.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 20;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-alignment: center");




		// New Group Scene: Group text field
		text_groupName = new TextField();
		text_groupName.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 15;" +
				"-fx-font-family: 'gg sans Medium';" +
				"-fx-pref-width: 250");
		// New Group Scene: Group confirm button
		button_groupName = new Button("group-it?");
		button_groupName.setStyle("-fx-text-fill: black;" +
				"-fx-font-size: 15;" +
				"-fx-font-family: 'gg sans Semibold';" +
				"-fx-background-color: #CF9FFF;");
		button_groupName.setOnAction(e->{
			String groupName = text_groupName.getText();
			for (RadioButton radio : list_groupChooseFriend.getItems()) {
                if (radio.isSelected()) {
					try {
						clientConnection.send(new Message(radio.getText(), "",
								groupName, "isNewGroupAddMember"));
					} catch (Exception temp) {}
                }
            }
			primaryStage.setScene(createProfileGui());
		});
		// New Group Scene: List of friends to choose from to make a group
		list_groupChooseFriend = new ListView<>();
		list_groups.setMaxHeight(150);








		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

		// Set initial scene
		primaryStage.setScene(createUsernameGui());
		primaryStage.setTitle("Text-it");
		primaryStage.show();
	}
	

	/*
	Username scene code
	 */
	public Scene createUsernameGui() {

		VBox rowUsername = new VBox(20, label_question, text_username);
		rowUsername.setAlignment(Pos.CENTER);
		rowUsername.setStyle("-fx-background-color: #9FE2BF;" +
				"-fx-padding: 20;");

		VBox rowCenter = new VBox(100, label_title, rowUsername, button_username);
		rowCenter.setAlignment(Pos.CENTER);

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setCenter(rowCenter);
		pane.setStyle("-fx-background-color: #DDF6E9");

		return new Scene(pane, 600, 600);
	}

	/*
	Text-it scene code
	 */
	public Scene createTextItGui() {

		HBox colText = new HBox(10, choiceBox_send, text_chat, button_send);
		colText.setAlignment(Pos.CENTER);
		HBox colApps = new HBox(100, button_profileIcon, button_messageIcon, button_groupIcon);
		colApps.setAlignment(Pos.CENTER);

		VBox rowUp = new VBox(30, label_textIt, colText, list_messages);
		rowUp.setAlignment(Pos.CENTER);



		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(50));
		pane.setTop(rowUp);
		pane.setBottom(colApps);
		pane.setStyle("-fx-background-color: #DDF6E9");

		return new Scene(pane, 600, 600);
	}

	/*
	Profile scene code
	 */
	public Scene createProfileGui() {

		label_profile.setText("It's, " + clientName + "!");

		HBox colApps = new HBox(100, button_profileIcon, button_messageIcon, button_groupIcon);
		colApps.setAlignment(Pos.CENTER);

		VBox rowProfile = new VBox(5, img_profilePic, label_profile);
		rowProfile.setAlignment(Pos.CENTER);

		VBox rowFriendList = new VBox(5, label_friendsList, list_profiles);
		rowFriendList.setAlignment(Pos.CENTER);

		VBox rowGroupList = new VBox(5, label_groupList, list_groups);
		rowGroupList.setAlignment(Pos.CENTER);

		HBox colLists = new HBox(40, rowFriendList, rowGroupList);
		colLists.setAlignment(Pos.CENTER);

		VBox rowUp = new VBox(40, rowProfile, colLists);
		rowUp.setAlignment(Pos.TOP_CENTER);


		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(50));
		pane.setCenter(rowUp);
		pane.setBottom(colApps);
		pane.setStyle("-fx-background-color: #DDF6E9");

		return new Scene(pane, 600, 600);
	}

	/*
	New Group scene code
	 */
	public Scene createNewGroupGui() {

		Label label = new Label("New group");

		HBox colApps = new HBox(10, button_profileIcon, button_messageIcon, button_groupIcon);


		VBox rowCenter = new VBox(10, label,  text_groupName,
				button_groupName, list_groupChooseFriend, colApps);


		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(50));
		pane.setCenter(rowCenter);

		return new Scene(pane, 600, 600);
	}

}
