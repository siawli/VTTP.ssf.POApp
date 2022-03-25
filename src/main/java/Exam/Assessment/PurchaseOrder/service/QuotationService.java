package Exam.Assessment.PurchaseOrder.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import Exam.Assessment.PurchaseOrder.model.Quotation;
import Exam.Assessment.PurchaseOrder.model.UserInput;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class QuotationService {
    private Logger logger = Logger.getLogger(QuotationService.class.getName());
    UserInput userInput = new UserInput();

    public JsonObject changeStringToJson(String data) {
        JsonObject dataNeeded = null;
        try (InputStream is = new ByteArrayInputStream(data.getBytes())) {
            JsonReader reader = Json.createReader(is);
            dataNeeded = reader.readObject();
        } catch (IOException ex) {
            logger.warning(ex.getMessage());
            System.exit(1);
        }

        return dataNeeded;
    }

    public List<String> obtainItemsFromSubmission(String orderSubmission) {
        JsonObject order = changeStringToJson(orderSubmission);
        List<String> items = new LinkedList<>();
        JsonArray lineItems = order.getJsonArray("lineItems");
        for (JsonValue item : lineItems) {
            String fruit = item.asJsonObject().getString("item");
            items.add(fruit);
        }

        return items;
    }

    public UserInput obtainUserInput(String orderSubmission) {
        JsonObject order = changeStringToJson(orderSubmission);
        JsonArray lineItems = order.getJsonArray("lineItems");
        for (JsonValue item : lineItems) {
            Integer fruitQuantity = item.asJsonObject().getInt("quantity");
            userInput.getQuantity().add(fruitQuantity);
        }
        userInput.setName(order.getString("name"));

        return userInput;
    }
    

    public Optional<Quotation> getQuotations(List<String> items) {

        JsonArrayBuilder itemsJsonB = Json.createArrayBuilder();

        for (String fruit : items) {
            itemsJsonB.add(fruit);
        }

        JsonArray itemsJsonArr = itemsJsonB.build();

        String url = "https://quotation.chuklee.com/quotation";
        RequestEntity<String> req = RequestEntity
                                    .post(url)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .header("Accept", "application/json")
                                    .body(itemsJsonArr.toString(), String.class);

        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp = template.exchange(req, String.class);
            JsonObject jsonObjFromQSys = changeStringToJson(resp.getBody());

            Quotation quotation = new Quotation();
            quotation.setQuoteId(jsonObjFromQSys.getString("quoteId"));
            JsonArray quotationsFruits = jsonObjFromQSys.getJsonArray("quotations");

            for (JsonValue fruit: quotationsFruits) {
                String eachFruit = fruit.asJsonObject().getString("item");
                JsonValue unitPrice = fruit.asJsonObject().get("unitPrice");
                quotation.addQuotation(eachFruit, Float.parseFloat(unitPrice.toString()));
            }
            return Optional.of(quotation);
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return Optional.empty();
        }
    }        
}
