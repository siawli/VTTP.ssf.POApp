package Exam.Assessment.PurchaseOrder.model;

import java.util.LinkedList;
import java.util.List;

public class UserInput {
    private List<Integer> quantity = new LinkedList<>();
    private String name;

    public List<Integer> getQuantity() {
        return quantity;
    }
    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    
}
