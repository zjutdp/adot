package com.example.myapp;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

import io.opentelemetry.extension.annotations.SpanAttribute;
import io.opentelemetry.extension.annotations.WithSpan;

/**
 * Hello world!
 *
 */
public class App {

    public static void listAllTables(DynamoDbClient ddb) {

        System.out.println("DynamoDB Tables:");

        boolean moreTables = true;
        String lastName = null;

        while (moreTables) {
            try {
                ListTablesResponse response = null;
                if (lastName == null) {
                    ListTablesRequest request = ListTablesRequest.builder().build();
                    response = ddb.listTables(request);
                } else {
                    ListTablesRequest request = ListTablesRequest.builder().exclusiveStartTableName(lastName).build();
                    response = ddb.listTables(request);
                }

                List<String> tableNames = response.tableNames();

                if (tableNames.size() > 0) {
                    for (String curName : tableNames) {
                        System.out.format("* %s\n", curName);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                lastName = response.lastEvaluatedTableName();
                if (lastName == null) {
                    moreTables = false;
                }
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        System.out.println("Done!\n");
    }

    public static void listAllBuckets(S3Client s3) {

        System.out.println("S3 Buckets:");

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.format("* %s\n", x.name()));

        System.out.println("Done!\n");
    }

    @WithSpan
    public static void listAllBucketsAndTables(@SpanAttribute("title") String title, S3Client s3, DynamoDbClient ddb) {

        System.out.println(title);

        listAllBuckets(s3);
        listAllTables(ddb);

    }

    @WithSpan
    public static void listTablesFirstAndThenBuckets(@SpanAttribute("title") String title, S3Client s3, DynamoDbClient ddb) {

        System.out.println(title);

        listAllTables(ddb);
        listAllBuckets(s3);

    }

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;

        S3Client s3 = S3Client.builder().region(region).build();
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        listAllBucketsAndTables("My S3 buckets and DynamoDB tables", s3, ddb);
        listTablesFirstAndThenBuckets("My DynamoDB tables first and then S3 bucket", s3, ddb);

        s3.close();
        ddb.close();
    }
}