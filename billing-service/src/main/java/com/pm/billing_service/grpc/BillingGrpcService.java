package com.pm.billing_service.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(billing.BillingRequest billingRequest,
            StreamObserver<billing.BillingResponse> responseObserver) {
        log.info("createBillingAccount request recieved {}", billingRequest.toString());

        // business logic to save to database or perform calculations etc

        BillingResponse response = BillingResponse.newBuilder().setAccountId("1234").setStatus("ACTIVE").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
