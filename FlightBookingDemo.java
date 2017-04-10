import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class FlightBookingDemo {
	public static String WORKSPACE_ID = "your Watson Conversation workspace id";

	public static final String FROM_CITY_KEY = "FromCity";
	public static final String TO_CITY_KEY = "ToCity";
	public static final String DATE_KEY = "Date";
	public static final String TIME_KEY = "Time";

	public static void main(String[] args) throws Exception {
		ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);
		service.setUsernameAndPassword("your Watson Conversation username", "your Watson Conversation password");

		String messageFromUser = "";
		Map<String, Object> context = null;
		boolean endConversation = false;
		do {
			MessageRequest request = new MessageRequest.Builder().inputText(messageFromUser).context(context).build();
			MessageResponse response = service.message(WORKSPACE_ID, request).execute();
			context = response.getContext();
			Map<String, Object> output = response.getOutput();
			List<String> responseText = (List<String>) output.get("text");
			if (responseText.size() > 0) {
				System.out.println("Bot: " + responseText.get(0));
			}
			if (output.get("action") != null) {
				String actionString = (String) output.get("action");
				if ("bookFlight".equalsIgnoreCase(actionString)) {
					bookFlight(context);
					context = null;
					endConversation = true;
				} else if ("end_conversation".equalsIgnoreCase(actionString)) {
					endConversation = true;
				}
			}
			if (!endConversation) {
				messageFromUser = getUserInput();
			}
		} while (!endConversation);
		String exitMessage = "Exiting program. Have a nice day!";
		System.out.println(exitMessage);
	}

	private static void bookFlight(Map<String, Object> context) throws InterruptedException {
		String fromAirport = (String) context.get(FROM_CITY_KEY);
		String toAirport = (String) context.get(TO_CITY_KEY);
		String departureDate = (String) context.get(DATE_KEY);
		String departureTiime = (String) context.get(TIME_KEY);
		String responseText = "(Calling backend API to search for flights from " + fromAirport + " to " + toAirport
				+ " on " + departureDate + " starting at " + departureTiime + ")";
		System.out.println(responseText);
	}

	static String getUserInput() {
		System.out.print("User: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			return br.readLine().trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}