package org.sim.umira.minio;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;


@ApplicationScoped
public class MinioServices {

    @Inject
    MinioClient minioClient;

    @ConfigProperty(name = "minio.bucket")
    String bucketName;

     public List<String> listObjects() throws Exception {

        List<String> objects = new ArrayList<>();

        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .recursive(true)
                                .build()
                );

        for (Result<Item> result : results) {
            Item item = result.get();
            objects.add(item.objectName());
        }

        return objects;
    }


    void onStart(@Observes StartupEvent ev) {
        //  System.out.println(minioClient.);
        try {

              minioClient.listBuckets()
                .forEach(bucket ->
                        System.out.println(bucket.name()));

        System.out.println("MinIO Connected");

            Log.info("MinIO connected");
            // Log.info("Bucket exists = " + exists);

        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Failed connect to MinIO", e);

        }
    }


    
}
