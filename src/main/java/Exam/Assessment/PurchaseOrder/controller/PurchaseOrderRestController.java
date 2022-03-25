package Exam.Assessment.PurchaseOrder.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import Exam.Assessment.PurchaseOrder.model.Quotation;
import Exam.Assessment.PurchaseOrder.model.UserInput;
import Exam.Assessment.PurchaseOrder.service.QuotationService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@RestController
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService quotationSvc;
    
    @PostMapping("/api/po")
    public ResponseEntity<String> getPurchaseOrder
            (@RequestBody String orderSubmission) {

        List<String> items = quotationSvc.obtainItemsFromSubmission(orderSubmission);
        Optional<Quotation> jsonRequestObtained = quotationSvc.getQuotations(items);

        if (jsonRequestObtained.isPresent()) {
            Quotation quotation = jsonRequestObtained.get();
            UserInput userInput = quotationSvc.obtainUserInput(orderSubmission);
            List<Integer> quantity = userInput.getQuantity();

            Float total = (float) 0;
            for (int i=0; i<items.size(); i++) {
                total += quotation.getQuotation(items.get(i)) * quantity.get(i);
                // System.out.println(">>> fruit " + items.get(i));
                // System.out.println(">>> quantity " + quantity.get(i));
                // System.out.println(">>> total " + total);
            }
            // System.out.println(">>>>>>>>> total" + total);

            JsonObject jsonObjToReturn = Json.createObjectBuilder()
                                                .add("invoiceId", quotation.getQuoteId())
                                                .add("name", userInput.getName())
                                                .add("total", total)
                                                .build();

            return ResponseEntity.ok(jsonObjToReturn.toString());
        } else {
            JsonObjectBuilder bodyB = Json.createObjectBuilder();
            JsonObject bodyObj = bodyB.build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bodyObj.toString());
        }
    }
}
