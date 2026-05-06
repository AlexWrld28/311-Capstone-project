package org.csc311.capstone.storage;

import io.github.cdimascio.dotenv.Dotenv;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

public class MinioStorageService {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory(System.getProperty("user.dir"))
            .ignoreIfMissing()
            .load();

    private static final String ENDPOINT = dotenv.get("MINIO_ENDPOINT");
    private static final String PUBLIC_URL = dotenv.get("MINIO_PUBLIC_URL", ENDPOINT);
    private static final String ACCESS_KEY = dotenv.get("MINIO_ACCESS_KEY");
    private static final String SECRET_KEY = dotenv.get("MINIO_SECRET_KEY");
    private static final String BUCKET = dotenv.get("MINIO_BUCKET", "staff-images");

    private static MinioClient client;

    public static String uploadStaffImage(File imageFile, String staffId) {
        if (imageFile == null) {
            throw new IllegalArgumentException("No image file selected.");
        }

        if (staffId == null || staffId.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing staff ID.");
        }

        validateConfig();

        try {
            ensureBucketExists();

            String extension = getExtension(imageFile.getName());
            String objectName = "staff/" + staffId + "/" + UUID.randomUUID() + extension;

            try (FileInputStream inputStream = new FileInputStream(imageFile)) {
                getClient().putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(objectName)
                                .stream(inputStream, imageFile.length(), -1)
                                .contentType(resolveContentType(extension))
                                .build()
                );
            }

            return PUBLIC_URL + "/" + BUCKET + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload staff image to MinIO: " + e.getMessage(), e);
        }
    }

    private static MinioClient getClient() {
        if (client == null) {
            client = MinioClient.builder()
                    .endpoint(ENDPOINT)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .build();
        }

        return client;
    }

    private static void ensureBucketExists() throws Exception {
        boolean exists = getClient().bucketExists(
                BucketExistsArgs.builder()
                        .bucket(BUCKET)
                        .build()
        );

        if (!exists) {
            getClient().makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(BUCKET)
                            .build()
            );
        }

        getClient().setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(BUCKET)
                        .config(publicReadPolicy())
                        .build()
        );
    }

    private static String publicReadPolicy() {
        return """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": "*",
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """.formatted(BUCKET);
    }

    private static void validateConfig() {
        if (isBlank(ENDPOINT)) {
            throw new RuntimeException("Missing MINIO_ENDPOINT in .env.");
        }

        if (isBlank(PUBLIC_URL)) {
            throw new RuntimeException("Missing MINIO_PUBLIC_URL in .env.");
        }

        if (isBlank(ACCESS_KEY)) {
            throw new RuntimeException("Missing MINIO_ACCESS_KEY in .env.");
        }

        if (isBlank(SECRET_KEY)) {
            throw new RuntimeException("Missing MINIO_SECRET_KEY in .env.");
        }

        if (isBlank(BUCKET)) {
            throw new RuntimeException("Missing MINIO_BUCKET in .env.");
        }
    }

    private static String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }

        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex < 0) {
            return "";
        }

        return fileName.substring(dotIndex).toLowerCase();
    }

    private static String resolveContentType(String extension) {
        return switch (extension.toLowerCase()) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}