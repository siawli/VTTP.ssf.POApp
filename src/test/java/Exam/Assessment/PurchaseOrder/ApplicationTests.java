package Exam.Assessment.PurchaseOrder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import Exam.Assessment.PurchaseOrder.model.Quotation;
import Exam.Assessment.PurchaseOrder.service.QuotationService;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

	@Autowired
	QuotationService quotationSvc;

	@Autowired
	MockMvc mvc;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnEmptyJsonObjectAndErrorCode() throws Exception {
		List<String> fruits = new LinkedList<>();
		fruits.add("durian");
		fruits.add("plum");
		fruits.add("pear");

		Optional<Quotation> result = quotationSvc.getQuotations(fruits);
		Assertions.assertTrue(result.isEmpty());

	}

}
