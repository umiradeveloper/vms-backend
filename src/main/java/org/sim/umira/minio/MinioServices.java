package org.sim.umira.minio;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class MinioServices {

    @Inject
    MinioClient minioClient;

    @Inject
    MinioAsyncClient minioSyncClient;

    @Inject
    MinioLogServices logServices;


    @ConfigProperty(name = "minio.bucket")
    String bucketName;

    public List<String> listObjects() throws Exception {

        List<String> objects = new ArrayList<>();

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            objects.add(item.objectName());
        }

        return objects;
    }

    public InputStream getFile(String url) {
        long start = System.currentTimeMillis();
        try {
            
            logServices.save(
                "DOWNLOAD",
                bucketName,
                url,
                null,
                System.currentTimeMillis() - start,
                "SUCCESS",
                "File downloaded",
                null
            );

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(url)
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
            logServices.save(
                "DOWNLOAD",
                bucketName,
                url,
                null,
                System.currentTimeMillis() - start,
                "FAILED",
                "File downloaded",
                e.getMessage()
            );
            // throw new InternalError("Can't get file");
            throw new WebApplicationException(
                    "File not found",
                    Response.Status.NOT_FOUND);
        }
    }

    public void uploadFile(String path_dir, String filename, InputStream file, long size) throws Exception {
     
        long start = System.currentTimeMillis();

        try {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path_dir + "/" + filename)
                            .stream(file, size, -1)
                            .build());

            logServices.save(
                    "UPLOAD",
                    bucketName,
                    path_dir + "/" + filename,
                    size,
                    System.currentTimeMillis() - start,
                    "SUCCESS",
                    "File uploaded successfully",
                    null);

        } catch (Exception e) {

            logServices.save(
                    "UPLOAD",
                    bucketName,
                    path_dir + "/" + filename,
                    size,
                    System.currentTimeMillis() - start,
                    "FAILED",
                    "Upload failed",
                    e.getMessage());

            throw e;
        }

    }

     public void deleteFile(String objectName) {
        long start = System.currentTimeMillis();
        try {

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()
            );
            logServices.save(
                    "DELETE",
                    bucketName,
                    objectName,
                    null,
                    System.currentTimeMillis() - start,
                    "SUCCESS",
                    "File delete successfully",
                    null);

        } catch (Exception e) {
            logServices.save(
                    "DELETE",
                    bucketName,
                    objectName,
                    null,
                    System.currentTimeMillis() - start,
                    "FAILED",
                    "File delete successfully",
                    e.getMessage());
           
        }
    }

    void onStart(@Observes StartupEvent ev) {
        // System.out.println(minioClient.);
        try {

            minioClient.listBuckets()
                    .forEach(bucket -> System.out.println(bucket.name()));

            System.out.println("MinIO Connected");

            Log.info("MinIO connected");
            // Log.info("Bucket exists = " + exists);

        } catch (Exception e) {
            e.printStackTrace();
            Log.error("Failed connect to MinIO", e);

        }
    }

}
