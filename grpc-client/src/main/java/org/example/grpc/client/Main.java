package org.example.grpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.grpc.example.Customer;
import com.grpc.example.CustomerServiceGrpc;
import com.grpc.example.CustomersResponse;
import com.grpc.example.FirstName;
import com.grpc.example.Id;
import com.grpc.example.LastName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final String target = "localhost:9999";
    private static final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
    private static final CustomerServiceGrpc.CustomerServiceBlockingStub blockingStub =
            CustomerServiceGrpc.newBlockingStub(channel);
    private static final CustomerServiceGrpc.CustomerServiceStub stub =
            CustomerServiceGrpc.newStub(channel);
    private static final CustomerServiceGrpc.CustomerServiceFutureStub futureStub =
            CustomerServiceGrpc.newFutureStub(channel);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("--------------------getCustomerById--------------------");
        System.out.println(blockingStub.getCustomerById(Id.newBuilder().setId("1").build()));
        System.out.println("--------------------getCustomersByFirstName--------------------");
        getCustomersByFirstName();
        System.out.println("--------------------createCustomer--------------------");
        System.out.println(blockingStub.createCustomer(Customer.newBuilder().setId(Id.newBuilder().setId("new")).build()));

        // asyncStub
        System.out.println("Async stub");
        System.out.println("--------------------getCustomerById--------------------");
        stub.getCustomerById(Id.newBuilder().setId("1").build(), new StreamObserver<Customer>() {
            @Override
            public void onNext(Customer customer) {
                System.out.println("getCustomerById: " + customer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred in getCustomerById.");
            }

            @Override
            public void onCompleted() {
                System.out.println("getCustomerById completed.");
            }
        });
        System.out.println("--------------------getCustomersByFirstName--------------------");
        stub.getCustomersByFirstName(FirstName.newBuilder().setFirstName("Egor").build(), new StreamObserver<Customer>() {
            @Override
            public void onNext(Customer customer) {
                System.out.println("getCustomersByFirstName: " + customer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred in getCustomerById.");
            }

            @Override
            public void onCompleted() {
                System.out.println("getCustomersByFirstName completed.");
            }
        });
        System.out.println("--------------------getCustomersByLastName--------------------");
        StreamObserver<LastName> requestStream = stub.getCustomersByLastName(new StreamObserver<CustomersResponse>() {
            @Override
            public void onNext(CustomersResponse customersResponse) {
                System.out.println("getCustomersByLastName: " + customersResponse.getCustomersList());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred in getCustomersByLastName.");
            }

            @Override
            public void onCompleted() {
                System.out.println("getCustomersByLastName completed.");
            }
        });

        requestStream.onNext(LastName.newBuilder().setLastName("Skorupich").build());
        requestStream.onNext(LastName.newBuilder().setLastName("Krukovich").build());
        requestStream.onNext(LastName.newBuilder().setLastName("Test").build());
        requestStream.onCompleted();

        System.out.println("--------------------getCustomersByIds--------------------");
        StreamObserver<Id> idStreamObserver = stub.getCustomersByIds(new StreamObserver<Customer>() {
            @Override
            public void onNext(Customer customer) {
                System.out.println("getCustomersByIds: " + customer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error occurred in getCustomersByIds.");
            }

            @Override
            public void onCompleted() {
                System.out.println("getCustomersByIds completed.");
            }
        });

        idStreamObserver.onNext(Id.newBuilder().setId("1").build());
        idStreamObserver.onNext(Id.newBuilder().setId("2").build());
        idStreamObserver.onNext(Id.newBuilder().setId("3").build());
        idStreamObserver.onNext(Id.newBuilder().setId("4").build());
        idStreamObserver.onNext(Id.newBuilder().setId("5").build());
        idStreamObserver.onNext(Id.newBuilder().setId("test").build());

        stub.createCustomer(Customer.newBuilder()
                        .setId(Id.newBuilder().setId("test"))
                        .setFirstName(FirstName.newBuilder().setFirstName("Egor"))
                        .build(),
                new StreamObserver<Customer>() {
                    @Override
                    public void onNext(Customer customer) {
                        System.out.println("createCustomer: " + customer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error occurred in createCustomer.");
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("createCustomer completed.");
                    }
                });

        System.out.println("--------------------futureStub--------------------");
        ListenableFuture<Customer> getByIdFuture =
                futureStub.getCustomerById(Id.newBuilder().setId("1").build());
        ListenableFuture<Customer> createCustomerFuture =
                futureStub.createCustomer(Customer.newBuilder().setAddress("testAddress").build());

        System.out.println("--------------------getByIdFuture--------------------");
        System.out.println("getByIdFuture: " + getByIdFuture.get());

        System.out.println("--------------------createCustomerFuture--------------------");
        System.out.println("createCustomerFuture: " + createCustomerFuture.get());

        System.in.read();
        channel.shutdown();
    }

    private static void getCustomersByFirstName() {
        Iterator<Customer> customerIterator =
                blockingStub.getCustomersByFirstName(FirstName.newBuilder().setFirstName("Egor").build());
        while (customerIterator.hasNext()) {
            System.out.println(customerIterator.next());
        }
    }
}
