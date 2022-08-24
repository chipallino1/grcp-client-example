package org.example.grpc.client;

import com.grpc.example.Customer;
import com.grpc.example.CustomerServiceGrpc;
import com.grpc.example.FirstName;
import com.grpc.example.Id;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;
import java.util.List;

public class Main {
    private static final String target = "localhost:9999";
    private static final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    private static final CustomerServiceGrpc.CustomerServiceBlockingStub blockingStub =
            CustomerServiceGrpc.newBlockingStub(channel);
    private static final CustomerServiceGrpc.CustomerServiceStub stub =
            CustomerServiceGrpc.newStub(channel);
    private static final CustomerServiceGrpc.CustomerServiceFutureStub futureStub =
            CustomerServiceGrpc.newFutureStub(channel);

    public static void main(String[] args) {
        System.out.println("--------------------getCustomerById--------------------");
        System.out.println(blockingStub.getCustomerById(Id.newBuilder().setId("new").build()));
        System.out.println("--------------------getCustomersByFirstName--------------------");
        getCustomersByFirstName("Egor");
        System.out.println("--------------------createCustomer--------------------");
        System.out.println(
                blockingStub.createCustomer(Customer.newBuilder().setId(Id.newBuilder().setId("new")).build()));
    }

    private static void getCustomersByFirstName(String firstName) {
        Iterator<Customer> customerIterator =
                blockingStub.getCustomersByFirstName(FirstName.newBuilder().setFirstName(firstName).build());
        while (customerIterator.hasNext()) {
            System.out.println(customerIterator.next());
        }
    }
}
